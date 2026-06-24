package com.example.sencare.activities.auth;


import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.example.sencare.R;
import com.example.sencare.utils.FirebaseUtil;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {


    private EditText edtEmail;
    private MaterialButton btnReset;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        mAuth = FirebaseUtil.getAuth();


        edtEmail = findViewById(R.id.edtEmail);
        btnReset = findViewById(R.id.btnLogin);
        MaterialButton btnClose = findViewById(R.id.btnClose);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }


        btnReset.setOnClickListener(v -> resetPassword());
    }


    private void resetPassword() {
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";


        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }
        if (!email.endsWith("@gmail.com")) {
            edtEmail.setError("Email phải có đuôi @gmail.com");
            return;
        }


        btnReset.setEnabled(false);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    btnReset.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Email khôi phục đã được gửi! Kiểm tra hộp thư.",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
