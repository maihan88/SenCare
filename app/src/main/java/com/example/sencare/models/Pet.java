package com.example.sencare.models;
import com.google.firebase.Timestamp;
public class Pet {
    private String petId;
    private String ownerId;
    private String name;
    private String species;
    private int age;
    private String personality;
    private String imageUrl;
    private String imagePublicId;
    private String status;
    private Timestamp passedAwayAt;
    private String farewellMessage;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Pet() {
    }

    public Pet(String petId, String ownerId, String name, String species, int age, String personality, String imageUrl, String imagePublicId, String status, Timestamp passedAwayAt, String farewellMessage, Timestamp createdAt, Timestamp updatedAt) {
        this.petId = petId;
        this.ownerId = ownerId;
        this.name = name;
        this.species = species;
        this.age = age;
        this.personality = personality;
        this.imageUrl = imageUrl;
        this.imagePublicId = imagePublicId;
        this.status = status;
        this.passedAwayAt = passedAwayAt;
        this.farewellMessage = farewellMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getPassedAwayAt() {
        return passedAwayAt;
    }

    public void setPassedAwayAt(Timestamp passedAwayAt) {
        this.passedAwayAt = passedAwayAt;
    }

    public String getFarewellMessage() {
        return farewellMessage;
    }

    public void setFarewellMessage(String farewellMessage) {
        this.farewellMessage = farewellMessage;
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
        return "Pet{" +
                "petId='" + petId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", age=" + age +
                ", personality='" + personality + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imagePublicId='" + imagePublicId + '\'' +
                ", status='" + status + '\'' +
                ", passedAwayAt=" + passedAwayAt +
                ", farewellMessage='" + farewellMessage + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
