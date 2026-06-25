package com.example.sencare.activities.diary;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;
import com.example.sencare.adapters.PetDiaryAdapter;
import com.example.sencare.models.Pet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PetDiaryListActivity extends AppCompatActivity {

    private RecyclerView rvDiaryList;
    private PetDiaryAdapter adapter;
    private List<Pet> petList;
    private ImageView btnBack;

    private FirebaseFirestore db;
    private ListenerRegistration petListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_diary_list);

        rvDiaryList = findViewById(R.id.rvDiaryList);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        petList = new ArrayList<>();
        adapter = new PetDiaryAdapter(petList);
        rvDiaryList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadPetsForDiary();
    }

    private void loadPetsForDiary() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        petListener = db.collection("pets")
                .whereEqualTo("ownerId", uid)
                .addSnapshotListener((@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Lỗi kéo data: ", error);
                        return;
                    }

                    if (value != null) {
                        petList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Pet pet = doc.toObject(Pet.class);
                            pet.setPetId(doc.getId());
                            petList.add(pet);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (petListener != null) {
            petListener.remove();
        }
    }
}