package de.mfischbo.bustamail.mailing.dto;

import java.io.Serializable;

public class HyperlinkDTO implements Serializable {

	private static final long serialVersionUID = -2232577884854918467L;
	private String id;
	private String target;
	private boolean success;
	
	private int		httpStatus;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
}
