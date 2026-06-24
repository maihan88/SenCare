package com.example.sencare.utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {
    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }
    public static String getCurrentUserId() {
        if (getAuth().getCurrentUser() != null) {
            return getAuth().getCurrentUser().getUid();
        }
        return null;
    }
}