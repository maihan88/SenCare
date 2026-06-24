package com.example.sencare.activities.spaowner;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.sencare.R;
import com.example.sencare.activities.dashboard.SpaOwnerHomeActivity;
import com.example.sencare.activities.map.MapPickerActivity;
import com.example.sencare.models.Spa;
import com.example.sencare.utils.CloudinaryUtil;
import com.example.sencare.utils.FirebaseUtil;
import com.example.sencare.utils.FirestoreHelper;
import com.example.sencare.utils.ImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class SpaFormActivity extends AppCompatActivity {

    private static final int MAP_PICKER_REQUEST_CODE = 1001;
    private static final int IMAGE_PICK_REQUEST_CODE = 1002;
    private static final int IMAGE_CAPTURE_REQUEST_CODE = 1003;
    private static final int CAMERA_PERMISSION_CODE = 101;

    private EditText etSpaName, etAddress, etPhone, etDescription, etServices, etPriceMin, etPriceMax, etLocation;
    private ImageButton btnPickMap, btnBack;
    private ImageView ivSpaImage;
    private MaterialButton btnSelectImage, btnCaptureImage, btnSave;

    private FirestoreHelper dbHelper;
    private String currentSpaId;
    private double latitude, longitude;
    private Uri selectedImageUri;
    private String uploadedImageUrl, uploadedPublicId;
    private boolean isEditMode = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spa_form);

        CloudinaryUtil.init(this);
        dbHelper = new FirestoreHelper();

        initViews();
        handleIntent();

        btnBack.setOnClickListener(v -> finish());

        btnPickMap.setOnClickListener(v -> {
            Intent intent = new Intent(SpaFormActivity.this, MapPickerActivity.class);
            startActivityForResult(intent, MAP_PICKER_REQUEST_CODE);
        });

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE);
        });

        btnCaptureImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });

        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAPTURE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Cần quyền Camera để chụp ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews() {
        etSpaName = findViewById(R.id.etSpaName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etServices = findViewById(R.id.etServices);
        etPriceMin = findViewById(R.id.etPriceMin);
        etPriceMax = findViewById(R.id.etPriceMax);
        etLocation = findViewById(R.id.etLocation);
        btnPickMap = findViewById(R.id.btnPickMap);
        btnBack = findViewById(R.id.btnBack);
        ivSpaImage = findViewById(R.id.ivSpaImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        btnSave = findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu thông tin...");
        progressDialog.setCancelable(false);
    }

    private void handleIntent() {
        String spaId = getIntent().getStringExtra("SPA_ID");
        if (spaId != null) {
            isEditMode = true;
            currentSpaId = spaId;
            loadSpaData(spaId);
        } else {
            currentSpaId = UUID.randomUUID().toString();
        }
    }

    private void loadSpaData(String spaId) {
        dbHelper.getSpa(spaId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Spa spa = documentSnapshot.toObject(Spa.class);
                if (spa != null) {
                    etSpaName.setText(spa.getSpaName());
                    etAddress.setText(spa.getAddress());
                    etPhone.setText(spa.getPhone());
                    etDescription.setText(spa.getDescription());
                    if (spa.getPriceRange() != null && spa.getPriceRange().contains(" - ")) {
                        String[] parts = spa.getPriceRange().split(" - ");
                        etPriceMin.setText(parts[0].replaceAll("[^\\d]", ""));
                        etPriceMax.setText(parts[1].replaceAll("[^\\d]", ""));
                    }
                    latitude = spa.getLatitude();
                    longitude = spa.getLongitude();
                    etLocation.setText(latitude + ", " + longitude);
                    
                    if (spa.getServices() != null) {
                        etServices.setText(String.join(", ", spa.getServices()));
                    }
                    
                    uploadedImageUrl = spa.getImageUrl();
                    uploadedPublicId = spa.getImagePublicId();
                    Glide.with(this).load(uploadedImageUrl).into(ivSpaImage);
                }
            }
        });
    }

    private String buildPriceRange(String minStr, String maxStr) {
        if (minStr.isEmpty() && maxStr.isEmpty()) return "";
        String min = minStr.isEmpty() ? "0" : minStr;
        String max = maxStr.isEmpty() ? min : maxStr;
        String fMin = String.format("%,d", Long.parseLong(min)).replace(',', '.');
        String fMax = String.format("%,d", Long.parseLong(max)).replace(',', '.');
        return fMin + " - " + fMax + " VNĐ";
    }

    private void validateAndSave() {
        String name = etSpaName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String servicesStr = etServices.getText().toString().trim();
        String priceMinStr = etPriceMin.getText().toString().trim();
        String priceMaxStr = etPriceMax.getText().toString().trim();
        String priceRange = buildPriceRange(priceMinStr, priceMaxStr);

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            uploadImageAndSave(name, address, phone, description, servicesStr, priceRange);
        } else if (uploadedImageUrl != null) {
            saveSpaToFirestore(name, address, phone, description, servicesStr, priceRange);
        } else {
            Toast.makeText(this, "Vui lòng chọn ảnh spa", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageAndSave(String name, String address, String phone, String description, String servicesStr, String priceRange) {
        btnSave.setEnabled(false);
        progressDialog.show();
        MediaManager.get().upload(selectedImageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        uploadedImageUrl = (String) resultData.get("secure_url");
                        uploadedPublicId = (String) resultData.get("public_id");
                        saveSpaToFirestore(name, address, phone, description, servicesStr, priceRange);
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        btnSave.setEnabled(true);
                        progressDialog.dismiss();
                        Toast.makeText(SpaFormActivity.this, "Lỗi upload ảnh: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    private void saveSpaToFirestore(String name, String address, String phone, String description, String servicesStr, String priceRange) {
        if (!progressDialog.isShowing()) progressDialog.show();
        ArrayList<String> servicesList = new ArrayList<>(Arrays.asList(servicesStr.split(",\\s*")));
        
        Spa spa = new Spa();
        spa.setSpaId(currentSpaId);
        spa.setOwnerId(FirebaseUtil.getCurrentUserId());
        spa.setSpaName(name);
        spa.setAddress(address);
        spa.setPhone(phone);
        spa.setDescription(description);
        spa.setServices(servicesList);
        spa.setPriceRange(priceRange);
        spa.setLatitude(latitude);
        spa.setLongitude(longitude);
        spa.setImageUrl(uploadedImageUrl);
        spa.setImagePublicId(uploadedPublicId);
        spa.setUpdatedAt(Timestamp.now());
        if (!isEditMode) {
            spa.setCreatedAt(Timestamp.now());
            spa.setOpen(true);
        }

        dbHelper.saveSpa(spa).addOnSuccessListener(aVoid -> {
            updateUserAndFinish();
        }).addOnFailureListener(e -> {
            btnSave.setEnabled(true);
            progressDialog.dismiss();
            Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserAndFinish() {
        String uid = FirebaseUtil.getCurrentUserId();
        dbHelper.updateUserSpaInfo(uid, currentSpaId, true).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
            if (isEditMode) {
                finish();
            } else {
                startActivity(new Intent(this, SpaOwnerHomeActivity.class));
                finish();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            btnSave.setEnabled(true);
            Toast.makeText(this, "Lỗi cập nhật user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == MAP_PICKER_REQUEST_CODE) {
                latitude = data.getDoubleExtra("latitude", 0);
                longitude = data.getDoubleExtra("longitude", 0);
                String address = data.getStringExtra("address");
                etLocation.setText(latitude + ", " + longitude);
                if (address != null && !address.isEmpty()) etAddress.setText(address);
            } else if (requestCode == IMAGE_PICK_REQUEST_CODE) {
                selectedImageUri = data.getData();
                ivSpaImage.setImageURI(selectedImageUri);
            } else if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    android.graphics.Bitmap imageBitmap = (android.graphics.Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        ivSpaImage.setImageBitmap(imageBitmap);
                        selectedImageUri = ImageUtil.getImageUri(this, imageBitmap);
                        Toast.makeText(this, "Ảnh đã được chụp", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
