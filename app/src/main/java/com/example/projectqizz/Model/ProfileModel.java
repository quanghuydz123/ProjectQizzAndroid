package com.example.projectqizz.Model;

public class ProfileModel {
    private String name;
    private String email;
    private Boolean IsAdmin;

    public ProfileModel(String name, String email, Boolean isAdmin) {
        this.name = name;
        this.email = email;
        IsAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return IsAdmin;
    }

    public void setAdmin(Boolean admin) {
        IsAdmin = admin;
    }
}
