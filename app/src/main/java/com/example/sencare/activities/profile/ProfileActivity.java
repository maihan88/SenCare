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


        // Nút Quay lại (Back) -> Về UserHomeActivity
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, UserHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }


        // Nút Chỉnh sửa
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, com.example.sencare.activities.profile.UserFormActivity.class);
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
                        // Hiển thị tên hiển thị (username) ưu tiên hơn fullName
                        String displayName = (user.getUsername() != null && !user.getUsername().isEmpty())
                                ? user.getUsername() : user.getFullName();

                        if (tvName != null) {
                            tvName.setText(displayName != null ? displayName : "User");
                        }
                        if (tvEmail != null) {
                            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "No Email");
                        }


                        // Hiển thị ảnh đại diện với Glide
                        ImageView ivAvatar = findViewById(R.id.ivAvatar);
                        if (ivAvatar != null) {
                            String avatarUrl = (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty())
                                    ? user.getAvatarUrl() : "https://res.cloudinary.com/dqofre7ms/image/upload/v1741513264/sample.jpg";

                            Glide.with(this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.icon)
                                    .into(ivAvatar);
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
