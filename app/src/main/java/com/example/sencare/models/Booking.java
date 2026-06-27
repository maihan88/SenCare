package com.example.sencare.models;
import com.google.firebase.Timestamp;

public class Booking {
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

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getSpaId() {
        return spaId;
    }

    public void setSpaId(String spaId) {
        this.spaId = spaId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getSpaName() {
        return spaName;
    }

    public void setSpaName(String spaName) {
        this.spaName = spaName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Timestamp getBookingTimestamp() {
        return bookingTimestamp;
    }

    public void setBookingTimestamp(Timestamp bookingTimestamp) {
        this.bookingTimestamp = bookingTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
