package com.example.sencare.models;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.sencare.BR;
import com.google.firebase.Timestamp;
import java.util.ArrayList;

public class Spa extends BaseObservable {
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

    @Bindable
    public String getSpaId() {
        return spaId;
    }

    public void setSpaId(String spaId) {
        this.spaId = spaId;
        notifyPropertyChanged(BR.spaId);
    }

    @Bindable
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        notifyPropertyChanged(BR.ownerId);
    }

    @Bindable
    public String getSpaName() {
        return spaName;
    }

    public void setSpaName(String spaName) {
        this.spaName = spaName;
        notifyPropertyChanged(BR.spaName);
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public ArrayList<String> getServices() {
        return services;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
        notifyPropertyChanged(BR.services);
    }

    @Bindable
    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
        notifyPropertyChanged(BR.priceRange);
    }

    @Bindable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        notifyPropertyChanged(BR.imageUrl);
    }

    @Bindable
    public String getImagePublicId() {
        return imagePublicId;
    }

    public void setImagePublicId(String imagePublicId) {
        this.imagePublicId = imagePublicId;
        notifyPropertyChanged(BR.imagePublicId);
    }

    @Bindable
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        notifyPropertyChanged(BR.latitude);
    }

    @Bindable
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        notifyPropertyChanged(BR.longitude);
    }

    @Bindable
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        notifyPropertyChanged(BR.open);
    }

    @Bindable
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        notifyPropertyChanged(BR.createdAt);
    }

    @Bindable
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
        notifyPropertyChanged(BR.updatedAt);
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
