package com.boom.crawler.processor;

import com.boom.crawler.pipeline.BossPipeline;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by yangding on 2017/7/19.
 */
public class BossProcessor implements PageProcessor {

  private Logger logger = Logger.getLogger(getClass());

  String
      cookie =
      "lastCity=101210100; JSESSIONID=\"\"; __c=1499997162; __g=-; toUrl=https%3A%2F%2Fwww.zhipin.com%2F; wt=Yh4JvSPC0hcKthQs; t=Yh4JvSPC0hcKthQs; Hm_lvt_194df3105ad7148dcf2b98a91b5e727a=1499749270,1499749522,1499865440,1499997162; Hm_lpvt_194df3105ad7148dcf2b98a91b5e727a=1500018805; __l=l=%2Fwww.zhipin.com%2F&r=https%3A%2F%2Fwww.google.com.sg%2F; __a=80327904.1498872869.1499865440.1499997162.39.3.7.39";

  private Site site = Site.me()
      .setDomain("https://www.zhipin.com")
      .setCharset("utf-8")
      .setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0")
      .setSleepTime(30000)
      .setTimeOut(40000)
      .setRetryTimes(3)
      .addHeader("Host", "www.zhipin.com")
      .addHeader("Cookie", cookie)
      .addHeader("User-Agent",
                 "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
      .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
      .addHeader("Accept-Encoding", "gzip, deflate, br")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
      .addHeader("Connection", "keep-alive")
      .addHeader("Referer", "https://www.zhipin.com/chat/im?mu=recommend&status=0")
      .addHeader("X-Requested-With", "XMLHttpRequest");

  public void process(Page page) {
    logger.info("page.getJson():" + page.getJson());
    //获取json串中的key为htmlList值
    String htmlList = page.getJson().jsonPath("htmlList").toString();

    //key为htmlList值实际是一段html
    Document doc = Jsoup.parse(htmlList);

    Elements links = doc.getElementsByTag("li");
    System.out.print("############");
    links.stream().forEach(a -> {
          System.out.println("\n li:" + a.toString());
          a.select("a[href]").stream().forEach(b -> {
            System.out.println("\n data-uid:" + b.attr("data-uid"));
            System.out.println("\n data-jid:" + b.attr("data-jid"));
            System.out.println("\n data-expect:" + b.attr("data-expect"));
            System.out.println("\n data-lid:" + b.attr("data-lid"));
          });
        }
    );

    logger.info("htmlList:" + htmlList);


    if (htmlList != null && htmlList != "") {
      //putField交给Pipeline处理
      page.putField("htmlList", htmlList);
    } else {
      page.setSkip(true);
    }

  }

  public Site getSite() {
    return site;
  }

  public static void main(String[] args) {
    Spider.create(new BossProcessor())
        .addUrl(
            "https://www.zhipin.com/boss/recommend/geeks.json?page=1&status=0&jobid=&salary=0&experience=0&degree=0&_=1500018804874")
        .addPipeline(new BossPipeline())
        //.setScheduler(new FileCacheQueueScheduler(Constants.CSDN_FILE_CACHE_URLS))
        .thread(5)
        .run();
  }

}
