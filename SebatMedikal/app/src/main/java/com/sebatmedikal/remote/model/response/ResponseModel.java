package com.sebatmedikal.remote.model.response;

public class ResponseModel {
	private boolean isOk;

	public ResponseModel() {
	}

	public ResponseModel(boolean isOk) {
		this.isOk = isOk;
	}

	public boolean getIsOk() {
		return isOk;
	}
}
