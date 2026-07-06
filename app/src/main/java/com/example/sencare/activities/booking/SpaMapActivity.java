package com.example.sencare.activities.booking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.example.sencare.R;
import com.example.sencare.databinding.ActivitySpaMapBinding;
import com.example.sencare.models.Spa;
import com.example.sencare.utils.DijkstraUtil;
import com.example.sencare.utils.FirestoreHelper;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FirestoreHelper dbHelper;
    private ListenerRegistration spaListener;
    private double initialRadius;
    private LatLng userLatLng;

    private ActivitySpaMapBinding binding;

    private List<Spa> allSpas = new ArrayList<>();
    private Map<Marker, DijkstraUtil.Node> markerMap = new HashMap<>();
    private DijkstraUtil.Node selectedNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spa_map);

        dbHelper = new FirestoreHelper();
        initialRadius = getIntent().getDoubleExtra("RADIUS", 5.0);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnViewDetail.setOnClickListener(v -> {
            if (selectedNode != null) {
                Intent intent = new Intent(this, SpaDetailActivity.class);
                intent.putExtra("SPA_ID", selectedNode.spa.getSpaId());
                intent.putExtra("DISTANCE", selectedNode.distance);
                startActivity(intent);
            }
        });

        binding.btnBookNow.setOnClickListener(v -> {
            if (selectedNode != null) {
                Intent intent = new Intent(this, BookingFormActivity.class);
                intent.putExtra("SPA_ID", selectedNode.spa.getSpaId());
                intent.putExtra("SPA_NAME", selectedNode.spa.getSpaName());
                intent.putExtra("SPA_IMAGE", selectedNode.spa.getImageUrl());
                startActivity(intent);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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

    // Lấy vị trí hiện tại rồi tải danh sách spa (gọi cả từ khi kiểm tra quyền lẫn callback cấp quyền)
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

    // Lắng nghe realtime: có spa mới/đổi thông tin là bản đồ tự cập nhật ngay
    private void fetchSpas() {
        if (spaListener != null) return; // đã đăng ký rồi thì thôi

        spaListener = dbHelper.getSpasQuery()
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    allSpas.clear();
                    for (QueryDocumentSnapshot document : value) {
                        Spa spa = document.toObject(Spa.class);
                        spa.setSpaId(document.getId());
                        allSpas.add(spa);
                    }
                    processSpas();
                });
    }

    // Lọc spa trong bán kính bằng Dijkstra rồi vẽ marker lên bản đồ
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

        binding.tvRadiusInfo.setText(String.format("Bán kính %.0f km", finalRadius));
        binding.tvSpaCount.setText(String.format("%d spa", filteredSpas.size()));

        mMap.clear();
        markerMap.clear();

        for (DijkstraUtil.Node node : filteredSpas) {
            LatLng spaLatLng = new LatLng(node.spa.getLatitude(), node.spa.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(spaLatLng)
                    .title(node.spa.getSpaName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpetspa)));
            markerMap.put(marker, node);
        }

        mMap.setOnMarkerClickListener(marker -> {
            selectedNode = markerMap.get(marker);
            if (selectedNode != null) {
                showSpaInfo(selectedNode);
            }
            return false;
        });

        mMap.setOnMapClickListener(latLng -> binding.cvSpaInfo.setVisibility(View.GONE));
    }

    private void showSpaInfo(DijkstraUtil.Node node) {
        binding.tvSpaName.setText(node.spa.getSpaName());
        binding.tvSpaAddress.setText(node.spa.getAddress());
        binding.tvDistance.setText(String.format("%.1f km", node.distance));
        binding.cvSpaInfo.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Gỡ listener realtime khi đóng màn hình để tránh rò rỉ bộ nhớ
        if (spaListener != null) {
            spaListener.remove();
            spaListener = null;
        }
    }
}
