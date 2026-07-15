package com.example.sencare.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.databinding.ItemServiceSelectBinding;

import java.util.List;

public class ServiceSelectAdapter extends RecyclerView.Adapter<ServiceSelectAdapter.ServiceViewHolder> {

    private List<String> serviceList;
    private int selectedPosition = -1;

    public ServiceSelectAdapter(List<String> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceSelectBinding binding = ItemServiceSelectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ServiceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        String service = serviceList.get(position);
        holder.binding.setService(service);
        holder.binding.cbService.setChecked(selectedPosition == position);

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public String getSelectedService() {
        if (selectedPosition != -1) {
            return serviceList.get(selectedPosition);
        }
        return null;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ItemServiceSelectBinding binding;

        public ServiceViewHolder(@NonNull ItemServiceSelectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
