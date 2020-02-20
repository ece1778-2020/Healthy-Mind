package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PhotoConfirmActivity extends AppCompatActivity {

    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_confirm);

        Intent intent = getIntent();
        photoPath = intent.getStringExtra("photoPath");

        Log.d("PhotoConfirmActivity", "This photo is in: "+photoPath);
    }
}
