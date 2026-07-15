package com.example.sencare.activities.pet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sencare.R;
import com.example.sencare.adapters.PetAdapter;
import com.example.sencare.models.Pet;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemorialActivity extends AppCompatActivity {

    private RecyclerView rvMemorialList;
    private PetAdapter petAdapter;
    private List<Pet> memorialList;
    private TextView tvEmptyMemorial;
    private ImageView btnBack;

    private FirestoreHelper dbHelper;
    private ListenerRegistration petListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorial);

        rvMemorialList = findViewById(R.id.rvMemorialList);
        tvEmptyMemorial = findViewById(R.id.tvEmptyMemorial);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        memorialList = new ArrayList<>();

        petAdapter = new PetAdapter(memorialList);
        rvMemorialList.setAdapter(petAdapter);

        dbHelper = new FirestoreHelper();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMemorialPets();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (petListener != null) {
            petListener.remove();
            petListener = null;
        }
    }

    private void loadMemorialPets() {
        FirebaseUser currentUser = FirebaseUtil.getAuth().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        petListener = dbHelper.getPetsByOwner(uid)
                .addSnapshotListener((@Nullable QuerySnapshot value,
                                      @Nullable FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Lỗi load góc tưởng niệm: ", error);
                        return;
                    }

                    if (value == null) {
                        return;
                    }

                    memorialList.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Pet pet = doc.toObject(Pet.class);
                        pet.setPetId(doc.getId());

                        if ("memorial".equals(pet.getStatus())) {
                            memorialList.add(pet);
                        }
                    }

                    Collections.sort(memorialList, (p1, p2) -> {
                        if (p1.getPassedAwayAt() == null) return 1;
                        if (p2.getPassedAwayAt() == null) return -1;
                        return p2.getPassedAwayAt().compareTo(p1.getPassedAwayAt());
                    });

                    if (memorialList.isEmpty()) {
                        tvEmptyMemorial.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyMemorial.setVisibility(View.GONE);
                    }

                    petAdapter.notifyDataSetChanged();
                });
    }
}
