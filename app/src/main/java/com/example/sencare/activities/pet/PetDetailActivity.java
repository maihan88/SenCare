package com.example.sencare.activities.pet;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PetDetailActivity extends AppCompatActivity {

    private ImageView btnBack, imgDetailAvatar;
    private TextView tvDetailName, tvDetailSpecies, tvDetailAge, tvDetailPersonality;
    private TextView tvMemorialDate, tvMemorialMessage;
    private LinearLayout layoutMemorial;
    private MaterialButton btnEdit, btnDelete;
    private View btnFarewell;

    private FirestoreHelper dbHelper;
    private String petId;
    private String petName = "";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        dbHelper = new FirestoreHelper();

        btnBack = findViewById(R.id.btnBack);
        imgDetailAvatar = findViewById(R.id.imgDetailAvatar);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailSpecies = findViewById(R.id.tvDetailSpecies);
        tvDetailAge = findViewById(R.id.tvDetailAge);
        tvDetailPersonality = findViewById(R.id.tvDetailPersonality);
        layoutMemorial = findViewById(R.id.layoutMemorial);
        tvMemorialDate = findViewById(R.id.tvMemorialDate);
        tvMemorialMessage = findViewById(R.id.tvMemorialMessage);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnFarewell = findViewById(R.id.btnFarewell);

        petId = getIntent().getStringExtra("petId");

        if (petId == null) {
            Toast.makeText(this, "Không tìm thấy thú cưng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(PetDetailActivity.this, PetFormActivity.class);
            intent.putExtra("petId", petId);
            startActivity(intent);
        });

        btnFarewell.setOnClickListener(v -> showFarewellDialog());

        btnDelete.setOnClickListener(v -> showDeleteDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (petId != null) {
            loadPetDetail(petId);
        }
    }

    private void loadPetDetail(String petId) {
        FirebaseUser currentUser = FirebaseUtil.getAuth().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper.getPet(petId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(this, "Không tìm thấy thú cưng!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    String ownerId = documentSnapshot.getString("ownerId");

                    if (ownerId == null || !ownerId.equals(currentUser.getUid())) {
                        Toast.makeText(this, "Bạn không có quyền xem thú cưng này!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    String name = documentSnapshot.getString("name");
                    String species = documentSnapshot.getString("species");
                    String personality = documentSnapshot.getString("personality");
                    String imageUrl = documentSnapshot.getString("imageUrl");
                    Long age = documentSnapshot.getLong("age");

                    petName = name != null ? name : "Bé";

                    tvDetailName.setText(name != null ? name : "Chưa có tên");
                    tvDetailSpecies.setText("Giống loài: " + (species != null ? species : "Chưa cập nhật"));
                    tvDetailAge.setText("Tuổi: " + (age != null ? age : 0));
                    tvDetailPersonality.setText("Tính cách: " + (personality != null ? personality : "Chưa cập nhật"));

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.bigcatontop)
                                .error(R.drawable.bigcatontop)
                                .into(imgDetailAvatar);
                    } else {
                        imgDetailAvatar.setImageResource(R.drawable.bigcatontop);
                    }

                    String status = documentSnapshot.getString("status");
                    Timestamp passedAwayAt = documentSnapshot.getTimestamp("passedAwayAt");
                    String farewellMessage = documentSnapshot.getString("farewellMessage");

                    showMemorialOrNormal(status, passedAwayAt, farewellMessage);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không tải được dữ liệu thú cưng!", Toast.LENGTH_SHORT).show();
                });
    }

    private void showMemorialOrNormal(String status, Timestamp passedAwayAt, String farewellMessage) {
        boolean isMemorial = "memorial".equals(status);

        if (isMemorial) {
            layoutMemorial.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            btnFarewell.setVisibility(View.GONE);
            btnDelete.setText("Xóa vĩnh viễn");

            if (passedAwayAt != null) {
                tvMemorialDate.setText(petName + " đã ra đi ngày " + dateFormat.format(passedAwayAt.toDate()));
            } else {
                tvMemorialDate.setText(petName + " đã ra đi");
            }

            if (farewellMessage != null && !farewellMessage.isEmpty()) {
                tvMemorialMessage.setVisibility(View.VISIBLE);
                tvMemorialMessage.setText("\"" + farewellMessage + "\"");
            } else {
                tvMemorialMessage.setVisibility(View.GONE);
            }
        } else {
            layoutMemorial.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
            btnFarewell.setVisibility(View.VISIBLE);
            btnDelete.setText("Xóa");
        }
    }

    private void showFarewellDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_farewell, null);
        TextView tvFarewellTitle = dialogView.findViewById(R.id.tvFarewellTitle);
        TextView tvPassedDate = dialogView.findViewById(R.id.tvPassedDate);
        EditText edtFarewell = dialogView.findViewById(R.id.edtFarewell);

        tvFarewellTitle.setText("Tiễn biệt " + petName);

        Calendar passedCalendar = Calendar.getInstance();
        tvPassedDate.setText(dateFormat.format(passedCalendar.getTime()));

        tvPassedDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                passedCalendar.set(Calendar.YEAR, year);
                passedCalendar.set(Calendar.MONTH, month);
                passedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tvPassedDate.setText(dateFormat.format(passedCalendar.getTime()));
            }, passedCalendar.get(Calendar.YEAR), passedCalendar.get(Calendar.MONTH), passedCalendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Tiễn biệt", (dialog, which) -> {
                    String message = edtFarewell.getText().toString().trim();
                    saveMemorial(passedCalendar.getTime(), message);
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void saveMemorial(Date passedDate, String farewellMessage) {
        Map<String, Object> petData = new HashMap<>();
        petData.put("status", "memorial");
        petData.put("passedAwayAt", new Timestamp(passedDate));
        petData.put("farewellMessage", farewellMessage);
        petData.put("updatedAt", Timestamp.now());

        dbHelper.savePet(petId, petData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã đưa " + petName + " vào Góc tưởng niệm", Toast.LENGTH_LONG).show();
                    cancelUpcomingBookingsOfPet();
                    loadPetDetail(petId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void cancelUpcomingBookingsOfPet() {
        Date now = new Date();

        dbHelper.getBookingsByPet(petId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String status = doc.getString("status");
                        Timestamp bookingTimestamp = doc.getTimestamp("bookingTimestamp");

                        boolean isActiveBooking = "pending".equals(status)
                                || "confirmed".equals(status)
                                || "active".equals(status);
                        boolean isUpcoming = bookingTimestamp != null
                                && bookingTimestamp.toDate().after(now);

                        if (isActiveBooking && isUpcoming) {
                            dbHelper.updateBookingStatus(doc.getId(), "cancelled", Timestamp.now());
                        }
                    }
                });
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa thú cưng")
                .setMessage("Bạn có chắc chắn muốn xóa thú cưng? Nếu CÓ thì sẽ mất toàn bộ dữ liệu và kỷ niệm của bé!")
                .setPositiveButton("CÓ", (dialog, which) -> deletePet())
                .setNegativeButton("KHÔNG", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deletePet() {
        if (petId == null) {
            return;
        }

        dbHelper.deletePet(petId)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
