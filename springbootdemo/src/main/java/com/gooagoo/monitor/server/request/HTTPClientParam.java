package com.gooagoo.monitor.server.request;

import com.gooagoo.monitor.server.status.FromTypeEnum;
import com.google.gson.Gson;

public class HTTPClientParam {
	public int type;
	public ClientNode serverNode;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ClientNode getServerNode() {
		return serverNode;
	}
	public void setServerNode(ClientNode serverNode) {
		this.serverNode = serverNode;
	}
	
	public static void main(String[] args) {
		HTTPClientParam re = new HTTPClientParam();
		ClientNode sn = new ClientNode();
		sn.setServerName("newserver");
		sn.setIp("127.0.0.1");
		sn.setPort(80);
		sn.setPath("/getnode");
		re.setServerNode(sn);
		re.setType(FromTypeEnum.CLIENT.getValue());
		Gson gson = new Gson();
		System.out.println(gson.toJson(re));
	}
}
