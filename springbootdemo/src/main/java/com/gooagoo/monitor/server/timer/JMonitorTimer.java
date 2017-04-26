package com.gooagoo.monitor.server.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class JMonitorTimer {
	  static ScheduledExecutorService scheduExec;
	  static final int INTERVAL = 60;
	  public static void load()
	  {
	    try {
			scheduExec = Executors.newScheduledThreadPool(1);
		    JMonitorTask rdtask = new JMonitorTask();
		    // 测试环境3600*24秒
		    scheduExec.scheduleWithFixedDelay(rdtask,10L,INTERVAL, TimeUnit.SECONDS);
		    System.out.println(" timer is work start ,interval : " + INTERVAL);	
		} catch (Exception e) {
			e.printStackTrace();
		}

	  }
}
