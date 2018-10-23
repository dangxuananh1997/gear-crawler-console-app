package gearcrawlerconsoleapp;

import crawler.HangchinhhieuCrawler;
import crawler.TanThanhDanhCrawler;
import crawler.XgearCrawler;
import java.io.IOException;

/**
 *
 * @author dangxuananh1997
 */
public class GearCrawlerConsoleApp {

    public static void main(String[] args) throws IOException {
        HangchinhhieuCrawler hccCrawler = new HangchinhhieuCrawler();
//        hccCrawler.crawlKeyboard();
        TanThanhDanhCrawler tthCrawler = new TanThanhDanhCrawler();
//        tthCrawler.crawlLaptop();
        XgearCrawler xgCrawler = new XgearCrawler();
        xgCrawler.crawlKeyboard();
        xgCrawler.crawlMouse();
    }
    
}
