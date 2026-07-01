package com.example.sencare.activities.booking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.example.sencare.R;
import com.example.sencare.models.Spa;
import com.example.sencare.utils.DijkstraUtil;
import com.example.sencare.utils.FirebaseUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private double initialRadius;
    private LatLng userLatLng;

    private ImageButton btnBack;
    private TextView tvRadiusInfo, tvSpaCount;
    private CardView cvSpaInfo;
    private TextView tvSpaName, tvSpaAddress, tvDistance;
    private Button btnViewDetail, btnBookNow;

    private List<Spa> allSpas = new ArrayList<>();
    private Map<Marker, DijkstraUtil.Node> markerMap = new HashMap<>();
    private DijkstraUtil.Node selectedNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spa_map);

        initialRadius = getIntent().getDoubleExtra("RADIUS", 5.0);

        initViews();
        setupListeners();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvRadiusInfo = findViewById(R.id.tvRadiusInfo);
        tvSpaCount = findViewById(R.id.tvSpaCount);
        cvSpaInfo = findViewById(R.id.cvSpaInfo);
        tvSpaName = findViewById(R.id.tvSpaName);
        tvSpaAddress = findViewById(R.id.tvSpaAddress);
        tvDistance = findViewById(R.id.tvDistance);
        btnViewDetail = findViewById(R.id.btnViewDetail);
        btnBookNow = findViewById(R.id.btnBookNow);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnViewDetail.setOnClickListener(v -> {
            if (selectedNode != null) {
                Intent intent = new Intent(this, SpaDetailActivity.class);
                intent.putExtra("SPA_ID", selectedNode.spa.getSpaId());
                intent.putExtra("DISTANCE", selectedNode.distance);
                startActivity(intent);
            }
        });

        btnBookNow.setOnClickListener(v -> {
            if (selectedNode != null) {
                Intent intent = new Intent(this, BookingFormActivity.class);
                intent.putExtra("SPA_ID", selectedNode.spa.getSpaId());
                intent.putExtra("SPA_NAME", selectedNode.spa.getSpaName());
                intent.putExtra("SPA_IMAGE", selectedNode.spa.getImageUrl());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Quyền vị trí bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14));
                fetchSpas();
            }
        });
    }

    private void fetchSpas() {
        FirebaseFirestore db = FirebaseUtil.getFirestore();
        db.collection("spas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allSpas.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Spa spa = document.toObject(Spa.class);
                    spa.setSpaId(document.getId());
                    allSpas.add(spa);
                }
                processSpas();
            }
        });
    }

    private void processSpas() {
        if (userLatLng == null || allSpas.isEmpty()) return;

        List<DijkstraUtil.Node> filteredSpas = DijkstraUtil.findSpasWithinDistance(
                userLatLng.latitude, userLatLng.longitude, allSpas, initialRadius
        );

        double finalRadius = initialRadius;
        if (!filteredSpas.isEmpty()) {
            // Find max distance in results to show final radius used
            double maxDist = 0;
            for (DijkstraUtil.Node node : filteredSpas) {
                if (node.distance > maxDist) maxDist = node.distance;
            }
            finalRadius = Math.max(initialRadius, Math.ceil(maxDist));
        }

        tvRadiusInfo.setText(String.format("Bán kính %.0f km", finalRadius));
        tvSpaCount.setText(String.format("%d spa", filteredSpas.size()));

        mMap.clear();
        markerMap.clear();

        for (DijkstraUtil.Node node : filteredSpas) {
            LatLng spaLatLng = new LatLng(node.spa.getLatitude(), node.spa.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(spaLatLng)
                    .title(node.spa.getSpaName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            markerMap.put(marker, node);
        }

        mMap.setOnMarkerClickListener(marker -> {
            selectedNode = markerMap.get(marker);
            if (selectedNode != null) {
                showSpaInfo(selectedNode);
            }
            return false;
        });

        mMap.setOnMapClickListener(latLng -> cvSpaInfo.setVisibility(View.GONE));
    }

    private void showSpaInfo(DijkstraUtil.Node node) {
        tvSpaName.setText(node.spa.getSpaName());
        tvSpaAddress.setText(node.spa.getAddress());
        tvDistance.setText(String.format("%.1f km", node.distance));
        cvSpaInfo.setVisibility(View.VISIBLE);
    }
}
