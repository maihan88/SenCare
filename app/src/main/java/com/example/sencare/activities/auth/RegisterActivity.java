package com.example.sencare.activities.auth;


import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.example.sencare.R;
import com.example.sencare.models.User;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {


    private EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    private RadioButton rbUser, rbSpa;
    private MaterialButton btnRegister;
    private FirebaseAuth mAuth;
    private FirestoreHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseUtil.getAuth();
        dbHelper = new FirestoreHelper();


        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        rbUser = findViewById(R.id.radiospinnerUser);
        rbSpa = findViewById(R.id.radiospinnerSpa);
        btnRegister = findViewById(R.id.btnLogin);


        btnRegister.setOnClickListener(v -> registerUser());
        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
    }


    private void registerUser() {
        String username = getText(edtUsername);
        String email = getText(edtEmail);
        String password = getText(edtPassword);
        String confirm = getText(edtConfirmPassword);


        if (username.isEmpty()) {
            edtUsername.setError("Nhập tên đăng nhập");
            return;
        }
        if (email.isEmpty() || !email.endsWith("@gmail.com")) {
            edtEmail.setError("Email phải có đuôi @gmail.com");
            return;
        }
        if (password.length() < 7 || password.length() > 13) {
            edtPassword.setError("Mật khẩu từ 7-13 ký tự");
            return;
        }
        if (!password.equals(confirm)) {
            edtConfirmPassword.setError("Xác nhận mật khẩu không khớp");
            return;
        }


        String role = rbUser.isChecked() ? "user" : "spa_owner";


        btnRegister.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    btnRegister.setEnabled(true);
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(uid, username, email, role);
                    } else {
                        String err = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại";
                        Toast.makeText(RegisterActivity.this, err, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveUserToFirestore(String uid, String username, String email, String role) {
        User user = new User();
        user.setUid(uid);
        user.setFullName(username);
        user.setEmail(email);
        user.setRole(role);
        user.setHasSpaProfile(false);


        // Sử dụng FirestoreHelper để lưu dữ liệu
        dbHelper.saveUser(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Hãy thiết lập hồ sơ.", Toast.LENGTH_SHORT).show();

                    // Chuyển sang UserFormActivity để cài đặt profile (ảnh, tên hiển thị)
                    Intent intent = new Intent(RegisterActivity.this, com.example.sencare.activities.profile.UserFormActivity.class);
                    intent.putExtra("IS_EDIT_MODE", false);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (mAuth.getCurrentUser() != null) mAuth.getCurrentUser().delete();
                });
    }


    private String getText(EditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}

