package com.example.sencare.activities.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.adapters.PetSelectAdapter;
import com.example.sencare.adapters.ServiceSelectAdapter;
import com.example.sencare.models.Booking;
import com.example.sencare.models.Pet;
import com.example.sencare.models.Spa;
import com.example.sencare.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookingFormActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgSpa;
    private TextView tvSpaName, tvDate, tvTime;
    private RecyclerView rvPets, rvServices;
    private Button btnConfirm;

    private String spaId, spaName, spaImage;
    private PetSelectAdapter petAdapter;
    private ServiceSelectAdapter serviceAdapter;
    private List<Pet> petList = new ArrayList<>();
    private List<String> serviceList = new ArrayList<>();

    private Calendar bookingCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        spaId = getIntent().getStringExtra("SPA_ID");
        spaName = getIntent().getStringExtra("SPA_NAME");
        spaImage = getIntent().getStringExtra("SPA_IMAGE");

        initViews();
        setupListeners();
        fetchUserData();
        fetchSpaServices();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgSpa = findViewById(R.id.imgSpa);
        tvSpaName = findViewById(R.id.tvSpaName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        rvPets = findViewById(R.id.rvPets);
        rvServices = findViewById(R.id.rvServices);
        btnConfirm = findViewById(R.id.btnConfirm);

        tvSpaName.setText(spaName);
        if (spaImage != null && !spaImage.isEmpty()) {
            Glide.with(this).load(spaImage).placeholder(R.drawable.iconpetspa).into(imgSpa);
        }

        petAdapter = new PetSelectAdapter(petList, pet -> {});
        rvPets.setAdapter(petAdapter);

        serviceAdapter = new ServiceSelectAdapter(serviceList);
        rvServices.setAdapter(serviceAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        tvDate.setOnClickListener(v -> showDatePicker());
        tvTime.setOnClickListener(v -> showTimePicker());

        btnConfirm.setOnClickListener(v -> submitBooking());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            bookingCalendar.set(Calendar.YEAR, year);
            bookingCalendar.set(Calendar.MONTH, month);
            bookingCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            tvDate.setText(String.format("%02d/%02d/%d", month + 1, dayOfMonth, year));
        }, bookingCalendar.get(Calendar.YEAR), bookingCalendar.get(Calendar.MONTH), bookingCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            bookingCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            bookingCalendar.set(Calendar.MINUTE, minute);
            bookingCalendar.set(Calendar.SECOND, 0);
            
            String amPm = hourOfDay < 12 ? "AM" : "PM";
            int hour = hourOfDay % 12;
            if (hour == 0) hour = 12;
            tvTime.setText(String.format("%02d:%02d %s", hour, minute, amPm));
        }, bookingCalendar.get(Calendar.HOUR_OF_DAY), bookingCalendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void fetchUserData() {
        String userId = FirebaseUtil.getCurrentUserId();
        if (userId == null) return;

        FirebaseUtil.getFirestore().collection("pets")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    petList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Pet pet = doc.toObject(Pet.class);
                        pet.setPetId(doc.getId());
                        petList.add(pet);
                    }
                    petAdapter.notifyDataSetChanged();
                });
    }

    private void fetchSpaServices() {
        if (spaId == null) return;

        FirebaseUtil.getFirestore().collection("spas").document(spaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Spa spa = documentSnapshot.toObject(Spa.class);
                        if (spa != null && spa.getServices() != null) {
                            serviceList.clear();
                            serviceList.addAll(spa.getServices());
                            serviceAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void submitBooking() {
        Pet selectedPet = petAdapter.getSelectedPet();
        String selectedService = serviceAdapter.getSelectedService();
        String date = tvDate.getText().toString();
        String time = tvTime.getText().toString();

        if (selectedPet == null) {
            Toast.makeText(this, "Vui lòng chọn thú cưng", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedService == null) {
            Toast.makeText(this, "Vui lòng chọn dịch vụ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày và giờ", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseUtil.getCurrentUserId();
        if (userId == null) return;

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setPetId(selectedPet.getPetId());
        booking.setPetName(selectedPet.getName());
        booking.setSpaId(spaId);
        booking.setSpaName(spaName);
        booking.setServiceName(selectedService);
        booking.setBookingDate(date);
        booking.setBookingTime(time);
        booking.setBookingTimestamp(new Timestamp(bookingCalendar.getTime()));
        booking.setStatus("active");
        booking.setCreatedAt(Timestamp.now());
        booking.setUpdatedAt(Timestamp.now());

        FirebaseUtil.getFirestore().collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, BookingListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Đặt lịch thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
