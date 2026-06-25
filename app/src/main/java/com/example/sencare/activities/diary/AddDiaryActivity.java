package com.example.sencare.activities.diary;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.sencare.R;
import com.example.sencare.utils.CloudinaryUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDiaryActivity extends AppCompatActivity {

    private ImageView btnBack, imgPreview;
    private View btnUp, btnGallery, btnCamera;
    private EditText edtCaption;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String petId;
    private Uri selectedImageUri = null;
    private Uri cameraImageUri = null;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        CloudinaryUtil.init(this);

        petId = getIntent().getStringExtra("petId");

        if (petId == null || petId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thú cưng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initImageLaunchers();
        initPermissionLaunchers();

        btnBack.setOnClickListener(v -> finish());

        btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        btnCamera.setOnClickListener(v -> openCameraWithPermissionCheck());

        btnUp.setOnClickListener(v -> saveDiary());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnUp = findViewById(R.id.btnUp);
        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        imgPreview = findViewById(R.id.imgPreview);
        edtCaption = findViewById(R.id.edtCaption);
    }

    private void initImageLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        Glide.with(this)
                                .load(selectedImageUri)
                                .into(imgPreview);
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        selectedImageUri = cameraImageUri;
                        Glide.with(this)
                                .load(selectedImageUri)
                                .into(imgPreview);
                    } else {
                        Toast.makeText(this, "Đã hủy chụp ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void initPermissionLaunchers() {
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "Bạn cần cấp quyền camera để chụp ảnh!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void openCameraWithPermissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        cameraImageUri = createImageUri();

        if (cameraImageUri != null) {
            cameraLauncher.launch(cameraImageUri);
        } else {
            Toast.makeText(this, "Không tạo được file ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "diary_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        return getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
        );
    }

    private void saveDiary() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn hoặc chụp ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        uploadImageToCloudinary(currentUser.getUid());
    }

    private void uploadImageToCloudinary(String ownerId) {
        MediaManager.get()
                .upload(selectedImageUri)
                .option("folder", "sencare/diaries")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Bắt đầu upload ảnh nhật ký");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = String.valueOf(resultData.get("secure_url"));
                        String imagePublicId = String.valueOf(resultData.get("public_id"));

                        saveDiaryToFirestore(ownerId, imageUrl, imagePublicId);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        setLoading(false);
                        Toast.makeText(
                                AddDiaryActivity.this,
                                "Upload ảnh thất bại: " + error.getDescription(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.d("Cloudinary", "Upload ảnh được lên lịch lại");
                    }
                })
                .dispatch();
    }

    private void saveDiaryToFirestore(String ownerId, String imageUrl, String imagePublicId) {
        String caption = edtCaption.getText().toString().trim();

        String diaryId = db.collection("diaries").document().getId();

        Map<String, Object> diaryData = new HashMap<>();
        diaryData.put("diaryId", diaryId);
        diaryData.put("petId", petId);
        diaryData.put("ownerId", ownerId);
        diaryData.put("imageUrl", imageUrl);
        diaryData.put("imagePublicId", imagePublicId);
        diaryData.put("caption", caption);
        diaryData.put("description", caption);
        diaryData.put("createdAt", Timestamp.now());
        diaryData.put("updatedAt", Timestamp.now());

        db.collection("diaries")
                .document(diaryId)
                .set(diaryData)
                .addOnSuccessListener(aVoid -> {
                    setLoading(false);
                    Toast.makeText(this, "Đã thêm nhật ký!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Lưu nhật ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean isLoading) {
        btnUp.setEnabled(!isLoading);
        btnGallery.setEnabled(!isLoading);
        btnCamera.setEnabled(!isLoading);

        if (isLoading) {
            Toast.makeText(this, "Đang lưu nhật ký...", Toast.LENGTH_SHORT).show();
        }
    }
}