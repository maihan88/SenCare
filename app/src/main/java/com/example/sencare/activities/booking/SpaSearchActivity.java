package com.example.sencare.activities.booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.sencare.R;
import com.example.sencare.databinding.ActivitySpaSearchBinding;

public class SpaSearchActivity extends AppCompatActivity {

    private ActivitySpaSearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spa_search);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSearch.setOnClickListener(v -> {
            String distanceStr = binding.etDistance.getText().toString().trim();
            if (distanceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập khoảng cách", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double distance = Double.parseDouble(distanceStr);
                if (distance <= 0) {
                    Toast.makeText(this, "Khoảng cách phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(this, SpaMapActivity.class);
                intent.putExtra("RADIUS", distance);
                startActivity(intent);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnBookingList.setOnClickListener(v -> {
            startActivity(new Intent(this, BookingListActivity.class));
        });
    }
}
