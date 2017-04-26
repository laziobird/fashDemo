package com.gooagoo.monitor.server.request;

public class MonitorValues {
	public int times;
	public String method;
	public float ms_per_invoke;
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public float getMs_per_invoke() {
		return ms_per_invoke;
	}
	public void setMs_per_invoke(float ms_per_invoke) {
		this.ms_per_invoke = ms_per_invoke;
	}
	@Override
	public String toString() {
		return "MonitorValues [times=" + times + ", method=" + method + ", ms_per_invoke=" + ms_per_invoke + "]";
	}


}
