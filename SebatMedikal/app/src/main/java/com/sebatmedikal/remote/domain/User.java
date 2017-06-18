package com.sebatmedikal.remote.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    private long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private byte[] image;
    private String fcmRegistrationId;
    private Role role;
    private String note;
    private boolean online;
    private Date lastLoginDate;
    private Date readedOperationsDate;
    private Date readedBrandsDate;
    private Date readedProductsDate;
    private String createdBy;
    private Date createdDate;

    public User() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getFcmRegistrationId() {
        return fcmRegistrationId;
    }

    public void setFcmRegistrationId(String fcmRegistrationId) {
        this.fcmRegistrationId = fcmRegistrationId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getNote() {
        return note;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getReadedOperationsDate() {
        return readedOperationsDate;
    }

    public void setReadedOperationsDate(Date readedOperationsDate) {
        this.readedOperationsDate = readedOperationsDate;
    }

    public Date getReadedBrandsDate() {
        return readedBrandsDate;
    }

    public void setReadedBrandsDate(Date readedBrandsDate) {
        this.readedBrandsDate = readedBrandsDate;
    }

    public Date getReadedProductsDate() {
        return readedProductsDate;
    }

    public void setReadedProductsDate(Date readedProductsDate) {
        this.readedProductsDate = readedProductsDate;
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
