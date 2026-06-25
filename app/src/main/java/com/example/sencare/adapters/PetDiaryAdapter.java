package com.example.sencare.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.activities.diary.DiaryTimelineActivity;
import com.example.sencare.models.Pet;

import java.util.List;

public class PetDiaryAdapter extends RecyclerView.Adapter<PetDiaryAdapter.PetDiaryViewHolder> {

    private List<Pet> petList;

    public PetDiaryAdapter(List<Pet> petList) {
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetDiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdiarycard, parent, false);
        return new PetDiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetDiaryViewHolder holder, int position) {
        Pet currentPet = petList.get(position);

        holder.tvPetName.setText(currentPet.getName() != null ? currentPet.getName() : "Chưa có tên");

        if (currentPet.getImageUrl() != null && !currentPet.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentPet.getImageUrl())
                    .placeholder(R.drawable.bigcatontop)
                    .into(holder.imgPetAvatar);
        } else {
            holder.imgPetAvatar.setImageResource(R.drawable.bigcatontop);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DiaryTimelineActivity.class);
            intent.putExtra("petId", currentPet.getPetId());
            intent.putExtra("petName", currentPet.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return petList == null ? 0 : petList.size();
    }

    public static class PetDiaryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPetAvatar;
        TextView tvPetName;

        public PetDiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPetAvatar = itemView.findViewById(R.id.imgPetAvatar);
            tvPetName = itemView.findViewById(R.id.tvPetName);
        }
    }
}