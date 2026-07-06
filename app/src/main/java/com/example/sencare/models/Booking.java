package com.example.sencare.models;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.sencare.BR;
import com.google.firebase.Timestamp;

public class Booking extends BaseObservable {
    private String bookingId;
    private String userId;
    private String petId;
    private String spaId;
    private String petName;
    private String spaName;
    private String serviceName;
    private String bookingDate;
    private String bookingTime;
    private Timestamp bookingTimestamp;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Booking() {
    }

    public Booking(String bookingId, String userId, String petId, String spaId, String petName, String spaName, String serviceName, String bookingDate, String bookingTime, Timestamp bookingTimestamp, String status, Timestamp createdAt, Timestamp updatedAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.petId = petId;
        this.spaId = spaId;
        this.petName = petName;
        this.spaName = spaName;
        this.serviceName = serviceName;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingTimestamp = bookingTimestamp;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Bindable
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
        notifyPropertyChanged(BR.bookingId);
    }

    @Bindable
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
        notifyPropertyChanged(BR.petId);
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
    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
        notifyPropertyChanged(BR.petName);
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
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
        notifyPropertyChanged(BR.serviceName);
    }

    @Bindable
    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
        notifyPropertyChanged(BR.bookingDate);
    }

    @Bindable
    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
        notifyPropertyChanged(BR.bookingTime);
    }

    @Bindable
    public Timestamp getBookingTimestamp() {
        return bookingTimestamp;
    }

    public void setBookingTimestamp(Timestamp bookingTimestamp) {
        this.bookingTimestamp = bookingTimestamp;
        notifyPropertyChanged(BR.bookingTimestamp);
    }

    @Bindable
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
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
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", petId='" + petId + '\'' +
                ", spaId='" + spaId + '\'' +
                ", petName='" + petName + '\'' +
                ", spaName='" + spaName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", bookingDate='" + bookingDate + '\'' +
                ", bookingTime='" + bookingTime + '\'' +
                ", bookingTimestamp=" + bookingTimestamp +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
