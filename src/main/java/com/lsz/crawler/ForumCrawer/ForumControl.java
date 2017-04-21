package com.lsz.crawler.ForumCrawer;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * Created by asus on 2017/4/18.
 */
public class ForumControl {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder="/data/forum/root";
        int numberOfCrawlers=5;
        CrawlConfig config=new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        //实例化Control
        PageFetcher pageFetcher=new PageFetcher(config);
        RobotstxtConfig robotstxtConfig=new RobotstxtConfig();
        RobotstxtServer robotstxtServer=new RobotstxtServer(robotstxtConfig,pageFetcher);
        CrawlController controller=new CrawlController(config,pageFetcher,robotstxtServer);
        //开始爬的url地址，可以有多个
        controller.addSeed("http://bbs.tianya.cn");
        controller.startNonBlocking(ForumCrawler.class,numberOfCrawlers);

    }


}
