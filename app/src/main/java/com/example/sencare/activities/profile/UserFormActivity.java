package com.example.sencare.activities.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class UserFormActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final String DEFAULT_AVATAR_URL = "https://res.cloudinary.com/dqofre7ms/image/upload/v1741513264/sample.jpg";

    private ImageView ivAvatar;
    private EditText etDisplayName;
    private MaterialButton btnChoosePhoto, btnTakePhoto, btnSave, btnClose;

    private Uri imageUri;
    private Bitmap imageBitmap;
    private boolean isEditMode = false;
    private FirestoreHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        CloudinaryUtil.init(this);
        dbHelper = new FirestoreHelper();
        isEditMode = getIntent().getBooleanExtra("IS_EDIT_MODE", false);

        ivAvatar = findViewById(R.id.ivAvatar);
        etDisplayName = findViewById(R.id.etDisplayName);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);

        loadUserData();

        btnChoosePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnTakePhoto.setOnClickListener(v -> openCamera());

        btnSave.setOnClickListener(v -> {
            String username = etDisplayName.getText().toString().trim();
            if (username.isEmpty()) {
                etDisplayName.setError("Vui lòng nhập tên hiển thị");
                return;
            }

            btnSave.setEnabled(false);
            if (imageUri != null) {
                uploadToCloudinary(imageUri, username);
            } else if (imageBitmap != null) {
                uploadToCloudinary(imageBitmap, username);
            } else {
                // Trường hợp không chọn ảnh mới
                String url = (currentUser != null && currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty())
                        ? currentUser.getAvatarUrl() : DEFAULT_AVATAR_URL;
                String publicId = (currentUser != null) ? currentUser.getAvatarPublicId() : "";
                saveToFirestore(username, url, publicId);
            }
        });

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }
    }

    // Tải dữ liệu người dùng hiện tại lên form
    private void loadUserData() {
        String uid = FirebaseUtil.getCurrentUserId();
        if (uid == null) return;

        dbHelper.getUser(uid).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentUser = documentSnapshot.toObject(User.class);
                if (currentUser != null) {
                    etDisplayName.setText(currentUser.getFullName());
                    if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
                        Glide.with(this).load(currentUser.getAvatarUrl()).into(ivAvatar);
                    }
                }
            }
        });
    }

    // Mở camera chụp ảnh (được gọi cả từ nút chụp ảnh lẫn callback cấp quyền)
    private void openCamera() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, 101);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else if (requestCode == 101) {
            Toast.makeText(this, "Bạn cần cấp quyền Camera để chụp ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                imageBitmap = null;
                ivAvatar.setImageURI(imageUri);
            } else if (requestCode == TAKE_PHOTO_REQUEST) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = null;
                ivAvatar.setImageBitmap(imageBitmap);
            }
        }
    }

    // Upload ảnh (Uri từ thư viện hoặc Bitmap từ camera) lên Cloudinary rồi lưu
    private void uploadToCloudinary(Object imageData, String username) {
        UploadCallback callback = new UploadCallback() {
            @Override
            public void onStart(String requestId) {}
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {}
            @Override
            public void onSuccess(String requestId, Map resultData) {
                String url = (String) resultData.get("secure_url");
                String publicId = (String) resultData.get("public_id");
                saveToFirestore(username, url, publicId);
            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                btnSave.setEnabled(true);
                Toast.makeText(UserFormActivity.this, "Lỗi upload ảnh", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {}
        };

        if (imageData instanceof Uri) {
            MediaManager.get().upload((Uri) imageData).callback(callback).dispatch();
        } else if (imageData instanceof Bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ((Bitmap) imageData).compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            MediaManager.get().upload(byteArray).callback(callback).dispatch();
        }
    }

    // Lưu thông tin người dùng vào Firestore (lấy user hiện tại trước nếu chưa có)
    private void saveToFirestore(String username, String url, String publicId) {
        String uid = FirebaseUtil.getCurrentUserId();
        if (uid == null) {
            btnSave.setEnabled(true);
            return;
        }

        if (currentUser == null) {
            dbHelper.getUser(uid).addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    currentUser = doc.toObject(User.class);
                    updateAndFinish(username, url, publicId);
                } else {
                    currentUser = new User();
                    currentUser.setUid(uid);
                    updateAndFinish(username, url, publicId);
                }
            }).addOnFailureListener(e -> btnSave.setEnabled(true));
        } else {
            updateAndFinish(username, url, publicId);
        }
    }

    private void updateAndFinish(String username, String url, String publicId) {
        currentUser.setFullName(username);
        currentUser.setAvatarUrl(url);
        currentUser.setAvatarPublicId(publicId);

        dbHelper.saveUser(currentUser).addOnSuccessListener(aVoid -> {
            Toast.makeText(UserFormActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
            if (!isEditMode) {
                FirebaseUtil.getAuth().signOut();
                Intent intent = new Intent(UserFormActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            finish();
        }).addOnFailureListener(e -> {
            btnSave.setEnabled(true);
            Toast.makeText(UserFormActivity.this, "Lỗi lưu dữ liệu", Toast.LENGTH_SHORT).show();
        });
    }
}
