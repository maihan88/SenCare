package com.example.sencare.activities.diary;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.utils.FirestoreHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryDetailActivity extends AppCompatActivity {

    private ImageView btnBack, imgDiaryDetail;
    private TextView tvDiaryDate, tvDiaryCaption;

    private FirestoreHelper dbHelper;
    private String diaryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        dbHelper = new FirestoreHelper();

        btnBack = findViewById(R.id.btnBack);
        imgDiaryDetail = findViewById(R.id.imgDiaryDetail);
        tvDiaryDate = findViewById(R.id.tvDiaryDate);
        tvDiaryCaption = findViewById(R.id.tvDiaryCaption);

        btnBack.setOnClickListener(v -> finish());

        diaryId = getIntent().getStringExtra("diaryId");

        if (diaryId == null || diaryId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy nhật ký!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDiaryDetail();
    }

    private void loadDiaryDetail() {
        dbHelper.getDiary(diaryId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(this, "Nhật ký không tồn tại!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    showDiaryData(documentSnapshot);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không tải được chi tiết nhật ký!", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDiaryData(DocumentSnapshot documentSnapshot) {
        String imageUrl = documentSnapshot.getString("imageUrl");
        String caption = documentSnapshot.getString("caption");

        if (caption == null || caption.isEmpty()) {
            caption = documentSnapshot.getString("description");
        }

        Timestamp createdAt = documentSnapshot.getTimestamp("createdAt");

        if (createdAt != null) {
            Date date = createdAt.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvDiaryDate.setText("Ngày: " + sdf.format(date));
        } else {
            tvDiaryDate.setText("Ngày: Chưa rõ");
        }

        if (caption != null && !caption.isEmpty()) {
            tvDiaryCaption.setText(caption);
        } else {
            tvDiaryCaption.setText("Chưa có mô tả.");
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.bigcatontop)
                    .error(R.drawable.bigcatontop)
                    .into(imgDiaryDetail);
        } else {
            imgDiaryDetail.setImageResource(R.drawable.bigcatontop);
        }
    }
}