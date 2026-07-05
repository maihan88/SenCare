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

    // Lấy thông tin người dùng theo username - dùng cho login bằng username
    public Task<QuerySnapshot> getUserByUsername(String username) {
        return db.collection(COLLECTION_USERS).whereEqualTo("username", username).limit(1).get();
    }

    // Lưu thông tin spa
    public Task<Void> saveSpa(Spa spa) {
        return db.collection(COLLECTION_SPAS).document(spa.getSpaId()).set(spa);
    }

    // Lấy thông tin spa theo spaId
    public Task<DocumentSnapshot> getSpa(String spaId) {
        return db.collection(COLLECTION_SPAS).document(spaId).get();
    }

    // Lấy thông tin spa theo ownerId
    public Task<QuerySnapshot> getSpaByOwner(String ownerId) {
        return db.collection(COLLECTION_SPAS).whereEqualTo("ownerId", ownerId).get();
    }

    // Lấy danh sách tất cả spa
    public Task<QuerySnapshot> getAllSpas() {
        return db.collection(COLLECTION_SPAS).get();
    }

    // Truy vấn tất cả spa (dùng cho addSnapshotListener() realtime)
    public Query getSpasQuery() {
        return db.collection(COLLECTION_SPAS);
    }

    // Tạo id mới cho thú cưng
    public String newPetId() {
        return db.collection(COLLECTION_PETS).document().getId();
    }

    // Lấy thông tin thú cưng theo petId
    public Task<DocumentSnapshot> getPet(String petId) {
        return db.collection(COLLECTION_PETS).document(petId).get();
    }

    // Lưu (thêm/cập nhật) thông tin thú cưng
    public Task<Void> savePet(String petId, Map<String, Object> petData) {
        return db.collection(COLLECTION_PETS).document(petId).set(petData, SetOptions.merge());
    }

    // Xóa thú cưng
    public Task<Void> deletePet(String petId) {
        return db.collection(COLLECTION_PETS).document(petId).delete();
    }

    // Truy vấn danh sách thú cưng theo chủ sở hữu (dùng cho cả get() một lần và addSnapshotListener() realtime)
    public Query getPetsByOwner(String ownerId) {
        return db.collection(COLLECTION_PETS).whereEqualTo("ownerId", ownerId);
    }

    // Tạo id mới cho nhật ký
    public String newDiaryId() {
        return db.collection(COLLECTION_DIARIES).document().getId();
    }

    // Lấy chi tiết một nhật ký
    public Task<DocumentSnapshot> getDiary(String diaryId) {
        return db.collection(COLLECTION_DIARIES).document(diaryId).get();
    }

    // Lưu nhật ký mới
    public Task<Void> saveDiary(String diaryId, Map<String, Object> diaryData) {
        return db.collection(COLLECTION_DIARIES).document(diaryId).set(diaryData);
    }

    // Truy vấn danh sách nhật ký theo thú cưng (dùng cho addSnapshotListener() realtime)
    public Query getDiariesByPet(String petId) {
        return db.collection(COLLECTION_DIARIES).whereEqualTo("petId", petId);
    }

    // Thêm lịch đặt mới
    public Task<DocumentReference> addBooking(Booking booking) {
        return db.collection(COLLECTION_BOOKINGS).add(booking);
    }

    // Lấy danh sách lịch đặt theo người dùng
    public Task<QuerySnapshot> getBookingsByUser(String userId) {
        return db.collection(COLLECTION_BOOKINGS).whereEqualTo("userId", userId).get();
    }

    // Cập nhật trạng thái lịch đặt
    public Task<Void> updateBookingStatus(String bookingId, String status, Timestamp updatedAt) {
        return db.collection(COLLECTION_BOOKINGS).document(bookingId)
                .update("status", status, "updatedAt", updatedAt);
    }

    // Lấy danh sách phòng khám thú y đang hoạt động
    public Task<QuerySnapshot> getActiveVetClinics() {
        return db.collection(COLLECTION_VET_CLINICS).whereEqualTo("active", true).get();
    }

    // Bạn có thể thêm các hàm truy vấn khác ở đây
}
