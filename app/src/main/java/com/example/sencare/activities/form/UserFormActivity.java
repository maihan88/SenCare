package com.example.sencare.activities.form;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.sencare.activities.auth.LoginActivity;
import com.example.sencare.models.User;
import com.example.sencare.utils.CloudinaryUtil;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.button.MaterialButton;

import java.util.Map;

public class UserFormActivity extends AppCompatActivity {

    private static final String DEFAULT_AVATAR = "https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg";

    private ImageView ivAvatar;
    private EditText etDisplayName;
    private MaterialButton btnChoosePhoto, btnTakePhoto, btnSave, btnClose;
    private Uri imageUri;
    private Uri cameraImageUri;
    private String currentUid;
    private FirestoreHelper dbHelper;
    private boolean isEditMode = false;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerLaunchers();

        setContentView(R.layout.activity_user_form);

        CloudinaryUtil.init(this);
        dbHelper = new FirestoreHelper();
        currentUid = FirebaseUtil.getCurrentUserId();

        isEditMode = getIntent().getBooleanExtra("IS_EDIT_MODE", false);

        ivAvatar = findViewById(R.id.ivAvatar);
        etDisplayName = findViewById(R.id.etDisplayName);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);

        btnChoosePhoto.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        btnTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnSave.setOnClickListener(v -> {
            String displayName = etDisplayName.getText().toString().trim();
            if (displayName.isEmpty()) {
                etDisplayName.setError("Vui lòng nhập tên hiển thị");
                return;
            }

            btnSave.setEnabled(false);
            if (imageUri != null) {
                uploadToCloudinary(displayName);
            } else {
                saveToFirestore(displayName, isEditMode ? null : DEFAULT_AVATAR);
            }
        });

        btnClose.setOnClickListener(v -> finish());

        if (isEditMode) {
            loadUserData();
        }
    }

    private void registerLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        ivAvatar.setImageURI(uri);
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        imageUri = cameraImageUri;
                        Glide.with(this).load(imageUri).into(ivAvatar);
                    }
                }
        );

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    } else {
                        Toast.makeText(this, "Cần quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadUserData() {
        if (currentUid == null) return;
        dbHelper.getUser(currentUid).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    etDisplayName.setText(user.getFullName());
                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        Glide.with(this).load(user.getAvatarUrl()).into(ivAvatar);
                    }
                }
            }
        });
    }

    private void launchCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "avatar_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        cameraImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (cameraImageUri != null) {
            cameraLauncher.launch(cameraImageUri);
        } else {
            Toast.makeText(this, "Không tạo được file ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToCloudinary(String displayName) {
        MediaManager.get().upload(imageUri).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {}
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {}
            @Override
            public void onSuccess(String requestId, Map resultData) {
                String url = (String) resultData.get("secure_url");
                saveToFirestore(displayName, url);
            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                btnSave.setEnabled(true);
                Toast.makeText(UserFormActivity.this, "Lỗi upload ảnh: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
    }

    private void saveToFirestore(String displayName, String avatarUrl) {
        if (currentUid == null) {
            btnSave.setEnabled(true);
            return;
        }

        dbHelper.getUser(currentUid).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user == null) user = new User();

            user.setUid(currentUid);
            user.setFullName(displayName);
            if (avatarUrl != null) {
                user.setAvatarUrl(avatarUrl);
            }

            dbHelper.saveUser(user).addOnSuccessListener(aVoid -> {
                Toast.makeText(UserFormActivity.this, "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show();
                if (isEditMode) {
                    finish();
                } else {
                    FirebaseUtil.getAuth().signOut();
                    Intent intent = new Intent(UserFormActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(e -> {
                btnSave.setEnabled(true);
                Toast.makeText(UserFormActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }
}
