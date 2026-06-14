package com.example.sencare.models;
import com.google.firebase.Timestamp;
import java.util.ArrayList;

public class Spa {
    private String spaId;
    private String ownerId;
    private String spaName;
    private String address;
    private String phone;
    private String description;
    private ArrayList<String> services;
    private String priceRange;
    private String imageUrl;
    private String imagePublicId;
    private double latitude;
    private double longitude;
    private boolean open;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Spa() {
    }

    public Spa(String spaId, String ownerId, String spaName, String address, String phone, String description, ArrayList<String> services, String priceRange, String imageUrl, String imagePublicId, double latitude, double longitude, boolean open, Timestamp createdAt, Timestamp updatedAt) {
        this.spaId = spaId;
        this.ownerId = ownerId;
        this.spaName = spaName;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.services = services;
        this.priceRange = priceRange;
        this.imageUrl = imageUrl;
        this.imagePublicId = imagePublicId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.open = open;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getSpaId() {
        return spaId;
    }

    public void setSpaId(String spaId) {
        this.spaId = spaId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSpaName() {
        return spaName;
    }

    public void setSpaName(String spaName) {
        this.spaName = spaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePublicId() {
        return imagePublicId;
    }

    public void setImagePublicId(String imagePublicId) {
        this.imagePublicId = imagePublicId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Spa{" +
                "spaId='" + spaId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", spaName='" + spaName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", description='" + description + '\'' +
                ", services=" + services +
                ", priceRange='" + priceRange + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imagePublicId='" + imagePublicId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", open=" + open +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
