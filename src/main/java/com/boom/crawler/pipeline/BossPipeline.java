package com.boom.crawler.pipeline;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by yangding on 2017/7/19.
 */
public class BossPipeline implements Pipeline {

  private Logger logger = Logger.getLogger(getClass());

  public void process(ResultItems resultItems, Task task) {

    logger.info(resultItems.toString());

    Map<String, String> map = new HashMap<String, String>();
    String htmlList = resultItems.get("htmlList");
    map.put("htmlList", htmlList);

    if (htmlList != null && htmlList != "") {
      JSONObject jsonObj = JSONObject.fromObject(map);
      logger.info(jsonObj.toString());
    }
  }


}
