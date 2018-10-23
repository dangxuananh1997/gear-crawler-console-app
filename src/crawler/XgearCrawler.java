package crawler;

import dto.Keyboard;
import dto.Laptop;
import dto.Mouse;
import dto.Product;
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
public class XgearCrawler implements CrawlerInterface {
    
    private final String siteUrl = "https://xgear.vn";
    private final String[] laptopPath = {
        "/danh-muc/laptop-msi",
        "/danh-muc/laptop-asus/",
        "/danh-muc/laptop-dell/",
        "/danh-muc/laptop-acer/",
    };
    private final String mousePath = "/danh-muc/mouse";
    private final String keyboardPath = "/danh-muc/keyboard";
    private final String headsetPath = "/danh-muc/headset-tai-nghe";
    
    public XgearCrawler() {
    }

    private String getPaginationDomString(String url) {
        try {
            BufferedReader reader = XMLUtilities.getBufferedReaderFromURL(url);
            String line;
            String document = "";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<ul class='page-numbers'>")) {
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
            Logger.getLogger(XgearCrawler.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(XgearCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(" - done: " + pageNum);
        return pageNum;
    }
    
    private String getProductListDomString(String url) {
        try {
            BufferedReader reader = XMLUtilities.getBufferedReaderFromURL(url);
            String line;
            String document = "";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("class=\"products-loop")) {
                    isStart = true;
                }
                // replace entity &#8363; with đ
                if (isStart && line.contains("&#8363;")) {
                    line = line.replaceAll("&#8363;", "đ");
                }
                // replace entity &-; with a-z
                if (isStart && line.contains("&") && line.contains(";")) {
                    line = line.replaceAll("&", "a").replaceAll(";", "z");
                }
                // add img closing tag
                if (isStart && line.contains("<img")) {
                    line = CommonUtilities.addCloseTagToLine(line, "img");
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
            Logger.getLogger(XgearCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private List<Product> getAllDraftProducts(String url) {
        System.out.println("get all draft products");
        List<Product> productArray = new LinkedList<>();
        try {
            int lastPageNumber = getLastPageNumber(url);
            for (int page = 1; page <= lastPageNumber; page++) {
                String domString = getProductListDomString(url + "/page/" + page);
                if (!domString.isEmpty()) {
                    Document document = XMLUtilities.parseStringToDom(domString);
                    XPath xPath = XMLUtilities.getXPath();
                    NodeList productList = (NodeList) xPath.evaluate("./ul/li", document, XPathConstants.NODESET);
                    if (productList != null) {
                        for (int p = 0; p < productList.getLength(); p++) {
                            Node productNode = productList.item(p);
                            
                            Node productImageNode = (Node) xPath.evaluate(".//img", productNode, XPathConstants.NODE);
                            String productImage = productImageNode.getAttributes().getNamedItem("src").getTextContent().trim();
                            
                            Node productLinkNode = (Node) xPath.evaluate(".//div[@class='item-img products-thumb']/a", productNode, XPathConstants.NODE);
                            String productLink = productLinkNode.getAttributes().getNamedItem("href").getTextContent().trim();
                            
                            Node productNameNode = (Node) xPath.evaluate(".//div[@class='item-content products-content']/h4/a", productNode, XPathConstants.NODE);
                            String productName = productNameNode.getTextContent();
                            
                            String productPrice = (String) xPath.evaluate(".//span[@class='item-price']/span", productNode, XPathConstants.STRING);
                            
                            Product product = new Product(productName, productImage, CommonUtilities.convertPriceXgear(productPrice), productLink);
                            productArray.add(product);
                            System.out.print("+");
                        }
                    }
                }
            }
        } catch (XPathExpressionException | DOMException ex) {
            Logger.getLogger(XgearCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\nget all draft products - done");
        return productArray;
    }
    
    private String getInfoTableDomString(String url) {
        try {
            BufferedReader reader = XMLUtilities.getBufferedReaderFromURL(url);
            String line;
            String document = "";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<table class=\"shop_attributes\">")) {
                    isStart = true;
                }
                // replace entity &-; with a-z
                if (isStart && line.contains("&") && line.contains(";")) {
                    line = line.replaceAll("&", "a").replaceAll(";", "z");
                }
                // close tag table
                if (isStart && line.contains("</table>")) {
                    line = line.substring(0, line.indexOf("</table>")) + "</table>";
                    document += line.trim();
                    break;
                }
                if (isStart && !line.trim().isEmpty()) {
                    document += line.trim();
                }
            }
            return document;
        } catch (IOException ex) {
            Logger.getLogger(XgearCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private Map<String, String> getInfoTableMap(String tableDomString) {
        Map<String, String> table = new Hashtable<>();
        try {
            if (tableDomString.isEmpty()) {
                return table;
            }
            XMLStreamReader reader = XMLUtilities.parseStringToXMLStreamReader(tableDomString);
            String tmpKey = null;
            while (reader.hasNext()) {
                int cursor = reader.next();
                if (cursor == XMLStreamReader.START_ELEMENT) {
                    String tagName = reader.getLocalName();
                    if (tagName.equals("td") || tagName.equals("th")) {
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
            Logger.getLogger(XgearCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }
    
    private Laptop parseLaptop(String tableDomString, Product product) {
        try {
            Map<String, String> table = getInfoTableMap(tableDomString);
            String cpu = table.get("CPU");
            String gpu = table.get("GPU");
            String ram = table.get("RAM");
            String hardDrive = table.get("Ổ cứng SSD") + ", " + table.get("Ổ cứng HDD");
            String monitor = table.get("Màn hình");
            String ports = table.get("Các cổng kết nối");
            String lan = table.get("Lan");
            String wireless = table.get("Wireless Lan");
            Laptop laptop = new Laptop(cpu, gpu, ram, hardDrive, monitor, ports, lan, wireless, product);
            return laptop;
        } catch (Exception ex) {
            Logger.getLogger(XgearCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public void crawlLaptop() {
        for (String laptopLink : laptopPath) {
            List<Product> productList = getAllDraftProducts(siteUrl + laptopLink);
            for (Product product : productList) {
                String tableDomString = getInfoTableDomString(product.getProductLink());
                Laptop laptop = parseLaptop(tableDomString, product);
                System.out.println(laptop);
            }
        }
    }

    private Mouse parseMouse(String tableDomString, Product product) {
        try {
            Map<String, String> table = getInfoTableMap(tableDomString);
            String weight = table.get("Mouse Weight");
            String maxDPI = table.get("Mouse Max. speed");
            String led = table.get("Mouse Led");
            String numberOfButton = table.get("Mouse Buttons");
            Mouse mouse = new Mouse(weight, maxDPI, led, numberOfButton, product);
            return mouse;
        } catch (NumberFormatException ex) {
            Logger.getLogger(XgearCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public void crawlMouse() {
        List<Product> productList = getAllDraftProducts(siteUrl + mousePath);
        for (Product product : productList) {
            String tableDomString = getInfoTableDomString(product.getProductLink());
            Mouse mouse = parseMouse(tableDomString, product);
            System.out.println(mouse);
        }
    }

    private Keyboard parseKeyboard(String tableDomString, Product product) {
        try {
            Map<String, String> table = getInfoTableMap(tableDomString);
            String numberOfKey = table.get("Keyboard Key Rollover");
            String pressForce = table.get("Keyboard Actuation force");
            String distance = table.get("Keyboard Travel distance");
            String led = table.get("Keyboard Backlighting");
            String weight = table.get("Keyboard Weight");
            String size = table.get("Keyboard Size");
            String switches = table.get("Keyboard Switches");
            Keyboard keyboard = new Keyboard(numberOfKey, pressForce, distance, led, weight, size, switches, product);
            return keyboard;
        } catch (NumberFormatException ex) {
            Logger.getLogger(XgearCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void crawlKeyboard() {
        List<Product> productList = getAllDraftProducts(siteUrl + keyboardPath);
        for (Product product : productList) {
            String tableDomString = getInfoTableDomString(product.getProductLink());
            Keyboard keyboard = parseKeyboard(tableDomString, product);
            System.out.println(keyboard);
        }
    }

    @Override
    public void crawlHeadset() {
        
    }
    
}
