package crawler;

import dto.Laptop;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utilities.XMLUtilities;

/**
 *
 * @author dangxuananh1997
 */
public class HangchinhhieuCrawler implements CrawlerInterface {

    private final String siteUrl = "https://hangchinhhieu.vn";
    private final String laptopPath = "/collections/laptop";
    private final String mousePath = "/collections/chuot";
    private final String keyboardPath = "/collections/ban-phim";
    private final String headsetPath = "/collections/tai-nghe";
    
    public HangchinhhieuCrawler() {
    }
    
    private String getPaginationDomString(String url) {
        try {
            BufferedReader reader = XMLUtilities.getBufferedReaderFromURL(url);
            String line;
            String document = "";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<ul class=\"pagination\">")) {
                    isStart = true;
                }
                // replace entity &-; with a-z
                if (isStart && line.contains("&") && line.contains(";")) {
                    line = line.replaceAll("&", "a").replaceAll(";", "z");
                }
                // close tag ul
                if (isStart && line.contains("</ul>")) {
                    line = line.substring(0, line.indexOf("</ul>")) + "</ul>";
                    document += line.trim();
                    break;
                }
                if (isStart && !line.trim().isEmpty()) {
                    document += line.trim();
                }
            }
            return document;
        } catch (IOException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private int getLastPageNumber(String url) {
        int pageNum = 1;
        try {
            String domString = getPaginationDomString(url);
            if (!domString.isEmpty()) {
                Document document = XMLUtilities.parseStringToDom(domString);
                XPath xPath = XMLUtilities.getXPath();
                String lastPageNum = (String) xPath.evaluate("//li[last()-1]/a", document, XPathConstants.STRING);
                if (lastPageNum != null) {
                    pageNum = Integer.parseInt(lastPageNum);
                }
            }
        } catch (NumberFormatException | XPathExpressionException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pageNum;
    }
    
    private String getProductDomString(String url) {
        try {
            BufferedReader reader = XMLUtilities.getBufferedReaderFromURL(url);
            String line;
            String document = "";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<section id=\"insCollectionPage\">")) {
                    isStart = true;
                }
                // replace entity &-; with a-z
                if (isStart && line.contains("&") && line.contains(";")) {
                    line = line.replaceAll("&", "a").replaceAll(";", "z");
                }
                // add img closing tag
                if (isStart && line.contains("<img")) {
                    line += "</img>";
                }
                // remove data-price with '<' line
                if (isStart && line.contains("data-price")) {
                    line = "";
                }
                if (isStart && !line.trim().isEmpty()) {
                    document += line.trim();
                }
                if (isStart && line.contains("</section>")) {
                    break;
                }
            }
            return document;
        } catch (IOException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private List<Laptop> getAllProductLink(String url) {
        List<Laptop> productArray = new LinkedList<>();
        try {
            int lastPageNumber = getLastPageNumber(url);
            for (int page = 1; page <= lastPageNumber; page++) {
                String domString = getProductDomString(url + "?page=" + page);
                if (!domString.isEmpty()) {
                    Document document = XMLUtilities.parseStringToDom(domString);
                    XPath xPath = XMLUtilities.getXPath();
                    NodeList productList = (NodeList) xPath.evaluate("//*[@id=\"pd_collection\"]/ul/li", document, XPathConstants.NODESET);
                    if (productList != null) {
                        for (int p = 0; p < productList.getLength(); p++) {
                            Node productNode = productList.item(p);
                            Node productLinkNode = (Node) xPath.evaluate(".//a[@class=\"productName\"]", productNode, XPathConstants.NODE);
                            String productLink = productLinkNode.getAttributes().getNamedItem("href").getTextContent().trim();
                            String productName = productLinkNode.getAttributes().getNamedItem("title").getTextContent().trim();
                            String productPriceNode = (String) xPath.evaluate(".//p[@class=\"pdPrice\"]/span", productNode, XPathConstants.STRING);
                            System.out.println(productLink);
                            System.out.println(productName);
                            System.out.println(productPriceNode);
                            
                            // add laptop to array
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productArray;
    }
    
    private String getInfoTableDomString(String url) {
        try {
            BufferedReader reader = XMLUtilities.getBufferedReaderFromURL(url);
            String line;
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<table")) {
                    int openTagPos = line.indexOf("<table");
                    int closeTagPos = line.indexOf("</table>") > 0
                            ? line.indexOf("</table>")
                            : line.length();
                    line = line.substring(openTagPos, closeTagPos) + "</table>";
                    
                    // replace entity &nbsp; with space
                    if (line.contains("&nbsp;")) {
                        line = line.replaceAll("&nbsp;", " ");
                    }
                    
                    if (closeTagPos > 0) {
                        return line.trim();
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private Map<String, String> getInfoTableMap(String tableDomString) {
        Map<String, String> table = new Hashtable<>();
        try {
            XMLStreamReader reader = XMLUtilities.parseStringToXMLStreamReader(tableDomString);
            String tmpKey = null;
            while (reader.hasNext()) {
                int cursor = reader.next();
                if (cursor == XMLStreamReader.START_ELEMENT) {
                    String tagName = reader.getLocalName();
                    if (tagName.equals("span")) {
                        if (tmpKey == null) {
                            tmpKey = reader.getElementText();
                        } else {
                            table.put(tmpKey, reader.getElementText());
                            tmpKey = null;
                        }
                    }
                }
            }
        } catch (XMLStreamException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }
    
    private Laptop parseLaptop(String tableDomString) {
        try {
            Map<String, String> table = getInfoTableMap(tableDomString);
            String cpu = table.get("CPU");
            String gpu = table.get("Card đồ họa");
            String ram = table.get("RAM");
            String hardDrive = table.get("Ổ cứng");
            String monitor = table.get("Màn hình");
            String ports = table.get("Cổng giao tiếp");
            String lan = table.get("Chuẩn LAN");
            String wireless = table.get("Chuẩn WIFI") + ", " + table.get("Bluetooth");
            
            return null;
        } catch (Exception e) {
            
        }
        return null;
    }
    
    @Override
    public void crawlLaptop() {
//        String laptopDomString = getInfoTableDomString("https://hangchinhhieu.vn/products/msi-gt83-titan-8rg-037vn");
//        System.out.println(parseLaptop(laptopDomString));
        getAllProductLink("https://hangchinhhieu.vn/collections/laptop");
    }

    @Override
    public void crawlMouse() {
        
    }

    @Override
    public void crawlKeyboard() {
        
    }

    @Override
    public void crawlHeadset() {
        
    }
    
}
