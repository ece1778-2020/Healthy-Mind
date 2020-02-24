package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ManualFeel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_feel);
    }


    public void onClick_next_page(View view) {
        Intent intent = new Intent(this, ChangeRoutine.class);
        startActivity(intent);
    }
}
