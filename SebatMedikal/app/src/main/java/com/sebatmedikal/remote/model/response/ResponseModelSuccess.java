package com.sebatmedikal.remote.model.response;

public class ResponseModelSuccess extends ResponseModel {
	private Object object;

	public ResponseModelSuccess() {
		super(true);
	}

	public Object getContent() {
		return object;
	}

	public ResponseModelSuccess setContent(Object object) {
		this.object = object;
		return this;
	}
}
