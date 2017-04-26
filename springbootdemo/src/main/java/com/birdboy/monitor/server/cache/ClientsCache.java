package com.birdboy.monitor.server.cache;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gooagoo.monitor.server.enity.PhoneMsg;
import com.gooagoo.monitor.server.request.ClientNode;

/**
 * 将要注册服务保存到Cache里面
 * 
 * @author laziobird 读取配置文件
 *
 */
// @ConfigurationProperties(locations = "classpath:monitor22.properties",
// ignoreUnknownFields = false, prefix = "client")
public class ClientsCache {
	
    static final Logger logger = LoggerFactory.getLogger(ClientsCache.class);		

	Properties props = new Properties();

	private static ClientsCache one = new ClientsCache();

	public static ClientsCache getInstance() {
		return one;
	}

	public ClientsCache() {
		try {
			InputStream in = this.getClass().getResourceAsStream("/monitor.properties");
			props.load(in);
		} catch (Exception e) {
			System.err.println("加载配置文件异常" + e.getCause().getMessage());
			System.exit(-1);
		}

	}
	//监控节点白名单
	public static List<ClientNode> clientNodes = new ArrayList<ClientNode>();
	//监控值别名
	private static Map<String,String> aliasMap = new HashMap<String,String>();
	//短信配置信息
	private static PhoneMsg phoneMsg = new PhoneMsg();

	public void freshCache(ClientNode one) throws Exception {
		for (ClientNode clientNode : clientNodes) {
			if (StringUtils.isNotBlank(clientNode.getIp()) || StringUtils.isNotBlank(clientNode.getPath())
					|| StringUtils.isNotBlank(clientNode.getServerName()) || clientNode.getPort() > 0) {
				throw new Exception("服务器参数不对:" + clientNode.toString());
			}
			if (StringUtils.equals(clientNode.getIp(), one.getIp())&&clientNode.getPort()==one.getPort()) {
				// 如果已经存在，更新serverName
				clientNode.setServerName(one.getServerName());
				throw new Exception("服务器已经存在:" + clientNode.toString());
			}
		}
		// 注册新的监控服务节点
		System.out.println("监控新的服务器节点:" + one.toString());
		clientNodes.add(one);
	}

	public List<ClientNode> getClientNodes() {
		return clientNodes;
	}

	/**
	 * 通过定时程序定时reload 监控服务器信息
	 */
	public void reloadCacheByConf() {

	}

	public void load() throws Exception {

		System.out.println("初始化配置信息内容:" + props.toString());
		String[] urls = props.getProperty("client.ips").split(",");
		for (String url : urls) {
			if (StringUtils.isNotBlank(url)) {
				String[] info = url.split(":");
				String ip = info[1];
				Integer port = Integer.parseInt(info[2]);
				String name = info[0];
				ClientNode clientNode = new ClientNode();
				clientNode.setIp(ip);
				clientNode.setPort(port);
				clientNode.setPath(props.getProperty("client.path"));
				clientNode.setServerName(name);
				clientNodes.add(clientNode);
			}
		}
		System.out.println("初始化服务监控节点:" + clientNodes.toString());
				
		String[] aliasKeys = props.getProperty("alias.keys").split(",,");
		for (String keys : aliasKeys) {
			String[] key = keys.split(":");
			aliasMap.put(key[1], key[0]);
		}	
		System.out.println("初始化别名配置:"+aliasMap);
		System.out.println("初始化短信配置:");
		phoneMsg.setSn(props.getProperty("phone.msg.sn"));
		phoneMsg.setPwd(props.getProperty("phone.msg.pwd"));
		phoneMsg.setUrl(props.getProperty("phone.msg.url"));
		System.out.println("短信配置:"+phoneMsg.toString());
	}

	public String returnMonitorName(String keyName){
		if(aliasMap.containsKey(keyName)){
			System.out.println(" 监控取别名 源key:"+keyName+" | value :"+aliasMap.get(keyName));
			return aliasMap.get(keyName);
		}
		return keyName;
	}
	
	
	public PhoneMsg getPhoneMsg() {		
		return phoneMsg;
	}

}
