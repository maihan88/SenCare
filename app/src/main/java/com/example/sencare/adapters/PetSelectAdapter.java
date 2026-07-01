package com.example.sencare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.models.Pet;

import java.util.List;

public class PetSelectAdapter extends RecyclerView.Adapter<PetSelectAdapter.PetViewHolder> {

    private List<Pet> petList;
    private int selectedPosition = -1;
    private OnPetClickListener listener;

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    public PetSelectAdapter(List<Pet> petList, OnPetClickListener listener) {
        this.petList = petList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet_select, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.tvPetName.setText(pet.getName());
        holder.tvPetSpecies.setText(pet.getSpecies());
        
        if (pet.getImageUrl() != null && !pet.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(pet.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imgPetAvatar);
            holder.imgPetAvatar.setVisibility(View.VISIBLE);
            holder.tvPetInitial.setVisibility(View.GONE);
        } else {
            if (pet.getName() != null && !pet.getName().isEmpty()) {
                holder.tvPetInitial.setText(pet.getName().substring(0, 1).toUpperCase());
            }
            holder.imgPetAvatar.setVisibility(View.GONE);
            holder.tvPetInitial.setVisibility(View.VISIBLE);
        }

        if (selectedPosition == position) {
            holder.llPetContainer.setBackgroundResource(R.drawable.bg_rounded_yellow);
        } else {
            holder.llPetContainer.setBackground(null);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onPetClick(pet);
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public Pet getSelectedPet() {
        if (selectedPosition != -1) {
            return petList.get(selectedPosition);
        }
        return null;
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPetAvatar;
        TextView tvPetInitial, tvPetName, tvPetSpecies;
        LinearLayout llPetContainer;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPetAvatar = itemView.findViewById(R.id.imgPetAvatar);
            tvPetInitial = itemView.findViewById(R.id.tvPetInitial);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvPetSpecies = itemView.findViewById(R.id.tvPetSpecies);
            llPetContainer = itemView.findViewById(R.id.llPetContainer);
        }
    }
}

