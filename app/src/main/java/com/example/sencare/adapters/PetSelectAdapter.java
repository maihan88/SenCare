package com.example.sencare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.databinding.ItemPetSelectBinding;
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
        ItemPetSelectBinding binding = ItemPetSelectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PetViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.binding.setPet(pet);

        if (pet.getImageUrl() != null && !pet.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(pet.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.imgPetAvatar);
            holder.binding.imgPetAvatar.setVisibility(View.VISIBLE);
            holder.binding.tvPetInitial.setVisibility(View.GONE);
        } else {
            if (pet.getName() != null && !pet.getName().isEmpty()) {
                holder.binding.tvPetInitial.setText(pet.getName().substring(0, 1).toUpperCase());
            }
            holder.binding.imgPetAvatar.setVisibility(View.GONE);
            holder.binding.tvPetInitial.setVisibility(View.VISIBLE);
        }

        if (selectedPosition == position) {
            holder.binding.llPetContainer.setBackgroundResource(R.drawable.bg_rounded_yellow);
        } else {
            holder.binding.llPetContainer.setBackground(null);
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
        ItemPetSelectBinding binding;

        public PetViewHolder(@NonNull ItemPetSelectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
