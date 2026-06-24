package com.example.sencare.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.activities.dashboard.UserHomeActivity;
import com.example.sencare.models.User;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail;
    private FirestoreHelper dbHelper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Khởi tạo Firebase và Helper
        mAuth = FirebaseUtil.getAuth();
        dbHelper = new FirestoreHelper();

        // Ánh xạ View đúng với ID mới trong activity_profile.xml
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        MaterialButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnEdit = findViewById(R.id.btnEditInfo);

        // Nút Quay lại (Back)
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Nút Chỉnh sửa
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, com.example.sencare.activities.dashboard.UserFormActivity.class);
                intent.putExtra("IS_EDIT_MODE", true);
                startActivity(intent);
            });
        }

        // Tải thông tin người dùng từ Firestore (Sẽ tải lại mỗi khi quay về màn hình này)
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserProfile();
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            dbHelper.getUser(uid).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        // Hiển thị tên (fullName) và email từ model User
                        if (tvName != null) {
                            tvName.setText(user.getFullName() != null ? user.getFullName() : "User");
                        }
                        if (tvEmail != null) {
                            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "No Email");
                        }
                        
                        // Hiển thị ảnh đại diện
                        ImageView ivAvatar = findViewById(R.id.ivAvatar);
                        if (ivAvatar != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                            Glide.with(this).load(user.getAvatarUrl()).into(ivAvatar);
                        }
                    }
                } else {
                    Toast.makeText(this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> 
                Toast.makeText(this, "Lỗi tải thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
