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
import com.example.sencare.activities.pet.PetDetailActivity;
import com.example.sencare.models.Pet;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> petList;

    public PetAdapter(List<Pet> petList) {
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet currentPet = petList.get(position);

        holder.tvPetName.setText(currentPet.getName());
        holder.tvPetAge.setText("Tuổi: " + currentPet.getAge());
        holder.tvPetSpecies.setText("Giống loài: " + currentPet.getSpecies());
        holder.tvPetPersonality.setText("Tính cách: " + currentPet.getPersonality());

        String imageUrl = currentPet.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.bigcatontop)
                    .error(R.drawable.bigcatontop)
                    .into(holder.imgPetAvatar);
        } else {
            holder.imgPetAvatar.setImageResource(R.drawable.bigcatontop);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PetDetailActivity.class);
            intent.putExtra("petId",currentPet.getPetId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return petList == null ? 0 : petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPetAvatar;
        TextView tvPetName, tvPetAge, tvPetSpecies, tvPetPersonality;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPetAvatar = itemView.findViewById(R.id.imgPetAvatar);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvPetAge = itemView.findViewById(R.id.tvPetAge);
            tvPetSpecies = itemView.findViewById(R.id.tvPetSpecies);
            tvPetPersonality = itemView.findViewById(R.id.tvPetPersonality);
        }
    }
}