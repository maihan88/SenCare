package com.example.sencare.models;
import com.google.firebase.Timestamp;

public class Diary {
    private String diaryId;
    private String ownerId;
    private String petId;
    private String imageUrl;
    private String imagePublicId;
    private String caption;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Diary() {
    }

    public Diary(String diaryId, String ownerId, String petId, String imageUrl, String imagePublicId, String caption, Timestamp createdAt, Timestamp updatedAt) {
        this.diaryId = diaryId;
        this.ownerId = ownerId;
        this.petId = petId;
        this.imageUrl = imageUrl;
        this.imagePublicId = imagePublicId;
        this.caption = caption;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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
        return "Diary{" +
                "diaryId='" + diaryId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", petId='" + petId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imagePublicId='" + imagePublicId + '\'' +
                ", caption='" + caption + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
