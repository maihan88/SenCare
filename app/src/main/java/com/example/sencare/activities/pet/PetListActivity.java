package com.example.sencare.activities.pet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;
import com.example.sencare.adapters.PetAdapter;
import com.example.sencare.models.Pet;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PetListActivity extends AppCompatActivity {
    private RecyclerView rvPetList;
    private PetAdapter petAdapter;
    private List<Pet> petList;
    private ImageView btnBack;
    private MaterialButton btnAddPet;

    private FirestoreHelper dbHelper;
    private ListenerRegistration petListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list);

        rvPetList = findViewById(R.id.rvPetList);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        btnAddPet = findViewById(R.id.btnAddPet);
        btnAddPet.setOnClickListener(v -> {
            Intent intent = new Intent(PetListActivity.this, PetFormActivity.class);
            startActivity(intent);
        });
        petList = new ArrayList<>();

        petAdapter = new PetAdapter(petList);
        rvPetList.setLayoutManager(new LinearLayoutManager(this));
        rvPetList.setAdapter(petAdapter);

        dbHelper = new FirestoreHelper();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPetsFromFirestore();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (petListener != null) {
            petListener.remove();
            petListener = null;
        }
    }

    private void loadPetsFromFirestore() {
        FirebaseUser currentUser = FirebaseUtil.getAuth().getCurrentUser();

        if (currentUser == null) {
            Log.e("PetListActivity", "Chưa đăng nhập, không thể load pet");
            return;
        }

        String uid = currentUser.getUid();

        petListener = dbHelper.getPetsByOwner(uid)
                .addSnapshotListener((@Nullable QuerySnapshot value,
                                      @Nullable FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Lỗi load danh sách pet: ", error);
                        return;
                    }

                    if (value == null) {
                        return;
                    }

                    petList.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Pet pet = doc.toObject(Pet.class);
                        pet.setPetId(doc.getId());
                        petList.add(pet);
                    }

                    // Sắp xếp mới nhất trước (thay cho orderBy để không cần composite index)
                    Collections.sort(petList, (p1, p2) -> {
                        if (p1.getCreatedAt() == null) return 1;
                        if (p2.getCreatedAt() == null) return -1;
                        return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                    });

                    petAdapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
