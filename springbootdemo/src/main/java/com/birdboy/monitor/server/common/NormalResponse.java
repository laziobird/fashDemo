package com.birdboy.monitor.server.common;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooagoo.monitor.server.status.HttpStatusEnum;

/**
 * 
 * @author laziobird
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class NormalResponse implements Serializable {

	private static final long serialVersionUID = -1109554374628669356L;
	private String msg;
	private int ret = HttpStatusEnum.ERROR.getValue();

	public static NormalResponse create() {
		return new NormalResponse();
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}




}
