package com.gooagoo.monitor.server.request;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class HTTPClientMonitorParam {
	public long stime;
	public String app;
	public String owner;
	public String contact;
	public List<MonitorValues> methods = new ArrayList<MonitorValues>();

	
	public long getStime() {
		return stime;
	}


	public void setStime(long stime) {
		this.stime = stime;
	}


	public String getApp() {
		return app;
	}


	public void setApp(String app) {
		this.app = app;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getContact() {
		return contact;
	}


	public void setContact(String contact) {
		this.contact = contact;
	}


	public List<MonitorValues> getMethods() {
		return methods;
	}


	public void setMethods(List<MonitorValues> methods) {
		this.methods = methods;
	}

	
	

	@Override
	public String toString() {
		return "HTTPClientMonitorParam [stime=" + stime + ", app=" + app + ", owner=" + owner + ", contact=" + contact
				+ ", methods=" + methods + "]";
	}


	public static void main(String[] args) {
		HTTPClientMonitorParam re = new HTTPClientMonitorParam();
		re.setApp("APP");
		re.setStime(123);
		re.setContact("");
		Gson gson = new Gson();
		System.out.println(gson.toJson(re));
	}
}
