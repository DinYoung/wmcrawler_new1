package com.boom.crawler.pipeline;


import com.boom.crawler.util.Constants;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class CsdnPipeline implements Pipeline {

  private Logger logger = Logger.getLogger(getClass());

  public void process(ResultItems resultItems, Task task) {
    Map<String, String> map = new HashMap<String, String>();
    String title = resultItems.get("title");
    map.put("title", title);
    if (title != null && title != "") {
      JSONObject jsonObj = JSONObject.fromObject(map);
      logger.info(jsonObj.toString());
      this.save2File(jsonObj.toString(), Constants.CSDN_FILE_NAME, Constants.CSDN_DIR);
    }
  }

  public void save2File(List<String> listString, String filename, String savePath) {
    FileWriter fwriter = null;
    try {
      File sf = new File(savePath);
      if (!sf.exists()) {
        sf.mkdirs();
      }
      fwriter = new FileWriter(savePath + "\\" + filename, true);
      if (listString.size() > 0) {
        for (String divString : listString) {
          fwriter.write(divString);
//					System.out.println(divString);
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      try {
        fwriter.flush();
        fwriter.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }


  public void save2File(String divString, String filename, String savePath) {
//		FileWriter fwriter = null;
    FileOutputStream fos = null;
    OutputStreamWriter osw = null;
    try {
      File sf = new File(savePath);
      if (!sf.exists()) {
        sf.mkdirs();
      }
//			fwriter = new FileWriter(savePath + "\\" + filename, true);
//			fwriter.write(divString + "\t\n");

      fos = new FileOutputStream(savePath + "\\" + filename, true);
      osw = new OutputStreamWriter(fos, "UTF-8");
      osw.write(divString + "\t\n");

    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      try {
//				fwriter.flush();
//				fwriter.close();
        osw.flush();
        osw.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void download(String urlString, String filename, String savePath) throws Exception {
    // 构造URL
    URL url = new URL(urlString);

    // 打开连接
    URLConnection con = url.openConnection();
    //设置请求的路径
    con.setConnectTimeout(5 * 1000);
    con.setRequestProperty("User-Agent",
                           "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
    con.setRequestProperty("Host", "www.46g.cn");
    // 输入流
    InputStream is = con.getInputStream();

    // 1K的数据缓冲
    byte[] bs = new byte[1024];
    // 读取到的数据长度
    int len;
    // 输出的文件流
    File sf = new File(savePath);
    if (!sf.exists()) {
      sf.mkdirs();
    }
    OutputStream os = new FileOutputStream(sf.getPath() + "\\" + filename);
    // 开始读取
    while ((len = is.read(bs)) != -1) {
      os.write(bs, 0, len);
    }
    // 完毕，关闭所有链接
    os.close();

    is.close();
  }

  public static void main(String[] args) throws Exception {
    CsdnPipeline fan = new CsdnPipeline();
    fan.download("http://www.46g.cn/uploadfile/small/201508212220422985/200x255.jpg", "1.jpg",
                 "./图片测试");
  }
}
