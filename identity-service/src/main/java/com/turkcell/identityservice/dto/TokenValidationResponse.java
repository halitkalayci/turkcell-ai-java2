package com.turkcell.identityservice.dto;

import java.util.List;
import java.util.UUID;

public class TokenValidationResponse {

    private Boolean valid;
    private UUID userId;
    private String username;
    private List<String> roles;

    public TokenValidationResponse() {
    }

    public TokenValidationResponse(Boolean valid, UUID userId, String username, List<String> roles) {
        this.valid = valid;
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
