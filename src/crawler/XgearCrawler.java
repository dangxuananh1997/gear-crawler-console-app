package crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class XgearCrawler implements CrawlerInterface {

    public XgearCrawler() {
    }

    private String getPaginationDomString(String url) {
        try {
            BufferedReader reader = XMLUtilities.getBufferedReaderFromURL(url);
            String line;
            String document = "";
            boolean isStart = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<ul class=\'page-numbers\'>")) {
                    isStart = true;
                }
                // replace entity &-; with a-z
                if (isStart && line.contains("&") && line.contains(";")) {
                    line = line.replaceAll("&", "a").replaceAll(";", "z");
                }
                if (isStart && !line.isEmpty()) {
                    document += line.trim();
                }
                if (isStart && line.contains("</ul>")) {
                    break;
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
                if (line.contains("class=\"products-loop")) {
                    isStart = true;
                }
                // replace entity &-; with a-z
                if (isStart && line.contains("&") && line.contains(";")) {
                    line = line.replaceAll("&", "a").replaceAll(";", "z");
                }
                // add img closing tag
                if (isStart && line.contains("<img")) {
                    int pos = line.indexOf("<img");
                    System.out.println(line);
                }
                if (isStart && !line.isEmpty()) {
                    document += line.trim();
                }
                if (isStart && line.contains("</ul>")) {
                    break;
                }
            }
            return document;
        } catch (IOException ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private List<String> getAllProductLink(String url) {
        List<String> productLinkArray = new LinkedList<>();
        try {
            int lastPageNumber = getLastPageNumber(url);
            for (int page = 1; page <= lastPageNumber; page++) {
                String domString = getProductDomString(url + "/page/" + page);
                if (!domString.isEmpty()) {
                    Document document = XMLUtilities.parseStringToDom(domString);
                    XPath xPath = XMLUtilities.getXPath();
                    NodeList productList = (NodeList) xPath.evaluate(".//div[@class='item-detail']", document, XPathConstants.NODESET);
                    if (productList != null) {
                        for (int p = 0; p < productList.getLength(); p++) {
                            Node product = productList.item(p);
                            Node productLink = (Node) xPath.evaluate(".//a", product, XPathConstants.NODE);
                            productLinkArray.add(productLink.getAttributes().getNamedItem("href").getTextContent());
                        }
                    }
                }
            }
            System.out.println(productLinkArray);
        } catch (Exception ex) {
            Logger.getLogger(HangchinhhieuCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productLinkArray;
    }
    
    @Override
    public void crawlLaptop() {
//        getLastPageNumber("https://xgear.vn/danh-muc/laptop-asus/");
//        getProductDomString("https://xgear.vn/danh-muc/laptop-asus/");
        getAllProductLink("https://xgear.vn/danh-muc/laptop-asus/");
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
