package com.sebatmedikal.remote.model.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestModel {
    private String accessToken;
    private String operation;
    private String parameter01;
    private String parameter02;
    private String parameter03;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getParameter01() {
        return parameter01;
    }

    public void setParameter01(String parameter01) {
        this.parameter01 = parameter01;
    }

    public String getParameter02() {
        return parameter02;
    }

    public void setParameter02(String parameter02) {
        this.parameter02 = parameter02;
    }

    public String getParameter03() {
        return parameter03;
    }

    public void setParameter03(String parameter03) {
        this.parameter03 = parameter03;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
