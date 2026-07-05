package com.example.sencare.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.activities.form.SpaFormActivity;
import com.example.sencare.models.Spa;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.button.MaterialButton;

public class SpaProfileActivity extends AppCompatActivity {

    private TextView tvSpaName, tvAddress, tvPhone, tvDescription, tvServices, tvPriceRange;
    private ImageView ivSpaAvatar;
    private ImageButton btnBack;
    private MaterialButton btnEditInfo;
    private FirestoreHelper dbHelper;
    private String currentSpaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spa_profile);

        dbHelper = new FirestoreHelper();
        currentSpaId = getIntent().getStringExtra("SPA_ID");

        tvSpaName = findViewById(R.id.tvSpaName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvDescription = findViewById(R.id.tvDescription);
        tvServices = findViewById(R.id.tvServices);
        tvPriceRange = findViewById(R.id.tvPriceRange);
        ivSpaAvatar = findViewById(R.id.ivSpaAvatar);
        btnBack = findViewById(R.id.btnBack);
        btnEditInfo = findViewById(R.id.btnEditInfo);

        btnBack.setOnClickListener(v -> finish());
        btnEditInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpaFormActivity.class);
            intent.putExtra("SPA_ID", currentSpaId);
            startActivity(intent);
        });

        if (currentSpaId == null) {
            Toast.makeText(this, "Không tìm thấy ID Spa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentSpaId != null) {
            loadSpaInfo(currentSpaId);
        }
    }

    // Tải và hiển thị thông tin spa (tải lại mỗi khi quay về màn hình)
    private void loadSpaInfo(String spaId) {
        dbHelper.getSpa(spaId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Spa spa = documentSnapshot.toObject(Spa.class);
                if (spa != null) {
                    tvSpaName.setText(spa.getSpaName());
                    tvAddress.setText(spa.getAddress());
                    tvPhone.setText(spa.getPhone());
                    tvDescription.setText(spa.getDescription());
                    tvPriceRange.setText("Khoảng giá: " + spa.getPriceRange());

                    if (spa.getServices() != null) {
                        tvServices.setText("Dịch vụ: " + String.join(", ", spa.getServices()));
                    }

                    Glide.with(this).load(spa.getImageUrl()).placeholder(R.drawable.icon).into(ivSpaAvatar);
                }
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin spa", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
