package com.example.sencare.activities.vet;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.example.sencare.R;
import com.example.sencare.databinding.ActivityVetMapBinding;
import com.example.sencare.models.VetClinic;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class VetMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityVetMapBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private VetClinic selectedClinic;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVetMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupMap();
        setupBottomSheet();
        setupListeners();
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetClinic);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnCall.setOnClickListener(v -> {
            if (selectedClinic != null && selectedClinic.getPhone() != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + selectedClinic.getPhone()));
                startActivity(intent);
            }
        });

        binding.btnDirections.setOnClickListener(v -> {
            if (selectedClinic != null) {
                // Sử dụng định dạng URL tường minh hơn cho Google Maps
                String uri = "google.navigation:q=" + selectedClinic.getLatitude() + "," + selectedClinic.getLongitude();
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback sang trình duyệt nếu không có app Google Maps
                    String webUri = "https://www.google.com/maps/dir/?api=1&destination=" +
                            selectedClinic.getLatitude() + "," + selectedClinic.getLongitude();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUri)));
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        checkLocationPermission();
        fetchVetClinics();

        mMap.setOnMarkerClickListener(marker -> {
            selectedClinic = (VetClinic) marker.getTag();
            if (selectedClinic != null) {
                showClinicDetails(selectedClinic);
            }
            return false;
        });

        mMap.setOnMapClickListener(latLng -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
            });
        }
    }

    private void fetchVetClinics() {
        db.collection("vetClinics")
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mMap.clear(); // Xóa marker cũ nếu có
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        VetClinic clinic = document.toObject(VetClinic.class);
                        clinic.setClinicId(document.getId());
                        addMarkerForClinic(clinic);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lấy dữ dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addMarkerForClinic(VetClinic clinic) {
        LatLng latLng = new LatLng(clinic.getLatitude(), clinic.getLongitude());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(clinic.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.vetpin)));
        if (marker != null) {
            marker.setTag(clinic);
        }
    }


    private void showClinicDetails(VetClinic clinic) {
        binding.tvClinicName.setText(clinic.getName());
        binding.tvClinicAddress.setText(clinic.getAddress());
        binding.tvClinicPhone.setText("SĐT: " + clinic.getPhone());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            } else {
                Toast.makeText(this, "Quyền vị trí bị từ chối. Bạn có thể không thấy vị trí của mình trên bản đồ.", Toast.LENGTH_LONG).show();
            }
        }
    }
}