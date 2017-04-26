package com.gooagoo.monitor.server.status;

public enum HttpStatusEnum {
	OK("ok", 1),ERROR("err", -1);
    private String key;
    private int value;	
    private HttpStatusEnum(String key,int value){
    	this.key = key;
    	this.value = value;
    }
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
    
}
