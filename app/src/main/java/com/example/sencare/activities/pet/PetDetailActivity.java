package com.example.sencare.activities.pet;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.sencare.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class PetDetailActivity extends AppCompatActivity {


    private ImageView btnBack, imgDetailAvatar;
    private TextView tvDetailName, tvDetailSpecies, tvDetailAge, tvDetailPersonality;
    private MaterialButton btnEdit, btnDelete;


    private FirebaseFirestore db;
    private String petId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);


        db = FirebaseFirestore.getInstance();


        btnBack = findViewById(R.id.btnBack);
        imgDetailAvatar = findViewById(R.id.imgDetailAvatar);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailSpecies = findViewById(R.id.tvDetailSpecies);
        tvDetailAge = findViewById(R.id.tvDetailAge);
        tvDetailPersonality = findViewById(R.id.tvDetailPersonality);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);


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


        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (petId != null) {
            loadPetDetail(petId);
        }
    }


    private void loadPetDetail(String petId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        db.collection("pets").document(petId).get()
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
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không tải được dữ liệu thú cưng!", Toast.LENGTH_SHORT).show();
                });
    }


    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa thú cưng")
                .setMessage("Bạn có chắc chắn muốn xóa thú cưng? Nếu CÓ thì sẽ mất dữ liệu!")
                .setPositiveButton("CÓ", (dialog, which) -> deletePet())
                .setNegativeButton("KHÔNG", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void deletePet() {
        if (petId == null) {
            return;
        }


        db.collection("pets")
                .document(petId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
