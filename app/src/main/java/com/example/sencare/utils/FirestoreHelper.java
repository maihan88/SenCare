package com.example.sencare.utils;

import com.example.sencare.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_SPAS = "spas";
    private final FirebaseFirestore db;

    public FirestoreHelper() {
        this.db = FirebaseUtil.getFirestore();
    }

    // Lưu thông tin người dùng
    public Task<Void> saveUser(User user) {
        return db.collection(COLLECTION_USERS).document(user.getUid()).set(user);
    }

    // Lấy thông tin người dùng
    public Task<DocumentSnapshot> getUser(String uid) {
        return db.collection(COLLECTION_USERS).document(uid).get();
    }

    // Cập nhật thông tin spa cho người dùng
    public Task<Void> updateUserSpaInfo(String uid, String spaId, boolean hasSpaProfile) {
        return db.collection(COLLECTION_USERS).document(uid)
                .update("spaId", spaId, "hasSpaProfile", hasSpaProfile);
    }

    // Lưu thông tin spa
    public Task<Void> saveSpa(com.example.sencare.models.Spa spa) {
        return db.collection(COLLECTION_SPAS).document(spa.getSpaId()).set(spa);
    }

    // Lấy thông tin spa theo spaId
    public Task<DocumentSnapshot> getSpa(String spaId) {
        return db.collection(COLLECTION_SPAS).document(spaId).get();
    }

    // Lấy thông tin spa theo ownerId
    public Task<com.google.firebase.firestore.QuerySnapshot> getSpaByOwner(String ownerId) {
        return db.collection(COLLECTION_SPAS).whereEqualTo("ownerId", ownerId).get();
    }

    // Lấy thông tin người dùng theo tên (fullName) - dùng cho login bằng username
    public Task<com.google.firebase.firestore.QuerySnapshot> getUserByUsername(String username) {
        return db.collection(COLLECTION_USERS).whereEqualTo("fullName", username).limit(1).get();
    }

    // Bạn có thể thêm các hàm truy vấn khác ở đây
}