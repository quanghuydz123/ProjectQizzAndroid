package com.example.projectqizz.Model;

public class ProfileModel {
    private String name;
    private String email;
    private Boolean IsAdmin;
    private String phone;
    private int bookMarksCount;

    public ProfileModel(String name, String email, Boolean isAdmin,String phone,int bookMarksCount) {
        this.name = name;
        this.email = email;
        this.IsAdmin = isAdmin;
        this.phone = phone;
        this.bookMarksCount = bookMarksCount;
    }

    public int getBookMarksCount() {
        return bookMarksCount;
    }

    public void setBookMarksCount(int bookMarksCount) {
        this.bookMarksCount = bookMarksCount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
