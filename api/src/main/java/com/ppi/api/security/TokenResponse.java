package com.ppi.api.security;

import com.ppi.api.model.NestupUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenResponse {
    private String token;
    private NestupUser user;

    public TokenResponse() {
    }

    public TokenResponse(String token, NestupUser user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NestupUser getUser() {
        return user;
    }

    public void setUser(NestupUser user) {
        this.user = user;
    }
}
