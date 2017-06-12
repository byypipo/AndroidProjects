package com.sebatmedikal.remote.domain;

import java.io.Serializable;
import java.util.Date;

public class Stock implements Serializable {
    private long id;
    private int count;
    private String createdBy;
    private Date createdDate;

    public Stock() {
    }

    public Stock(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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