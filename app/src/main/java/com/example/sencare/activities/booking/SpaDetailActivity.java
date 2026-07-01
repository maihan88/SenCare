package com.example.sencare.activities.booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.models.Spa;
import com.example.sencare.utils.FirebaseUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;

public class SpaDetailActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgSpa;
    private TextView tvSpaName, tvDistance, tvSpaAddress, tvSpaPhone, tvPriceRange, tvSpaDescription;
    private ChipGroup cgServices;
    private Button btnBookNow;

    private String spaId;
    private double distance;
    private Spa mSpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spa_detail);

        spaId = getIntent().getStringExtra("SPA_ID");
        distance = getIntent().getDoubleExtra("DISTANCE", 0);

        initViews();
        setupListeners();
        fetchSpaDetail();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgSpa = findViewById(R.id.imgSpa);
        tvSpaName = findViewById(R.id.tvSpaName);
        tvDistance = findViewById(R.id.tvDistance);
        tvSpaAddress = findViewById(R.id.tvSpaAddress);
        tvSpaPhone = findViewById(R.id.tvSpaPhone);
        tvPriceRange = findViewById(R.id.tvPriceRange);
        tvSpaDescription = findViewById(R.id.tvSpaDescription);
        cgServices = findViewById(R.id.cgServices);
        btnBookNow = findViewById(R.id.btnBookNow);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnBookNow.setOnClickListener(v -> {
            if (mSpa != null) {
                Intent intent = new Intent(this, BookingFormActivity.class);
                intent.putExtra("SPA_ID", mSpa.getSpaId());
                intent.putExtra("SPA_NAME", mSpa.getSpaName());
                intent.putExtra("SPA_IMAGE", mSpa.getImageUrl());
                startActivity(intent);
            }
        });
    }

    private void fetchSpaDetail() {
        if (spaId == null) return;

        FirebaseUtil.getFirestore().collection("spas").document(spaId).get()
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
        tvSpaName.setText(mSpa.getSpaName());
        tvDistance.setText(String.format("%.1f km", distance));
        tvSpaAddress.setText(mSpa.getAddress());
        tvSpaPhone.setText(mSpa.getPhone());
        tvPriceRange.setText(mSpa.getPriceRange());
        tvSpaDescription.setText(mSpa.getDescription());

        if (mSpa.getImageUrl() != null && !mSpa.getImageUrl().isEmpty()) {
            Glide.with(this).load(mSpa.getImageUrl()).placeholder(R.drawable.placeholder).into(imgSpa);
        }

        cgServices.removeAllViews();
        if (mSpa.getServices() != null) {
            for (String service : mSpa.getServices()) {
                Chip chip = new Chip(this);
                chip.setText(service);
                chip.setChipBackgroundColorResource(R.color.accent_yellow);
                chip.setTextColor(getResources().getColor(R.color.text_green));
                cgServices.addView(chip);
            }
        }
    }
}
