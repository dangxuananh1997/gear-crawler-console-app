package gearcrawlerconsoleapp;

import crawler.HangchinhhieuCrawler;
import crawler.XgearCrawler;
import utilities.XMLUtilities;

/**
 *
 * @author dangxuananh1997
 */
public class GearCrawlerConsoleApp {

    public static void main(String[] args) {
        XgearCrawler crawler = new XgearCrawler();
//        crawler.crawlLaptop();
        HangchinhhieuCrawler hccCrawler = new HangchinhhieuCrawler();
        hccCrawler.crawlLaptop();
    }
    
}
