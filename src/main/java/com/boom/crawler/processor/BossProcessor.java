package com.boom.crawler.processor;

import com.boom.crawler.pipeline.BossPipeline;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import javax.management.JMException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

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
      .setRetryTimes(5)
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

    System.out.println("page url: " + page.getUrl().toString() + " # isDownloadSuccess : " + page
        .isDownloadSuccess());

    //获取json串中的key为htmlList值
    String htmlList = page.getJson().jsonPath("htmlList").toString();

    //key为htmlList值实际是一段html
    Document doc = Jsoup.parse(htmlList);

    Elements links = doc.getElementsByTag("li");

    CloseableHttpClient httpclient = HttpClients.createDefault();

    System.out.println("############");
    links.stream().forEach(a -> {
                             System.out.println("\n li:" + a.toString());
                             a.select("a[href]").stream().forEach(b -> {
                               System.out.println("\n data-uid:" + b.attr("data-uid"));
                               System.out.println("\n data-jid:" + b.attr("data-jid"));
                               System.out.println("\n data-expect:" + b.attr("data-expect"));
                               System.out.println("\n data-lid:" + b.attr("data-lid"));

                               //todo 需要加验证，只挑选出合适的人选才会发送"打招呼"的请求。

                               //发送"打招呼请求"
//                               page.addTargetRequest(
//                                   getSayHiRequest(b.attr("data-uid")));

                               page.getTargetRequests().stream().forEach(c -> {
                                 System.out.println(
                                     "TargetRequests:" + c.getUrl() + " # RequestBody: " + c.getRequestBody().toString()
                                     + " # Headers: " + c.getHeaders().toString());
                               });

                               try {
                                 HttpResponse
                                     responsePost =
                                     httpclient.execute(
                                         getSayHiPostRequest(b.attr("data-uid"), b.attr("data-jid"), b.attr("data-expect"),
                                                             b.attr("data-lid")));

                                 HttpResponse
                                     responseGet =
                                     httpclient.execute(getSayHiGetRequest(b.attr("data-uid")));

                                 Header[] h = responsePost.getAllHeaders();
                                 for (Header header : h) {
                                   System.out.println("responsePost header: " + header.toString());
                                 }
                                 if (responsePost.getEntity() != null) {
                                   System.out.println(
                                       "responsePost Entity : " + responsePost.getEntity() + " #responsePost StatusLine: " + responsePost.getStatusLine()
                                           .toString() + "#responsePost response: " + responsePost.toString());
                                 }

                                 if (responseGet.getEntity() != null) {
                                   System.out.println(
                                       "responseGet Entity : " + responseGet.getEntity() + " #responseGet StatusLine: " + responseGet.getStatusLine()
                                           .toString() + "#responseGet response: " + responseGet.toString());
                                 }

                               } catch (IOException e) {
                                 e.printStackTrace();
                               }


                             });
                           }
    );

    logger.info("htmlList:" + htmlList);

//    if (htmlList != null && htmlList != "") {
//      //putField交给Pipeline处理
//      page.putField("htmlList", htmlList);
//    } else {
//      page.setSkip(true);
//    }

  }

  public Site getSite() {
    return site;
  }

  //暂时废弃不用  但是不要删
