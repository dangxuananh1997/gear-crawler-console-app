package crawler;

import dto.Keyboard;
import dto.Laptop;
import dto.Mouse;
import dto.Product;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utilities.CommonUtilities;
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
        System.out.print("get last page");
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
        System.out.println(" - done");
        return pageNum;
    }
    
    private String getProductListDomString(String url) {
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
                    line = CommonUtilities.addCloseTagToLine(line, "img");
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
    
    private List<Product> getAllDraftProducts(String url) {
        System.out.println("get all draft products");
        List<Product> productArray = new LinkedList<>();
        try {
            int lastPageNumber = getLastPageNumber(url);
            for (int page = 1; page <= lastPageNumber; page++) {
                String domString = getProductListDomString(url + "?page=" + page);
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
                            String productPrice = (String) xPath.evaluate(".//p[@class=\"pdPrice\"]/span", productNode, XPathConstants.STRING);
                            Node productImageNode = (Node) xPath.evaluate(".//div[@class=\"image-product\"]/a/img", productNode, XPathConstants.NODE);
                            String productImage = productImageNode.getAttributes().getNamedItem("src").getTextContent().trim();
                            
                            Product product = new Product(productName, productImage, CommonUtilities.convertPriceHangchinhhieu(productPrice), productLink);
                            productArray.add(product);
                            System.out.print("+");
                        }
                    }
                }
            }
        } catch (XPathExpressionException | DOMException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\nget all draft products - done");
        return productArray;
    }
    
    private String getInfoTableDomString(String url) {
        try {
            BufferedReader reader = XMLUtilities.getBufferedReaderFromURL(url);
            String line;
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
                    
                    // add br closing tag
                    if (line.contains("<br")) {
                        line = CommonUtilities.addCloseTagToLine(line, "br");
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
        Map<String, String> table = new HashMap<>();
        try {
            XMLStreamReader reader = XMLUtilities.parseStringToXMLStreamReader(tableDomString);
            String tmpKey = null;
            while (reader.hasNext()) {
                int cursor = reader.next();
                if (cursor == XMLStreamReader.START_ELEMENT) {
                    String tagName = reader.getLocalName();
                    if (tagName.equals("td")) {
                        // traverse until meet text node
                        while (!reader.hasText() && reader.hasNext()) {
                            reader.next();
                        }
                        if (reader.hasNext()) {
                            if (tmpKey == null) {
                                tmpKey = reader.getText().trim();
                            } else {
                                table.put(tmpKey, reader.getText().trim());
                                tmpKey = null;
                            }
                        }
                    }
                }
            }
        } catch (XMLStreamException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }
    
    private Laptop parseLaptop(String tableDomString, Product product) {
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
            Laptop laptop = new Laptop(cpu, gpu, ram, hardDrive, monitor, ports, lan, wireless, product);
            return laptop;
        } catch (Exception ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public void crawlLaptop() {
        List<Product> productList = getAllDraftProducts(siteUrl + laptopPath);
        for (Product product : productList) {
            String tableDomString = getInfoTableDomString(siteUrl + product.getProductLink());
            Laptop laptop = parseLaptop(tableDomString, product);
            System.out.println(laptop);
        }
    }

    private Mouse parseMouse(String tableDomString, Product product) {
        try {
            Map<String, String> table = getInfoTableMap(tableDomString);
            String weight = table.get("Cân nặng");
            String maxDPI = table.get("DPI (Tối thiểu / Tối đa)");
            String led = table.get("LED");
            String numberOfButton = table.get("Số nút");
            Mouse mouse = new Mouse(weight, maxDPI, led, numberOfButton, product);
            return mouse;
        } catch (NumberFormatException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public void crawlMouse() {
        List<Product> productList = getAllDraftProducts(siteUrl + mousePath);
        for (Product product : productList) {
            String tableDomString = getInfoTableDomString(siteUrl + product.getProductLink());
            Mouse mouse = parseMouse(tableDomString, product);
            System.out.println(mouse);
        }
    }

    private Keyboard parseKeyboard(String tableDomString, Product product) {
        try {
            Map<String, String> table = getInfoTableMap(tableDomString);
            String numberOfKey = table.get("Số nút");
            String pressForce = table.get("Lực nhấn");
            String distance = table.get("Khoảng cách hành trình");
            String led = table.get("Đền nền");
            String weight = table.get("Trọng lượng");
            String size = table.get("Kích thước");
            String switches = table.get("Switch");
            Keyboard keyboard = new Keyboard(numberOfKey, pressForce, distance, led, weight, size, switches, product);
            return keyboard;
        } catch (NumberFormatException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void crawlKeyboard() {
        List<Product> productList = getAllDraftProducts(siteUrl + keyboardPath);
        for (Product product : productList) {
            String tableDomString = getInfoTableDomString(siteUrl + product.getProductLink());
            Keyboard keyboard = parseKeyboard(tableDomString, product);
            System.out.println(keyboard);
        }
    }

    @Override
    public void crawlHeadset() {
        
    }
    
}
