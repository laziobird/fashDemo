package com.gooagoo.monitor.server.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import com.birdboy.monitor.server.cache.ClientsCache;
import com.gooagoo.monitor.server.timer.JMonitorTimer;

@SpringBootApplication(scanBasePackages = {"com.birdboy"}, exclude = {MongoAutoConfiguration.class})
public class MonitorServer {
    static final Logger logger = LoggerFactory.getLogger(MonitorServer.class);	
    public static void main(String... args) {
        SpringApplication.run(MonitorServer.class, args);
        try {
			ClientsCache.getInstance().load();
			JMonitorTimer.load();
			logger.info("监控服务启动成功！");
		} catch (Exception e) {
			logger.error("启动失败！",e);
			e.printStackTrace();			
			System.exit(-2);

		}       
    }
}
