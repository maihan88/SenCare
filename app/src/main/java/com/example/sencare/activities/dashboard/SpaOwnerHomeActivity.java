package com.example.sencare.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sencare.MainActivity;
import com.example.sencare.R;
import com.example.sencare.activities.auth.LoginActivity;
import com.example.sencare.activities.spaowner.SpaProfileActivity;
import com.example.sencare.models.User;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.button.MaterialButton;

public class SpaOwnerHomeActivity extends AppCompatActivity {

    private MaterialButton btnLogout;
    private LinearLayout btnProfile;
    private FirestoreHelper dbHelper;
    private String spaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spa_owner_home);

        dbHelper = new FirestoreHelper();
        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnProfile);

        btnLogout.setOnClickListener(v -> {
            FirebaseUtil.getAuth().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpaProfileActivity.class);
            intent.putExtra("SPA_ID", spaId);
            startActivity(intent);
        });

        loadUserData();
    }

    private void loadUserData() {
        String uid = FirebaseUtil.getCurrentUserId();
        if (uid != null) {
            dbHelper.getUser(uid).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        spaId = user.getSpaId();
                    }
                }
            });
        }
    }
}
