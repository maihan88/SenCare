package com.example.sencare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_select, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        String service = serviceList.get(position);
        holder.tvServiceName.setText(service);
        holder.cbService.setChecked(selectedPosition == position);

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
        CheckBox cbService;
        TextView tvServiceName;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            cbService = itemView.findViewById(R.id.cbService);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
        }
    }
}
