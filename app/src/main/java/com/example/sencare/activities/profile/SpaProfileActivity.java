package com.example.sencare.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.activities.form.SpaFormActivity;
import com.example.sencare.databinding.ActivitySpaProfileBinding;
import com.example.sencare.models.Spa;
import com.example.sencare.utils.FirestoreHelper;

public class SpaProfileActivity extends AppCompatActivity {

    private ActivitySpaProfileBinding binding;
    private FirestoreHelper dbHelper;
    private String currentSpaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spa_profile);

        dbHelper = new FirestoreHelper();
        currentSpaId = getIntent().getStringExtra("SPA_ID");

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEditInfo.setOnClickListener(v -> {
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
                    // Tên, địa chỉ, SĐT, mô tả được bind trong XML
                    binding.setSpa(spa);
                    binding.tvPriceRange.setText("Khoảng giá: " + spa.getPriceRange());

                    if (spa.getServices() != null) {
                        binding.tvServices.setText("Dịch vụ: " + String.join(", ", spa.getServices()));
                    }

                    Glide.with(this).load(spa.getImageUrl()).placeholder(R.drawable.icon).into(binding.ivSpaAvatar);
                }
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin spa", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
