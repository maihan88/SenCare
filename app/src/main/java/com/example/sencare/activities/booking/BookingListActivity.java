package com.example.sencare.activities.booking;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.sencare.R;
import com.example.sencare.adapters.BookingAdapter;
import com.example.sencare.databinding.ActivityBookingListBinding;
import com.example.sencare.models.Booking;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingListActivity extends AppCompatActivity {

    private ActivityBookingListBinding binding;
    private List<Booking> upcomingList = new ArrayList<>();
    private List<Booking> pastList = new ArrayList<>();
    private List<Booking> rejectedList = new ArrayList<>();
    private BookingAdapter upcomingAdapter, pastAdapter, rejectedAdapter;
    private FirestoreHelper dbHelper;
    private ListenerRegistration bookingListener;

    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_list);

        dbHelper = new FirestoreHelper();

        upcomingAdapter = new BookingAdapter(upcomingList, false, booking -> showCancelDialog(booking));
        binding.rvUpcoming.setAdapter(upcomingAdapter);

        pastAdapter = new BookingAdapter(pastList, true, booking -> {});
        binding.rvPast.setAdapter(pastAdapter);

        rejectedAdapter = new BookingAdapter(rejectedList, false, booking -> {});
        binding.rvRejected.setAdapter(rejectedAdapter);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.tabUpcoming.setOnClickListener(v -> setTab(0));
        binding.tabPast.setOnClickListener(v -> setTab(1));
        binding.tabRejected.setOnClickListener(v -> setTab(2));

        setTab(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenBookings();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bookingListener != null) {
            bookingListener.remove();
            bookingListener = null;
        }
    }

    private void listenBookings() {
        String userId = FirebaseUtil.getCurrentUserId();
        if (userId == null) return;

        bookingListener = dbHelper.getBookingsByUser(userId)
                .addSnapshotListener((@Nullable QuerySnapshot value,
                                      @Nullable FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi khi tải lịch hẹn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) {
                        return;
                    }

                    upcomingList.clear();
                    pastList.clear();
                    rejectedList.clear();
                    Date now = new Date();

                    for (QueryDocumentSnapshot doc : value) {
                        Booking booking = doc.toObject(Booking.class);
                        booking.setBookingId(doc.getId());

                        String status = booking.getStatus();

                        if ("cancelled".equals(status)) {
                            continue;
                        }

                        if ("rejected".equals(status)) {
                            rejectedList.add(booking);
                        } else if (booking.getBookingTimestamp() != null &&
                                   booking.getBookingTimestamp().toDate().before(now)) {
                            pastList.add(booking);
                        } else {
                            upcomingList.add(booking);
                        }
                    }

                    sortByTime(upcomingList, true);
                    sortByTime(pastList, false);
                    sortByTime(rejectedList, false);

                    upcomingAdapter.notifyDataSetChanged();
                    pastAdapter.notifyDataSetChanged();
                    rejectedAdapter.notifyDataSetChanged();

                    refreshVisibility();
                });
    }

    private void sortByTime(List<Booking> list, boolean ascending) {
        list.sort((b1, b2) -> {
            if (b1.getBookingTimestamp() == null || b2.getBookingTimestamp() == null) return 0;
            int cmp = b1.getBookingTimestamp().compareTo(b2.getBookingTimestamp());
            return ascending ? cmp : -cmp;
        });
    }

    private void setTab(int tab) {
        currentTab = tab;
        styleTab(binding.tabUpcoming, tab == 0);
        styleTab(binding.tabPast, tab == 1);
        styleTab(binding.tabRejected, tab == 2);
        refreshVisibility();
    }

    private void styleTab(TextView tab, boolean active) {
        if (active) {
            tab.setBackgroundResource(R.drawable.bg_rounded_mint);
            tab.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tab.setTypeface(null, Typeface.BOLD);
        } else {
            tab.setBackgroundResource(0);
            tab.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
            tab.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void refreshVisibility() {
        binding.rvUpcoming.setVisibility(currentTab == 0 ? View.VISIBLE : View.GONE);
        binding.rvPast.setVisibility(currentTab == 1 ? View.VISIBLE : View.GONE);
        binding.rvRejected.setVisibility(currentTab == 2 ? View.VISIBLE : View.GONE);

        List<Booking> activeList;
        String emptyText;
        if (currentTab == 0) {
            activeList = upcomingList;
            emptyText = "Chưa có lịch sắp tới.";
        } else if (currentTab == 1) {
            activeList = pastList;
            emptyText = "Chưa có lịch đã qua.";
        } else {
            activeList = rejectedList;
            emptyText = "Không có lịch bị từ chối.";
        }

        if (activeList.isEmpty()) {
            binding.tvEmpty.setText(emptyText);
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
        }
    }

    private void showCancelDialog(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Hủy lịch")
                .setMessage("Bạn có chắc chắn muốn hủy lịch hẹn tại " + booking.getSpaName() + "?")
                .setPositiveButton("Hủy lịch", (dialog, which) -> cancelBooking(booking))
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelBooking(Booking booking) {
        dbHelper.updateBookingStatus(booking.getBookingId(), "cancelled", Timestamp.now())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã hủy lịch hẹn", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Hủy lịch thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
