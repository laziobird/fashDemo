package com.gooagoo.monitor.server.services;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birdboy.monitor.server.cache.ClientsCache;
import com.birdboy.monitor.server.common.HttpClientUtil;
import com.gooagoo.monitor.server.enity.PhoneMsg;

public class MessageSenderService {
    static final Logger logger = LoggerFactory.getLogger(MessageSenderService.class);	
	private static MessageSenderService one = new MessageSenderService();
	public static MessageSenderService getInstance() {
		return one;
	}	
	public String sendMessage(String mobiles, String content) throws Exception {
		// 短信流水号
		// 取当前时间作为短信流水号
		String rrid = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		// 短信内容
		if (content == null || content.equals("")) {
			throw new Exception(String.format("流水号[%s].短信内容为空.", rrid));
		}
		PhoneMsg pm = ClientsCache.getInstance().getPhoneMsg();
		String newStr = mobiles.replaceAll(";", ",");
	
		String url = pm.getUrl() + "sn=" + pm.getSn() + "&pwd=" + pm.getPwd() + "&mobile=" + newStr + "&content="
				+ URLEncoder.encode(content, "GBK") + "&ext=&stime=&rrid=";
		System.out.println(" | 发送给短信URL:"+url);
		logger.info(" | 发送给短信URL:"+url);
		// 短信手机号
		return HttpClientUtil.get(url);
	}

	public static void main(String[] args) {
		try {
			//ClientsCache.getInstance().load();
			//MessageSenderService.getInstance().sendMessage("18280041890", "我好啊！");
			System.out.println("1;2;3".replaceAll(";", ","));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
