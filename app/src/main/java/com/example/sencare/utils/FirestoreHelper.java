package com.example.sencare.utils;

import com.example.sencare.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {
    private static final String COLLECTION_USERS = "users";
    private final FirebaseFirestore db;

    public FirestoreHelper() {
        this.db = FirebaseUtil.getFirestore();
    }

    // Lưu thông tin người dùng (tương đương INSERT/UPDATE trong SQLite)
    public Task<Void> saveUser(User user) {
        return db.collection(COLLECTION_USERS).document(user.getUid()).set(user);
    }

    // Lấy thông tin người dùng (tương đương SELECT * FROM users WHERE uid = ?)
    public Task<DocumentSnapshot> getUser(String uid) {
        return db.collection(COLLECTION_USERS).document(uid).get();
    }

    // Bạn có thể thêm các hàm truy vấn khác ở đây
}