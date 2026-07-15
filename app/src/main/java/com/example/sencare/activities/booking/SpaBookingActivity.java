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
import com.example.sencare.adapters.SpaBookingAdapter;
import com.example.sencare.databinding.ActivitySpaBookingBinding;
import com.example.sencare.models.Booking;
import com.example.sencare.utils.FirestoreHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SpaBookingActivity extends AppCompatActivity {

    private ActivitySpaBookingBinding binding;
    private List<Booking> pendingList = new ArrayList<>();
    private List<Booking> confirmedList = new ArrayList<>();
    private List<Booking> rejectedList = new ArrayList<>();
    private SpaBookingAdapter pendingAdapter, confirmedAdapter, rejectedAdapter;
    private FirestoreHelper dbHelper;
    private ListenerRegistration bookingListener;
    private String spaId;

    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spa_booking);

        dbHelper = new FirestoreHelper();
        spaId = getIntent().getStringExtra("SPA_ID");

        pendingAdapter = new SpaBookingAdapter(pendingList, new SpaBookingAdapter.OnBookingActionListener() {
            @Override
            public void onConfirm(Booking booking) {
                showActionDialog(booking, true);
            }

            @Override
            public void onReject(Booking booking) {
                showActionDialog(booking, false);
            }
        });
        binding.rvPending.setAdapter(pendingAdapter);

        confirmedAdapter = new SpaBookingAdapter(confirmedList, new SpaBookingAdapter.OnBookingActionListener() {
            @Override
            public void onConfirm(Booking booking) {
            }

            @Override
            public void onReject(Booking booking) {
            }
        });
        binding.rvConfirmed.setAdapter(confirmedAdapter);

        rejectedAdapter = new SpaBookingAdapter(rejectedList, new SpaBookingAdapter.OnBookingActionListener() {
            @Override
            public void onConfirm(Booking booking) {
            }

            @Override
            public void onReject(Booking booking) {
            }
        });
        binding.rvRejected.setAdapter(rejectedAdapter);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.tabPending.setOnClickListener(v -> setTab(0));
        binding.tabConfirmed.setOnClickListener(v -> setTab(1));
        binding.tabRejected.setOnClickListener(v -> setTab(2));

        setTab(0);

        if (spaId == null) {
            Toast.makeText(this, "Không tìm thấy ID Spa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenSpaBookings();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bookingListener != null) {
            bookingListener.remove();
            bookingListener = null;
        }
    }

    private void listenSpaBookings() {
        if (spaId == null) return;

        bookingListener = dbHelper.getBookingsBySpa(spaId)
                .addSnapshotListener((@Nullable QuerySnapshot value,
                                      @Nullable FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi khi tải lịch đặt: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) {
                        return;
                    }

                    pendingList.clear();
                    confirmedList.clear();
                    rejectedList.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Booking booking = doc.toObject(Booking.class);
                        booking.setBookingId(doc.getId());

                        String status = booking.getStatus();

                        if ("pending".equals(status)) {
                            pendingList.add(booking);
                        } else if ("confirmed".equals(status) || "active".equals(status)) {
                            confirmedList.add(booking);
                        } else if ("rejected".equals(status)) {
                            rejectedList.add(booking);
                        }
                    }

                    sortByTime(pendingList, true);
                    sortByTime(confirmedList, true);
                    sortByTime(rejectedList, false);

                    pendingAdapter.notifyDataSetChanged();
                    confirmedAdapter.notifyDataSetChanged();
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
        styleTab(binding.tabPending, tab == 0);
        styleTab(binding.tabConfirmed, tab == 1);
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
        binding.rvPending.setVisibility(currentTab == 0 ? View.VISIBLE : View.GONE);
        binding.rvConfirmed.setVisibility(currentTab == 1 ? View.VISIBLE : View.GONE);
        binding.rvRejected.setVisibility(currentTab == 2 ? View.VISIBLE : View.GONE);

        List<Booking> activeList;
        String emptyText;
        if (currentTab == 0) {
            activeList = pendingList;
            emptyText = "Không có lịch chờ xác nhận.";
        } else if (currentTab == 1) {
            activeList = confirmedList;
            emptyText = "Chưa có lịch đã xác nhận.";
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

    private void showActionDialog(Booking booking, boolean isConfirm) {
        String title = isConfirm ? "Xác nhận lịch" : "Từ chối lịch";
        String message = isConfirm
                ? "Xác nhận nhận lịch của bé " + booking.getPetName() + " lúc " + booking.getBookingTime() + " ngày " + booking.getBookingDate() + "?"
                : "Từ chối lịch của bé " + booking.getPetName() + " lúc " + booking.getBookingTime() + " ngày " + booking.getBookingDate() + "?";
        String positiveText = isConfirm ? "Xác nhận" : "Từ chối";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, (dialog, which) -> updateStatus(booking, isConfirm))
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void updateStatus(Booking booking, boolean isConfirm) {
        String newStatus = isConfirm ? "confirmed" : "rejected";
        String successMessage = isConfirm ? "Đã xác nhận lịch hẹn" : "Đã từ chối lịch hẹn";

        dbHelper.updateBookingStatus(booking.getBookingId(), newStatus, Timestamp.now())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
