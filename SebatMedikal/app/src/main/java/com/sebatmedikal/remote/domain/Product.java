package com.sebatmedikal.remote.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Product implements Serializable {
    private long id;
    private String productName;
    private BigDecimal price;
    private String barcod;
    private byte[] image;
    private Brand brand;
    private String note;
    private Stock stock;
    private String createdBy;
    private Date createdDate;

    public Product() {
    }

    public Product(String productName, Brand brand, String createdBy) {
        this.productName = productName;
        this.brand = brand;
        this.createdBy = createdBy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getBarcod() {
        return barcod;
    }

    public void setBarcod(String barcod) {
        this.barcod = barcod;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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