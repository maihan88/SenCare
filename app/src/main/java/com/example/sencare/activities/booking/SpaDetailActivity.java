package com.example.sencare.activities.booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.databinding.ActivitySpaDetailBinding;
import com.example.sencare.models.Spa;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.chip.Chip;

public class SpaDetailActivity extends AppCompatActivity {

    private ActivitySpaDetailBinding binding;

    private FirestoreHelper dbHelper;
    private String spaId;
    private double distance;
    private Spa mSpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spa_detail);

        dbHelper = new FirestoreHelper();

        spaId = getIntent().getStringExtra("SPA_ID");
        distance = getIntent().getDoubleExtra("DISTANCE", 0);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnBookNow.setOnClickListener(v -> {
            if (mSpa != null) {
                Intent intent = new Intent(this, BookingFormActivity.class);
                intent.putExtra("SPA_ID", mSpa.getSpaId());
                intent.putExtra("SPA_NAME", mSpa.getSpaName());
                intent.putExtra("SPA_IMAGE", mSpa.getImageUrl());
                startActivity(intent);
            }
        });

        fetchSpaDetail();
    }

    private void fetchSpaDetail() {
        if (spaId == null)
            return;

        dbHelper.getSpa(spaId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        mSpa = documentSnapshot.toObject(Spa.class);
                        if (mSpa != null) {
                            mSpa.setSpaId(documentSnapshot.getId());
                            displaySpaDetail();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show());
    }

    private void displaySpaDetail() {
        binding.setSpa(mSpa);
        binding.tvDistance.setText(String.format("%.1f km", distance));

        if (mSpa.getImageUrl() != null && !mSpa.getImageUrl().isEmpty()) {
            Glide.with(this).load(mSpa.getImageUrl()).placeholder(R.drawable.placeholder).into(binding.imgSpa);
        }

        binding.cgServices.removeAllViews();
        if (mSpa.getServices() != null) {
            for (String service : mSpa.getServices()) {
                Chip chip = new Chip(this);
                chip.setText(service);
                chip.setChipBackgroundColorResource(R.color.accent_yellow);
                chip.setTextColor(getResources().getColor(R.color.text_green));
                binding.cgServices.addView(chip);
            }
        }
    }
}
