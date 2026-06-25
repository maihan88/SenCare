package com.example.sencare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;
import com.example.sencare.activities.diary.DiaryTimelineActivity;

import java.util.List;

public class TimelineDateAdapter extends RecyclerView.Adapter<TimelineDateAdapter.DateViewHolder> {

    private List<DiaryTimelineActivity.TimelineGroup> timelineList;

    public TimelineDateAdapter(List<DiaryTimelineActivity.TimelineGroup> timelineList) {
        this.timelineList = timelineList;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dùng cái khuôn bự chứa Ngày Tháng và danh sách trượt ngang
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DiaryTimelineActivity.TimelineGroup currentGroup = timelineList.get(position);

        // Set chữ Ngày Tháng Năm
        holder.tvTimelineDate.setText(currentGroup.getDate());

        // Setup cái danh sách ảnh cuộn ngang bên trong
        TimelineImageAdapter imageAdapter = new TimelineImageAdapter(currentGroup.getDiaries());
        holder.rvTimelineImages.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.rvTimelineImages.setAdapter(imageAdapter);
    }

    @Override
    public int getItemCount() {
        return timelineList == null ? 0 : timelineList.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimelineDate;
        RecyclerView rvTimelineImages;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimelineDate = itemView.findViewById(R.id.tvTimelineDate);
            rvTimelineImages = itemView.findViewById(R.id.rvTimelineImages);
        }
    }
}