//  private Request getSayHiRequest(String uid, String jid, String expect, String lid) {
//    Request request = new Request("https://www.zhipin.com/chat/batchAddRelation.json");
//    request.setMethod(HttpConstant.Method.POST);
//
//    request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
//    request.addHeader("Accept-Encoding", "gzip, deflate, br");
//    request.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
//    request.addHeader("Connection", "keep-alive");
////    request.addHeader("Content-Length", "92");
//    request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//    request.addHeader("Host", "www.zhipin.com");
//    request.addHeader("Origin", "https://www.zhipin.com");
//    request.addHeader("Referer", "https://www.zhipin.com/chat/im?mu=recommend");
//    request.addHeader("User-Agent",
//                      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
//    request.addHeader("X-Requested-With", "XMLHttpRequest");
//    request.addHeader("Cookie",
//                      "lastCity=101210100; __c=1500876139; __g=-; JSESSIONID=\"\"; toUrl=https%3A%2F%2Fwww.zhipin.com%2Fjob_detail%2F1412453118.html%3Fka%3Djob-10; wt=Yh4JvSPC0hcKthQs; t=Yh4JvSPC0hcKthQs; Hm_lvt_194df3105ad7148dcf2b98a91b5e727a=1500473970,1500560594,1500603618,1500876139; Hm_lpvt_194df3105ad7148dcf2b98a91b5e727a=1500895165; __l=l=%2Fwww.zhipin.com%2F&r=https%3A%2F%2Fwww.google.com.sg%2F; __a=80327904.1498872869.1499997162.1500876139.101.4.9.37");
//
//    Map<String, Object> froms = new HashMap();
//    froms.put("data-uid", uid);
//    froms.put("data-jid", jid);
//    froms.put("data-expect", expect);
//    froms.put("data-lid", lid);
//    try {
//      request.setRequestBody(HttpRequestBody.form(froms, "UTF-8"));
//    } catch (UnsupportedEncodingException e) {
//      e.printStackTrace();
//    }
//
//    return request;
//  }

  //https://www.zhipin.com/chat/geek.json?uid=19505677
  private Request getSayHiRequest(String uid) {
    Request request = new Request("https://www.zhipin.com/chat/geek.json?uid=" + uid);
    request.setMethod(HttpConstant.Method.GET);

    request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
    request.addHeader("Accept-Encoding", "gzip, deflate, br");
    request.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
    request.addHeader("Connection", "keep-alive");
    request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    request.addHeader("Host", "www.zhipin.com");
    request.addHeader("Origin", "https://www.zhipin.com");
    request.addHeader("Referer", "https://www.zhipin.com/chat/im?mu=recommend");
    request.addHeader("User-Agent",
                      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
    request.addHeader("X-Requested-With", "XMLHttpRequest");
    request.addHeader("Cookie",
                      "lastCity=101210100; __c=1500876139; __g=-; JSESSIONID=\"\"; toUrl=https%3A%2F%2Fwww.zhipin.com%2Fjob_detail%2F1412453118.html%3Fka%3Djob-10; wt=Yh4JvSPC0hcKthQs; t=Yh4JvSPC0hcKthQs; Hm_lvt_194df3105ad7148dcf2b98a91b5e727a=1500473970,1500560594,1500603618,1500876139; Hm_lpvt_194df3105ad7148dcf2b98a91b5e727a=1500895165; __l=l=%2Fwww.zhipin.com%2F&r=https%3A%2F%2Fwww.google.com.sg%2F; __a=80327904.1498872869.1499997162.1500876139.101.4.9.37");

    return request;
  }


  private HttpPost getSayHiPostRequest(String uid, String jid, String expect, String lid) {

    HttpPost request = new HttpPost("https://www.zhipin.com/chat/batchAddRelation.json");

    request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
    request.addHeader("Accept-Encoding", "gzip, deflate, br");
    request.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
    request.addHeader("Connection", "keep-alive");
    request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    request.addHeader("Host", "www.zhipin.com");
    request.addHeader("Origin", "https://www.zhipin.com");
    request.addHeader("Referer", "https://www.zhipin.com/chat/im?mu=recommend");
    request.addHeader("User-Agent",
                      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
    request.addHeader("X-Requested-With", "XMLHttpRequest");
    request.addHeader("Cookie",
                      "lastCity=101210100; __c=1500876139; __g=-; JSESSIONID=\"\"; toUrl=https%3A%2F%2Fwww.zhipin.com%2Fjob_detail%2F1412453118.html%3Fka%3Djob-10; wt=Yh4JvSPC0hcKthQs; t=Yh4JvSPC0hcKthQs; Hm_lvt_194df3105ad7148dcf2b98a91b5e727a=1500473970,1500560594,1500603618,1500876139; Hm_lpvt_194df3105ad7148dcf2b98a91b5e727a=1500895165; __l=l=%2Fwww.zhipin.com%2F&r=https%3A%2F%2Fwww.google.com.sg%2F; __a=80327904.1498872869.1499997162.1500876139.101.4.9.37");

    String
        paras =
        "data-uid=" + uid + "&data-jid=" + jid + "&data-expect=" + expect + "&data-lid=" + lid;

    request.setEntity(new StringEntity(paras, "UTF-8"));

    return request;
  }


  private HttpGet getSayHiGetRequest(String uid) {

    HttpGet request = new HttpGet("https://www.zhipin.com/chat/geek.json?uid=" + uid);

    request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
    request.addHeader("Accept-Encoding", "gzip, deflate, br");
    request.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
    request.addHeader("Connection", "keep-alive");
    request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    request.addHeader("Host", "www.zhipin.com");
    request.addHeader("Origin", "https://www.zhipin.com");
    request.addHeader("Referer", "https://www.zhipin.com/chat/im?mu=recommend");
    request.addHeader("User-Agent",
                      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
    request.addHeader("X-Requested-With", "XMLHttpRequest");
    request.addHeader("Cookie",
                      "lastCity=101210100; __c=1500876139; __g=-; JSESSIONID=\"\"; toUrl=https%3A%2F%2Fwww.zhipin.com%2Fjob_detail%2F1412453118.html%3Fka%3Djob-10; wt=Yh4JvSPC0hcKthQs; t=Yh4JvSPC0hcKthQs; Hm_lvt_194df3105ad7148dcf2b98a91b5e727a=1500473970,1500560594,1500603618,1500876139; Hm_lpvt_194df3105ad7148dcf2b98a91b5e727a=1500895165; __l=l=%2Fwww.zhipin.com%2F&r=https%3A%2F%2Fwww.google.com.sg%2F; __a=80327904.1498872869.1499997162.1500876139.101.4.9.37");

    return request;
  }

  public static void main(String[] args) {
    Spider bossSpider = Spider.create(new BossProcessor())
        .addUrl(
            "https://www.zhipin.com/boss/recommend/geeks.json?page=1&status=0&jobid=&salary=0&experience=0&degree=0&_=1500018804874")
        .addPipeline(new BossPipeline())
        //.setScheduler(new FileCacheQueueScheduler(Constants.CSDN_FILE_CACHE_URLS))
        .thread(5);

    try {
      SpiderMonitor.instance().register(bossSpider);
    } catch (JMException e) {
      e.printStackTrace();
    }
    //bossSpider.addRequest(getSayHiRequest(b.attr("data-uid"), b.attr("data-jid"), b.attr("data-expect"), b.attr("data-lid")));
    bossSpider.start();
  }

}
