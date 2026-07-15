package com.example.sencare.activities.diary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;
import com.example.sencare.adapters.TimelineDateAdapter;
import com.example.sencare.models.Diary;
import com.example.sencare.utils.FirestoreHelper;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DiaryTimelineActivity extends AppCompatActivity {

    private ImageView btnBack, btnAddDiary;
    private TextView tvDiaryTitle;
    private RecyclerView rvDiaryList;

    private FirestoreHelper dbHelper;
    private ListenerRegistration diaryListener;
    private String petId, petName, petStatus;
    private TimelineDateAdapter adapter;
    private List<TimelineGroup> timelineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_timeline);

        btnBack = findViewById(R.id.btnBack);
        btnAddDiary = findViewById(R.id.btnAddDiary);
        tvDiaryTitle = findViewById(R.id.tvDiaryTitle);
        rvDiaryList = findViewById(R.id.rvDiaryList);

        dbHelper = new FirestoreHelper();

        petId = getIntent().getStringExtra("petId");
        petName = getIntent().getStringExtra("petName");
        petStatus = getIntent().getStringExtra("petStatus");

        if (petId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thú cưng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (petName != null) {
            tvDiaryTitle.setText("Nhật ký của " + petName);
        }

        btnBack.setOnClickListener(v -> finish());

        if ("memorial".equals(petStatus)) {
            btnAddDiary.setVisibility(View.GONE);
        }

        btnAddDiary.setOnClickListener(v -> {
             Intent intent = new Intent(this, AddDiaryActivity.class);
             intent.putExtra("petId", petId);
             startActivity(intent);
        });

        timelineList = new ArrayList<>();
        rvDiaryList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TimelineDateAdapter(timelineList);
        rvDiaryList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadDiariesAndGroupByDate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (diaryListener != null) {
            diaryListener.remove();
            diaryListener = null;
        }
    }

    private void loadDiariesAndGroupByDate() {
        if (diaryListener != null) {
            diaryListener.remove();
        }
        diaryListener = dbHelper.getDiariesByPet(petId)
                .addSnapshotListener((@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e("DiaryTimeline", "Lỗi kéo data: ", error);
                        return;
                    }

                    if (value != null) {
                        List<Diary> diaries = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Diary diary = doc.toObject(Diary.class);
                            diary.setDiaryId(doc.getId());
                            diaries.add(diary);
                        }

                        Collections.sort(diaries, (d1, d2) -> {
                            if (d1.getCreatedAt() == null) return 1;
                            if (d2.getCreatedAt() == null) return -1;
                            return d2.getCreatedAt().compareTo(d1.getCreatedAt());
                        });

                        Map<String, List<Diary>> groupedData = new LinkedHashMap<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                        for (Diary diary : diaries) {
                            String dateStr = "Chưa rõ ngày";
                            if (diary.getCreatedAt() != null) {
                                Date date = diary.getCreatedAt().toDate();
                                dateStr = sdf.format(date);
                            }

                            if (!groupedData.containsKey(dateStr)) {
                                groupedData.put(dateStr, new ArrayList<>());
                            }
                            groupedData.get(dateStr).add(diary);
                        }

                        timelineList.clear();
                        for (Map.Entry<String, List<Diary>> entry : groupedData.entrySet()) {
                            timelineList.add(new TimelineGroup(entry.getKey(), entry.getValue()));
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static class TimelineGroup {
        private String date;
        private List<Diary> diaries;

        public TimelineGroup(String date, List<Diary> diaries) {
            this.date = date;
            this.diaries = diaries;
        }

        public String getDate() { return date; }
        public List<Diary> getDiaries() { return diaries; }
    }
}
