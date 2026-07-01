package com.example.sencare.activities.booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sencare.R;

public class SpaSearchActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etDistance;
    private Button btnSearch, btnBookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spa_search);

        btnBack = findViewById(R.id.btnBack);
        etDistance = findViewById(R.id.etDistance);
        btnSearch = findViewById(R.id.btnSearch);
        btnBookingList = findViewById(R.id.btnBookingList);

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            String distanceStr = etDistance.getText().toString().trim();
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

        btnBookingList.setOnClickListener(v -> {
            startActivity(new Intent(this, BookingListActivity.class));
        });
    }
}
