package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private TextView usernameText;
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        usernameText = findViewById(R.id.profile_username);
        emailText = findViewById(R.id.profile_email);

        //get current user
        final FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        //set profile image

        usernameText.setText(user.getDisplayName());
        emailText.setText(user.getEmail());

    }

    public void onClick_signout(View view) {
        //method to sign out the current user
        mAuth.signOut();
        //back to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
