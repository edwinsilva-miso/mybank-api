package com.mybank.shared.exception;

public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
    
    public ResourceNotFoundException(String message, String domain) {
        super(message, "RESOURCE_NOT_FOUND", domain);
    }
} 