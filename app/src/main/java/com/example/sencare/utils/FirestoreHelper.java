package com.example.sencare.utils;

import com.example.sencare.models.Booking;
import com.example.sencare.models.Spa;
import com.example.sencare.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

public class FirestoreHelper {
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_SPAS = "spas";
    private static final String COLLECTION_PETS = "pets";
    private static final String COLLECTION_DIARIES = "diaries";
    private static final String COLLECTION_BOOKINGS = "bookings";
    private static final String COLLECTION_VET_CLINICS = "vetClinics";
    private final FirebaseFirestore db;

    public FirestoreHelper() {
        this.db = FirebaseUtil.getFirestore();
    }

    public Task<Void> saveUser(User user) {
        return db.collection(COLLECTION_USERS).document(user.getUid()).set(user);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return db.collection(COLLECTION_USERS).document(uid).get();
    }

    public Task<Void> updateUserSpaInfo(String uid, String spaId, boolean hasSpaProfile) {
        return db.collection(COLLECTION_USERS).document(uid)
                .update("spaId", spaId, "hasSpaProfile", hasSpaProfile);
    }

    public Task<QuerySnapshot> getUserByUsername(String username) {
        return db.collection(COLLECTION_USERS).whereEqualTo("username", username).limit(1).get();
    }

    public Task<Void> saveSpa(Spa spa) {
        return db.collection(COLLECTION_SPAS).document(spa.getSpaId()).set(spa);
    }

    public Task<DocumentSnapshot> getSpa(String spaId) {
        return db.collection(COLLECTION_SPAS).document(spaId).get();
    }

    public Task<QuerySnapshot> getSpaByOwner(String ownerId) {
        return db.collection(COLLECTION_SPAS).whereEqualTo("ownerId", ownerId).get();
    }

    public Task<QuerySnapshot> getAllSpas() {
        return db.collection(COLLECTION_SPAS).get();
    }

    public Query getSpasQuery() {
        return db.collection(COLLECTION_SPAS);
    }

    public String newPetId() {
        return db.collection(COLLECTION_PETS).document().getId();
    }

    public Task<DocumentSnapshot> getPet(String petId) {
        return db.collection(COLLECTION_PETS).document(petId).get();
    }

    public Task<Void> savePet(String petId, Map<String, Object> petData) {
        return db.collection(COLLECTION_PETS).document(petId).set(petData, SetOptions.merge());
    }

    public Task<Void> deletePet(String petId) {
        return db.collection(COLLECTION_PETS).document(petId).delete();
    }

    public Query getPetsByOwner(String ownerId) {
        return db.collection(COLLECTION_PETS).whereEqualTo("ownerId", ownerId);
    }

    public String newDiaryId() {
        return db.collection(COLLECTION_DIARIES).document().getId();
    }

    public Task<DocumentSnapshot> getDiary(String diaryId) {
        return db.collection(COLLECTION_DIARIES).document(diaryId).get();
    }

    public Task<Void> saveDiary(String diaryId, Map<String, Object> diaryData) {
        return db.collection(COLLECTION_DIARIES).document(diaryId).set(diaryData);
    }

    public Query getDiariesByPet(String petId) {
        return db.collection(COLLECTION_DIARIES).whereEqualTo("petId", petId);
    }

    public Task<DocumentReference> addBooking(Booking booking) {
        return db.collection(COLLECTION_BOOKINGS).add(booking);
    }

    public Query getBookingsByUser(String userId) {
        return db.collection(COLLECTION_BOOKINGS).whereEqualTo("userId", userId);
    }

    public Query getBookingsBySpa(String spaId) {
        return db.collection(COLLECTION_BOOKINGS).whereEqualTo("spaId", spaId);
    }

    public Query getBookingsBySpaAndDate(String spaId, String bookingDate) {
        return db.collection(COLLECTION_BOOKINGS)
                .whereEqualTo("spaId", spaId)
                .whereEqualTo("bookingDate", bookingDate);
    }

    public Query getBookingsByPet(String petId) {
        return db.collection(COLLECTION_BOOKINGS).whereEqualTo("petId", petId);
    }

    public Task<Void> updateBookingStatus(String bookingId, String status, Timestamp updatedAt) {
        return db.collection(COLLECTION_BOOKINGS).document(bookingId)
                .update("status", status, "updatedAt", updatedAt);
    }

    public Task<QuerySnapshot> getActiveVetClinics() {
        return db.collection(COLLECTION_VET_CLINICS).whereEqualTo("active", true).get();
    }
}
