package com.example.sencare.activities.auth;


import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.example.sencare.R;
import com.example.sencare.activities.dashboard.SpaOwnerHomeActivity;
import com.example.sencare.activities.dashboard.UserHomeActivity;
import com.example.sencare.activities.spaowner.SpaFormActivity;
import com.example.sencare.models.User;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {


    private EditText edtEmail;
    private EditText edtPassword;
    private MaterialButton btnLogin;
    private MaterialButton btnClose;
    private TextView tvForgotPassword;
    private FirebaseAuth mAuth;
    private FirestoreHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseUtil.getAuth();
        dbHelper = new FirestoreHelper();


        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnClose = findViewById(R.id.btnClose);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);


        btnLogin.setOnClickListener(v -> loginUser());


        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }


        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }


    private void loginUser() {
        String input = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";


        if (input.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email hoặc tên đăng nhập");
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }


        btnLogin.setEnabled(false);
        
        // Kiểm tra xem input là email hay username
        if (input.contains("@")) {
            // Đăng nhập trực tiếp bằng Email
            performFirebaseLogin(input, password);
        } else {
            // Tìm email tương ứng với username trong Firestore
            dbHelper.getUserByUsername(input)
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String email = queryDocumentSnapshots.getDocuments().get(0).getString("email");
                            if (email != null) {
                                performFirebaseLogin(email, password);
                            } else {
                                btnLogin.setEnabled(true);
                                Toast.makeText(this, "Không tìm thấy email cho người dùng này", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            btnLogin.setEnabled(true);
                            Toast.makeText(this, "Tên đăng nhập không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        btnLogin.setEnabled(true);
                        Toast.makeText(this, "Lỗi kiểm tra tên đăng nhập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void performFirebaseLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    btnLogin.setEnabled(true);
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
                        if (uid != null) {
                            checkUserRole(uid);
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Không thể lấy UID người dùng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Đăng nhập thất bại";
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void checkUserRole(String uid) {
        // 11.3 Luồng đăng nhập: Kiểm tra role và điều hướng qua FirestoreHelper
        dbHelper.getUser(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            Intent intent;
                            if ("spa_owner".equals(user.getRole())) {
                                if (user.isHasSpaProfile()) {
                                    intent = new Intent(LoginActivity.this, SpaOwnerHomeActivity.class);
                                } else {
                                    intent = new Intent(LoginActivity.this, SpaFormActivity.class);
                                }
                            } else {
                                // Mặc định là User
                                intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Nếu tài khoản Auth tồn tại nhưng Firestore chưa có
                        startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Lỗi lấy thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

