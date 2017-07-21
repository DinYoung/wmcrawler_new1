package com.boom.crawler.processor;

import com.boom.crawler.pipeline.CsdnPipeline;
import com.boom.crawler.util.Constants;

import org.apache.log4j.Logger;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

public class CsdnProcessor implements PageProcessor{

	 private Logger logger = Logger.getLogger(getClass());
	 
	private Site site = Site.me()
			.setDomain(Constants.CSDN_DOMAIN)
			.setCharset("utf-8")
			.setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0")
			.setSleepTime(30000)
			.setTimeOut(40000)
			.setRetryTimes(3)
			.addHeader("Host", "bbs.csdn.net")
			;


	public void process(Page page) {
		// 列表页
		List<String> postUrls = page.getHtml().xpath("//div[@class='page_nav']/ul/li/a/@href")
				.all();
		// 将帖子地址加入要抓取的url队列
		page.addTargetRequests(postUrls);

		logger.info(page.getUrl());
		// 得到页面里的包含图片的div
		String title = page.getHtml().xpath("//td[@class='title']/a/text()").toString();
		logger.info("title:" + title);
		if (title != null && title != "") {
			page.putField("title", title);
		} else {
			page.setSkip(true);
		}

	}

	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
    	 Spider.create(new CsdnProcessor())
    	 .addUrl(Constants.CSDN_START_URL)
    	 .addPipeline(new CsdnPipeline())
    	 //.setScheduler(new FileCacheQueueScheduler(Constants.CSDN_FILE_CACHE_URLS))
    	 .thread(5)
    	 .run();
    }
}
