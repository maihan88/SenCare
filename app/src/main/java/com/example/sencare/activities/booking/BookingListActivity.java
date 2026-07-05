package com.example.sencare.activities.booking;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;
import com.example.sencare.adapters.BookingAdapter;
import com.example.sencare.models.Booking;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingListActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView rvUpcoming, rvPast;
    private List<Booking> upcomingList = new ArrayList<>();
    private List<Booking> pastList = new ArrayList<>();
    private BookingAdapter upcomingAdapter, pastAdapter;
    private FirestoreHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        dbHelper = new FirestoreHelper();

        btnBack = findViewById(R.id.btnBack);
        rvUpcoming = findViewById(R.id.rvUpcoming);
        rvPast = findViewById(R.id.rvPast);

        upcomingAdapter = new BookingAdapter(upcomingList, false, booking -> showCancelDialog(booking));
        rvUpcoming.setAdapter(upcomingAdapter);

        pastAdapter = new BookingAdapter(pastList, true, booking -> {});
        rvPast.setAdapter(pastAdapter);

        btnBack.setOnClickListener(v -> finish());

        fetchBookings();
    }

    // Tải danh sách lịch hẹn của người dùng, chia thành sắp tới / đã qua (gọi lại sau khi hủy lịch)
    private void fetchBookings() {
        String userId = FirebaseUtil.getCurrentUserId();
        if (userId == null) return;

        // Fetch all bookings for the user and filter in memory to avoid index requirements
        dbHelper.getBookingsByUser(userId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    upcomingList.clear();
                    pastList.clear();
                    Date now = new Date();

                    List<Booking> allUserBookings = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Booking booking = doc.toObject(Booking.class);
                        booking.setBookingId(doc.getId());

                        // Filter by status "active"
                        if ("active".equals(booking.getStatus())) {
                            allUserBookings.add(booking);
                        }
                    }

                    // Sort by timestamp
                    allUserBookings.sort((b1, b2) -> {
                        if (b1.getBookingTimestamp() == null || b2.getBookingTimestamp() == null) return 0;
                        return b1.getBookingTimestamp().compareTo(b2.getBookingTimestamp());
                    });

                    for (Booking booking : allUserBookings) {
                        if (booking.getBookingTimestamp() != null &&
                            booking.getBookingTimestamp().toDate().before(now)) {
                            pastList.add(booking);
                        } else {
                            upcomingList.add(booking);
                        }
                    }
                    upcomingAdapter.notifyDataSetChanged();
                    pastAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải lịch hẹn: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Hộp thoại xác nhận hủy lịch (được adapter gọi khi bấm nút hủy trên từng lịch hẹn)
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
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã hủy lịch hẹn", Toast.LENGTH_SHORT).show();
                    fetchBookings(); // Refresh list
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Hủy lịch thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
