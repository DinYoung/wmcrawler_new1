package com.boom.crawler.util;//package com.yangding.yangyujuan.crawler.util;
//
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//
//public class ImageBase64Utils {
//	public static String GetImageStr(byte[] data) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
//
//		// 对字节数组Base64编码
//		BASE64Encoder encoder = new BASE64Encoder();
//		return encoder.encode(data);// 返回Base64编码过的字节数组字符串
//	}
//
//	public static boolean GenerateImage(String path, String filename, String imgStr) {// 对字节数组字符串进行Base64解码并生成图片
//		System.out.println("imgStr == null:" + imgStr == null);
//		if (imgStr == null) // 图像数据为空
//		{
//			return false;
//		}
//		BASE64Decoder decoder = new BASE64Decoder();
//		try {
//			// Base64解码
//			byte[] b = decoder.decodeBuffer(imgStr);
//			for (int i = 0; i < b.length; ++i) {
//				if (b[i] < 0) {// 调整异常数据
//					b[i] += 256;
//				}
//			}
//			// 生成jpeg图片
//			String imgFilePath = path + filename;// 新生成的图片
//			OutputStream out = new FileOutputStream(imgFilePath);
//			out.write(b);
//			out.flush();
//			out.close();
//			return true;
//		} catch (Exception e) {
//			System.out.println("e:" + e.getMessage());
//			return false;
//		}
//	}
//}
