package com.gooagoo.monitor.server.timer;

import java.util.List;

import com.birdboy.monitor.server.cache.ClientsCache;
import com.gooagoo.monitor.server.request.ClientNode;

public class JMonitorTask implements Runnable {
	public void run() {
		try {
			System.out.println(" JMonitor data begin ------------------");
			List<ClientNode> list = ClientsCache.getInstance().getClientNodes();
			System.out.println(" 获取监控服务器列表:个数:" + list.size());
			for (ClientNode clientNode : list) {
				System.out.println(" 服务器: " + clientNode.toString());
				try {
					//TODO
				} catch (Exception e) {
					System.out.println(" 当前服务器不能监控，"+clientNode.toString()+" | 异常："+e);
				}
				
			}
			System.out.println(" JMonitor data end --------------------");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(" JMonitorTask error: " + e.getMessage());
		}
	}

}
