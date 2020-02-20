package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private BottomNavigationView navbar;
    private ImageView profileImage;

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
        navbar = findViewById(R.id.bottomNavigation);
        profileImage = findViewById(R.id.profilePic);

        //set profile picture
        StorageReference filepath = storageRef.child("assets").child("profilePic.png");
        Glide.with(this).load(filepath).apply(RequestOptions.circleCropTransform()).into(profileImage);

        //set profile button selected
        navbar.setSelectedItemId(R.id.dashboard);
        //perform itemselected listener
        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.note_list:
                        startActivity(new Intent(getApplicationContext(), NoteListActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.add:
                        startActivity(new Intent(getApplicationContext(), AddNoteActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

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
