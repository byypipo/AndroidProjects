package com.sebatmedikal.remote.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Operation implements Serializable {
    private long id;
    private OperationType operationType;
    private Product product;
    private int count;
    private BigDecimal totalPrice;
    private String note;
    private String createdBy;
    private Date createdDate;

    public Operation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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
