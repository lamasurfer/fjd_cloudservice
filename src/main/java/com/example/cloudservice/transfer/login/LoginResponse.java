package com.example.cloudservice.transfer.login;

import com.example.cloudservice.security.SecurityConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class LoginResponse {
    @JsonProperty(SecurityConfig.AUTH_TOKEN)
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponse that = (LoginResponse) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                '}';
    }
}
