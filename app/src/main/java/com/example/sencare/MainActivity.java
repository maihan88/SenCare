package com.example.sencare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sencare.activities.auth.LoginActivity;
import com.example.sencare.activities.auth.RegisterActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnLogin, btnRegister, btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnClose = findViewById(R.id.btnClose);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
        btnClose.setOnClickListener(v-> finish());
    }
}
