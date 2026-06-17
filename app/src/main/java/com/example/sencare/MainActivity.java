package com.example.sencare;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.example.sencare.activities.auth.ForgotPasswordActivity;
import com.example.sencare.activities.auth.LoginActivity;
import com.example.sencare.activities.auth.RegisterActivity;
import com.example.sencare.activities.dashboard.SpaOwnerHomeActivity;
import com.example.sencare.activities.dashboard.UserHomeActivity;
import com.example.sencare.activities.spaowner.SpaFormActivity;
import com.example.sencare.models.User;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {


    private MaterialButton btnLogin, btnRegister;
    private FirestoreHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new FirestoreHelper();


        setContentView(R.layout.activity_main);


        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);


        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }


    private void checkUserRoleAndRedirect(String uid) {
        dbHelper.getUser(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if ("user".equals(user.getRole())) {
                                startActivity(new Intent(MainActivity.this, UserHomeActivity.class));
                            } else if ("spa_owner".equals(user.getRole())) {
                                if (user.isHasSpaProfile()) {
                                    startActivity(new Intent(MainActivity.this, SpaOwnerHomeActivity.class));
                                } else {
                                    startActivity(new Intent(MainActivity.this, SpaFormActivity.class));
                                }
                            }
                            finish();
                        }
                    } else {
                        // Nếu có user Auth nhưng không có Firestore doc (trường hợp hiếm)
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi kiểm tra quyền: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.activity_main); // Hiển thị lại nút bấm nếu lỗi
                });
    }
}
