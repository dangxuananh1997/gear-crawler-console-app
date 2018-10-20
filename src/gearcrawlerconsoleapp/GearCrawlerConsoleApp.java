package gearcrawlerconsoleapp;

import crawler.XgearCrawler;

/**
 *
 * @author dangxuananh1997
 */
public class GearCrawlerConsoleApp {

    public static void main(String[] args) {
        XgearCrawler crawler = new XgearCrawler();
//        crawler.crawlLaptop();
        String str = "<a><img></a>";
        System.out.println(str.indexOf("<img"));
    }
    
}
