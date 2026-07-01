package com.example.sencare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;
import com.example.sencare.models.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private boolean isPast;
    private OnBookingCancelListener cancelListener;

    public interface OnBookingCancelListener {
        void onCancel(Booking booking);
    }

    public BookingAdapter(List<Booking> bookingList, boolean isPast, OnBookingCancelListener cancelListener) {
        this.bookingList = bookingList;
        this.isPast = isPast;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvSpaName.setText(booking.getSpaName());
        holder.tvPetService.setText(String.format("🐾 %s - %s", booking.getPetName(), booking.getServiceName()));
        holder.tvDateTime.setText(String.format("📅 %s - 🕒 %s", booking.getBookingDate(), booking.getBookingTime()));

        if (isPast) {
            holder.tvStatus.setText("Đã qua");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_rounded_white);
            holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setText("Sắp tới");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_rounded_yellow);
            holder.btnCancel.setVisibility(View.VISIBLE);
        }

        holder.btnCancel.setOnClickListener(v -> cancelListener.onCancel(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvSpaName, tvStatus, tvPetService, tvDateTime;
        Button btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSpaName = itemView.findViewById(R.id.tvSpaName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPetService = itemView.findViewById(R.id.tvPetService);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
