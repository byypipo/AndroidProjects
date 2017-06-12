package com.sebatmedikal.remote.domain;

import java.io.Serializable;
import java.util.Date;

public class OperationType implements Serializable {
    private long id;
    private String operationTypeName;
    private boolean sale;
    private String note;
    private String createdBy;
    private Date createdDate;

    public OperationType() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOperationTypeName() {
        return operationTypeName;
    }

    public void setOperationTypeName(String operationTypeName) {
        this.operationTypeName = operationTypeName;
    }

    public boolean isSale() {
        return sale;
    }

    public void setSale(boolean sale) {
        this.sale = sale;
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
