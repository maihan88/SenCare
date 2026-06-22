package com.example.sencare.activities.pet;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.sencare.BuildConfig;
import com.example.sencare.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class PetFormActivity extends AppCompatActivity {
    private ScrollView scrollPetForm;
    private EditText edtPetName, edtPetAge, edtPetSpecies, edtPetPersonality;
    private MaterialButton btnSave, btnGallery, btnCamera;
    private ImageView btnBack, imgAvatarPreview;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String currentPetId = null;
    private Uri selectedImageUri = null;
    private Uri cameraImageUri = null;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_form);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initCloudinaryIfNeeded();
        initViews();
        initImageLaunchers();
        initPermissionLaunchers();

        btnBack.setOnClickListener(v -> finish());

        currentPetId = getIntent().getStringExtra("petId");

        if (currentPetId != null) {
            loadPetDataFromFirestore(currentPetId);
        }

        btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        btnCamera.setOnClickListener(v -> openCameraWithPermissionCheck());

        btnSave.setOnClickListener(v -> savePetToFirestore());
    }

    private void initViews() {
        scrollPetForm = findViewById(R.id.scrollPetForm);
        btnBack = findViewById(R.id.btnBack);
        imgAvatarPreview = findViewById(R.id.imgAvatarPreview);
        TextView tvFormTitle = findViewById(R.id.tvFormTitle);

        if (currentPetId != null) {
            tvFormTitle.setText("Chỉnh sửa thú cưng");
            loadPetDataFromFirestore(currentPetId);
        } else {
            tvFormTitle.setText("Thêm thú cưng");
        }
        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        btnSave = findViewById(R.id.btnSave);

        edtPetName = findViewById(R.id.edtPetName);
        edtPetAge = findViewById(R.id.edtPetAge);
        edtPetSpecies = findViewById(R.id.edtPetSpecies);
        edtPetPersonality = findViewById(R.id.edtPetPersonality);
    }

    private void initImageLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        Glide.with(this)
                                .load(selectedImageUri)
                                .into(imgAvatarPreview);
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
                                .into(imgAvatarPreview);
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
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "pet_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        return getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
        );
    }

    private void initCloudinaryIfNeeded() {
        try {
            MediaManager.get();
        } catch (Exception e) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
            MediaManager.init(this, config);
        }
    }

    private void loadPetDataFromFirestore(String petId) {
        db.collection("pets").document(petId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        edtPetName.setText(documentSnapshot.getString("name"));
                        edtPetSpecies.setText(documentSnapshot.getString("species"));
                        edtPetPersonality.setText(documentSnapshot.getString("personality"));

                        Long age = documentSnapshot.getLong("age");
                        if (age != null) {
                            edtPetAge.setText(String.valueOf(age));
                        }

                        String imageUrl = documentSnapshot.getString("imageUrl");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .into(imgAvatarPreview);
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy thú cưng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không tải được dữ liệu thú cưng!", Toast.LENGTH_SHORT).show();
                });
    }

    private void savePetToFirestore() {
        String name = edtPetName.getText().toString().trim();
        String ageStr = edtPetAge.getText().toString().trim();
        String species = edtPetSpecies.getText().toString().trim();
        String personality = edtPetPersonality.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || species.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên, tuổi và giống loài!", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;

        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Tuổi phải là số!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (age < 0) {
            Toast.makeText(this, "Tuổi không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        String ownerId = currentUser.getUid();

        Map<String, Object> petData = new HashMap<>();
        petData.put("name", name);
        petData.put("age", age);
        petData.put("species", species);
        petData.put("personality", personality);
        petData.put("ownerId", ownerId);
        petData.put("updatedAt", Timestamp.now());

        if (currentPetId == null) {
            currentPetId = db.collection("pets").document().getId();
            petData.put("petId", currentPetId);
            petData.put("createdAt", Timestamp.now());
        }

        setLoading(true);

        if (selectedImageUri != null) {
            uploadImageToCloudinaryThenSave(petData);
        } else {
            savePetData(petData);
        }
    }

    private void uploadImageToCloudinaryThenSave(Map<String, Object> petData) {
        MediaManager.get()
                .upload(selectedImageUri)
                .unsigned(BuildConfig.CLOUDINARY_UPLOAD_PRESET)
                .option("folder", "sencare/pets")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Bắt đầu upload ảnh pet");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = String.valueOf(resultData.get("secure_url"));
                        String imagePublicId = String.valueOf(resultData.get("public_id"));

                        petData.put("imageUrl", imageUrl);
                        petData.put("imagePublicId", imagePublicId);

                        savePetData(petData);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        setLoading(false);
                        Toast.makeText(
                                PetFormActivity.this,
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

    private void savePetData(Map<String, Object> petData) {
        db.collection("pets").document(currentPetId)
                .set(petData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    setLoading(false);
                    Toast.makeText(this, "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean isLoading) {
        btnSave.setEnabled(!isLoading);
        btnGallery.setEnabled(!isLoading);
        btnCamera.setEnabled(!isLoading);

        if (isLoading) {
            btnSave.setText("Đang lưu...");
        } else {
            btnSave.setText("Lưu thông tin");
        }
    }
}