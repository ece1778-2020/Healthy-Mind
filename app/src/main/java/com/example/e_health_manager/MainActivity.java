package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //check if user is already logged in, if so, go to profile page
        //else, stay at this page
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //go to profile page
            String email = user.getEmail();
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    public void onClick_signup(View view) {
        //go to sign up activity
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void onClick_login(View view) {
        //go to sign up activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
