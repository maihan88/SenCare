package com.example.sencare.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.activities.diary.DiaryDetailActivity;
import com.example.sencare.models.Diary;

import java.util.List;

public class TimelineImageAdapter extends RecyclerView.Adapter<TimelineImageAdapter.ImageViewHolder> {

    private List<Diary> diaryList;

    public TimelineImageAdapter(List<Diary> diaryList) {
        this.diaryList = diaryList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdiaryimage, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Diary currentDiary = diaryList.get(position);

        if (currentDiary.getImageUrl() != null && !currentDiary.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentDiary.getImageUrl())
                    .placeholder(R.drawable.bigcatontop)
                    .into(holder.imgDiaryItem);
        }

        holder.itemView.setOnClickListener(v -> {
             Intent intent = new Intent(v.getContext(), DiaryDetailActivity.class);
             intent.putExtra("diaryId", currentDiary.getDiaryId());
             v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return diaryList == null ? 0 : diaryList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDiaryItem;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDiaryItem = itemView.findViewById(R.id.imgDiaryItem);
        }
    }
}