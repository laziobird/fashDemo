package com.gooagoo.monitor.server.enity;

public class PhoneMsg {
	//http://sdk2.zucp.net:8060/webservice.asmx/mt?sn=SDK-BBX-010-20197&pwd=77C73D14E8158675C4A47B27AD73F540
	//&mobile=18280041890&content=me&ext=&stime=&rrid=
	String sn;
	String pwd;
	String url;
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}	
	
}
