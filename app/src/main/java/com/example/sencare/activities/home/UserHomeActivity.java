package com.example.sencare.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sencare.MainActivity;
import com.example.sencare.R;
import com.example.sencare.activities.booking.SpaSearchActivity;
import com.example.sencare.activities.diary.PetDiaryListActivity;
import com.example.sencare.activities.pet.PetListActivity;
import com.example.sencare.activities.profile.ProfileActivity;
import com.example.sencare.activities.vet.VetMapActivity;
import com.example.sencare.utils.FirebaseUtil;

public class UserHomeActivity extends AppCompatActivity {

    private TextView btnLogout;
    private LinearLayout layoutPet, layoutDiary, layoutVet, layoutSpa;
    private LinearLayout layoutProfile, layoutHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // Ánh xạ các view
        btnLogout = findViewById(R.id.btnLogout);
        layoutPet = findViewById(R.id.layoutPet);
        layoutDiary = findViewById(R.id.layoutDiary);
        layoutVet = findViewById(R.id.layoutVet);
        layoutSpa = findViewById(R.id.layoutSpa);
        layoutProfile = findViewById(R.id.layoutProfile);
        layoutHome = findViewById(R.id.layoutHome);

        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> {
            FirebaseUtil.getAuth().signOut();
            Intent intent = new Intent(UserHomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Xử lý chuyển màn hình Menu
        layoutPet.setOnClickListener(v -> startActivity(new Intent(this, PetListActivity.class)));
        layoutDiary.setOnClickListener(v -> startActivity(new Intent(this, PetDiaryListActivity.class)));
        layoutVet.setOnClickListener(v -> startActivity(new Intent(this, VetMapActivity.class)));
        layoutSpa.setOnClickListener(v -> startActivity(new Intent(this, SpaSearchActivity.class)));

        // Xử lý chuyển màn hình Bottom Navigation
        layoutProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        layoutHome.setOnClickListener(v -> {
            // Đang ở trang chủ
        });
    }
}
