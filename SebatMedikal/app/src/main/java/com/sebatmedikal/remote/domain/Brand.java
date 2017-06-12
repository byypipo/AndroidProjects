package com.sebatmedikal.remote.domain;

import java.io.Serializable;
import java.util.Date;

public class Brand implements Serializable{
	private long id;
	private String brandName;
	private byte[] image;
	private String note;
	private String createdBy;
	private Date createdDate;

	public Brand() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedDate() {
		createdDate = new Date();
	}

	public Date getCreatedDate() {
		return createdDate;
	}
}