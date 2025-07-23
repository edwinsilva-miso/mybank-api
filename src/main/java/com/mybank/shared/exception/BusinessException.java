package com.mybank.shared.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final String domain;
    
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.domain = "GENERAL";
    }
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.domain = "GENERAL";
    }
    
    public BusinessException(String message, String errorCode, String domain) {
        super(message);
        this.errorCode = errorCode;
        this.domain = domain;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
        this.domain = "GENERAL";
    }
} 