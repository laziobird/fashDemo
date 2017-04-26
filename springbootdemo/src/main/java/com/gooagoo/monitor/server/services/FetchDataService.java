package com.gooagoo.monitor.server.services;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birdboy.monitor.server.common.HttpClientUtil;
import com.gooagoo.monitor.server.request.ClientNode;
import com.gooagoo.monitor.server.request.MonitorValues;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 
 * @author laziobird
 *
 */
public class FetchDataService {
	
    static final Logger logger = LoggerFactory.getLogger(FetchDataService.class);
	
	private static FetchDataService one = new FetchDataService();

	public static FetchDataService getInstance() {
		return one;
	}	
	
	
	/**
	 * 来自小米 https://book.open-falcon.org/zh/usage/data-push.html
		[
		    {
		        "endpoint": "test-endpoint",
		        "metric": "test-metric",
		        "timestamp": ts,
		        "step": 60,
		        "value": 1,
		        "counterType": "GAUGE",
		        "tags": "idc=lg,loc=beijing",
		    },
		
		    {
		        "endpoint": "test-endpoint",
		        "metric": "test-metric2",
		        "timestamp": ts,
		        "step": 60,
		        "value": 2,
		        "counterType": "GAUGE",
		        "tags": "idc=lg,loc=beijing",
		    },
		]
	 * @throws IOException 
	**/

	public void postOpenFalcon(List<MonitorValues> monitors,ClientNode client) throws IOException{
		System.out.println("推送给falcon的信息,监控服务器:"+client+" | 监控的数据:"+monitors.toString());
		String url = "http://"+client.getIp()+":1988/v1/push";
		JsonArray ja = new JsonArray();
		for (MonitorValues monitorValues : monitors) {
			//请求平均访问时间
			JsonObject jo = new JsonObject();
			jo.addProperty("metric", monitorValues.getMethod()+"_avgTime");
			jo.addProperty("endpoint", client.getIp());
			jo.addProperty("value", monitorValues.getMs_per_invoke());
			jo.addProperty("counterType", "GAUGE");
			jo.addProperty("tags", "tags");
			jo.addProperty("step", 60);
			jo.addProperty("timestamp", Calendar.getInstance().getTimeInMillis()/1000);
			ja.add(jo);
			//请求访问次数
			JsonObject jo2 = new JsonObject();
			jo2.addProperty("metric", monitorValues.getMethod()+"_count");
			jo2.addProperty("endpoint", client.getIp());
			jo2.addProperty("value", monitorValues.getTimes());
			jo2.addProperty("counterType", "GAUGE");
			jo2.addProperty("tags", "tags");
			jo2.addProperty("step", 60);
			jo2.addProperty("timestamp", Calendar.getInstance().getTimeInMillis()/1000);			
			ja.add(jo2);
		}
		String re = ja.toString();
		logger.info(url+" | 发送给Falcon Agent 最终的post数据:"+re);
		System.out.println(url+" | 发送给Falcon Agent ");
		HttpClientUtil.post(url,re);
	}
	

	public static void main(String[] args) {
		String url = "http://192.168.3.223:1988/v1/push";
		JsonArray ja = new JsonArray();
		JsonObject jo2 = new JsonObject();
		jo2.addProperty("metric", "监控数据333");
		jo2.addProperty("endpoint", "mac-jiangzhiwei");
		jo2.addProperty("value", 5);
		jo2.addProperty("counterType", "GAUGE");
		jo2.addProperty("tags", "");
		jo2.addProperty("step", 60);
		jo2.addProperty("timestamp", Calendar.getInstance().getTimeInMillis() / 1000);
		ja.add(jo2);
		System.out.println(ja.toString());
		try {
			System.out.println(HttpClientUtil.post(url, ja.toString()));
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		//FetchDataService.getInstance().fetchClientMonitorInfo("http://192.168.9.226:9707/pull/statistics");
		
	}
}
