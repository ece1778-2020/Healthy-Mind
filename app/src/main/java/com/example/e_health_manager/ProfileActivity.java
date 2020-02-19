package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

    }

    public void onClick_signout(View view) {
        //method to sign out the current user
        mAuth.signOut();
        //back to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
