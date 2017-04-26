package com.gooagoo.monitor.server.request;

public class ClientNode {
	public String ip;
	public int port;
	public String path;
	public String serverName;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	@Override
	public String toString() {
		return "HTTPClientParam [ip=" + ip + ", port=" + port + ", path=" + path + ", serverName=" + serverName + "]";
	}
	
}
