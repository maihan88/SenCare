package com.example.sencare.models;


public class User {
    private String uid;
    private String email;
    private String fullName;
    private String username;
    private String role;
    private String avatarUrl;
    private String avatarPublicId;
    private boolean hasSpaProfile;
    private String spaId;


    public User() {
    }


    public User(String uid, String email, String fullName, String username, String role, String avatarUrl, String avatarPublicId, boolean hasSpaProfile, String spaId) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
        this.hasSpaProfile = hasSpaProfile;
        this.spaId = spaId;
    }


    public String getUid() {
        return uid;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getFullName() {
        return fullName;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public String getAvatarUrl() {
        return avatarUrl;
    }


    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }


    public String getAvatarPublicId() {
        return avatarPublicId;
    }


    public void setAvatarPublicId(String avatarPublicId) {
        this.avatarPublicId = avatarPublicId;
    }


    public boolean isHasSpaProfile() {
        return hasSpaProfile;
    }


    public void setHasSpaProfile(boolean hasSpaProfile) {
        this.hasSpaProfile = hasSpaProfile;
    }


    public String getSpaId() {
        return spaId;
    }


    public void setSpaId(String spaId) {
        this.spaId = spaId;
    }


    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", avatarPublicId='" + avatarPublicId + '\'' +
                ", hasSpaProfile=" + hasSpaProfile +
                ", spaId='" + spaId + '\'' +
                '}';
    }
}
