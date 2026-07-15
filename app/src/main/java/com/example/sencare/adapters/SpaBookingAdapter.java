package com.example.sencare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;
import com.example.sencare.databinding.ItemSpaBookingBinding;
import com.example.sencare.models.Booking;

import java.util.List;

public class SpaBookingAdapter extends RecyclerView.Adapter<SpaBookingAdapter.SpaBookingViewHolder> {

    private List<Booking> bookingList;
    private OnBookingActionListener actionListener;

    public interface OnBookingActionListener {
        void onConfirm(Booking booking);
        void onReject(Booking booking);
    }

    public SpaBookingAdapter(List<Booking> bookingList, OnBookingActionListener actionListener) {
        this.bookingList = bookingList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public SpaBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSpaBookingBinding binding = ItemSpaBookingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SpaBookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SpaBookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.binding.setBooking(booking);

        if (booking.getUserName() != null && !booking.getUserName().isEmpty()) {
            holder.binding.tvUserName.setText(booking.getUserName());
        } else {
            holder.binding.tvUserName.setText("Khách hàng");
        }

        String status = booking.getStatus();
        boolean isPending = "pending".equals(status);
        boolean isRejected = "rejected".equals(status);

        if (isPending) {
            holder.binding.tvStatus.setText("Chờ xác nhận");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_rounded_yellow);
        } else if (isRejected) {
            holder.binding.tvStatus.setText("Đã từ chối");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_rounded_coral);
        } else {
            holder.binding.tvStatus.setText("Đã xác nhận");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_rounded_mint);
        }

        if (isPending) {
            holder.binding.layoutActions.setVisibility(View.VISIBLE);
        } else {
            holder.binding.layoutActions.setVisibility(View.GONE);
        }

        holder.binding.btnConfirm.setOnClickListener(v -> actionListener.onConfirm(booking));
        holder.binding.btnReject.setOnClickListener(v -> actionListener.onReject(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class SpaBookingViewHolder extends RecyclerView.ViewHolder {
        ItemSpaBookingBinding binding;

        public SpaBookingViewHolder(@NonNull ItemSpaBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
