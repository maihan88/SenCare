package com.example.sencare.activities.spaowner;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sencare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SpaProfileActivity extends AppCompatActivity {

    private TextView tvSpaName, tvSpaEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spa_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvSpaName = findViewById(R.id.tvSpaName);
        tvSpaEmail = findViewById(R.id.tvSpaEmail);

        loadSpaInfo();
    }

    private void loadSpaInfo() {
        // Kiểm tra đăng nhập để tránh NullPointerException
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");
                        // Xử lý null cho text
                        tvSpaName.setText(username != null ? username : "");
                        tvSpaEmail.setText(email != null ? email : "");
                    } else {
                        Toast.makeText(SpaProfileActivity.this, "Không tìm thấy thông tin spa", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    String error = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định";
                    Toast.makeText(SpaProfileActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                });
    }
}