package com.boom.crawler.util;

public class Constants {
	public static final String CSDN_DOMAIN = "http://bbs.csdn.net";//网站域名, 自己去找
	public static final String CSDN_START_URL = "http://bbs.csdn.net/forums/JavaScript";//要抓取的入口url
	public static final String CSDN_DIR = ".\\config\\csdn\\";
	public static final String CSDN_FILE_CACHE_URLS = CSDN_DIR + "urls\\";//记录url,下次重启时可以从之前抓取到的URL继续
	public static final String CSDN_FILE_NAME = "csdn_JavaScript.txt";//保存的文件名
	//Java_WebDevelop , JavaOther,JavaScript
}
