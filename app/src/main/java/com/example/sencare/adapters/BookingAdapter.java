package com.example.sencare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;
import com.example.sencare.databinding.ItemBookingBinding;
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
        ItemBookingBinding binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        // Các dòng text (spa, thú cưng - dịch vụ, ngày, giờ) được bind trong XML
        holder.binding.setBooking(booking);

        if (isPast) {
            holder.binding.tvStatus.setText("Đã qua");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_rounded_white);
            holder.binding.btnCancel.setVisibility(View.GONE);
        } else {
            holder.binding.tvStatus.setText("Sắp tới");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_rounded_yellow);
            holder.binding.btnCancel.setVisibility(View.VISIBLE);
        }

        holder.binding.btnCancel.setOnClickListener(v -> cancelListener.onCancel(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        ItemBookingBinding binding;

        public BookingViewHolder(@NonNull ItemBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
