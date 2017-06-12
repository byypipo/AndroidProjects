package com.sebatmedikal.remote.model.response;

import com.sebatmedikal.remote.domain.User;

public class ResponseModelLogin extends ResponseModel {
    private String accessToken;
    private User user;

    public ResponseModelLogin() {
        super(true);
    }

    public User getUser() {
        return user;
    }

    public ResponseModelLogin setUser(User user) {
        this.user = user;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public ResponseModelLogin setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
