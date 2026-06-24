package com.example.sencare.activities.dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.example.sencare.utils.ImageUtil;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.Map;

public class UserFormActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final String DEFAULT_AVATAR = "https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg"; // Link ảnh mặc định

    private ImageView ivAvatar;
    private EditText etDisplayName;
    private MaterialButton btnChoosePhoto, btnTakePhoto, btnSave, btnClose;
    private Uri imageUri;
    private String currentUid;
    private FirestoreHelper dbHelper;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        CloudinaryUtil.init(this);
        dbHelper = new FirestoreHelper();
        currentUid = FirebaseUtil.getCurrentUserId();

        // Kiểm tra xem là chế độ chỉnh sửa hay thiết lập ban đầu
        isEditMode = getIntent().getBooleanExtra("IS_EDIT_MODE", false);

        initViews();
        setupListeners();

        if (isEditMode) {
            loadUserData();
        }
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        etDisplayName = findViewById(R.id.etDisplayName);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);
    }

    private void setupListeners() {
        btnChoosePhoto.setOnClickListener(v -> openGallery());
        btnTakePhoto.setOnClickListener(v -> openCamera());
        btnSave.setOnClickListener(v -> validateAndSave());
        btnClose.setOnClickListener(v -> finish());
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

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                imageUri = data.getData();
                ivAvatar.setImageURI(imageUri);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    ivAvatar.setImageBitmap(bitmap);
                    imageUri = ImageUtil.getImageUri(this, bitmap);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Cần quyền truy cập bộ nhớ để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Cần quyền Camera để chụp ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateAndSave() {
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
                    // Sau khi thiết lập xong hồ sơ -> Đăng xuất và quay về Login
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
