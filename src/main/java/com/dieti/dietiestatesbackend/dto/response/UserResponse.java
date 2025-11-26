package com.dieti.dietiestatesbackend.dto.response;

public class UserResponse {
    private Long id = null;
    private String fullName;
    private String email;

    public UserResponse() {}

    public UserResponse(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public UserResponse(Long id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
