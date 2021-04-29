package com.example.cloudservice.config;

public interface AppConstants {
    // ??
    String TOKEN_HEADER = "auth-token";
    int TOKEN_START_INDEX = 7;
    String SCOPE_CLAIM_NAME = "scp";
    String ISSUER = "example.com";
    int DELETE_TOKENS_RATE = 300000;
}
