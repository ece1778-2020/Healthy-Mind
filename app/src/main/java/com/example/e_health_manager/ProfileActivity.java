package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private TextView usernameText;
    private TextView emailText;
    private BottomNavigationView navbar;
    private ImageView profileImage;
    //for profile content
    private TextView mediDate;
    private TextView mediTime;
    private TextView mediName;
    private TextView mediDose;
    private TextView appointDate;
    private TextView appointTime;
    private TextView appointLoc;
    private TextView appointDoc;
    private TextView timeAdded;

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

        mediDate = findViewById(R.id.mediDate);
        mediTime = findViewById(R.id.mediTime);
        mediName = findViewById(R.id.mediName);
        mediDose = findViewById(R.id.mediDose);
        appointDate = findViewById(R.id.appointDate);
        appointTime = findViewById(R.id.appointTime);
        appointLoc = findViewById(R.id.appointLoc);
        appointDoc = findViewById(R.id.appointDoc);
        timeAdded = findViewById(R.id.timeAdded);

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
        //set profile content
        setProfileContent();

    }

    public void setProfileContent(){
        //set upcoming medications
        mFirestore.collection("medications")
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid())
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.getResult().size() != 0){
                                Log.d("photo Activity", "found this users medication");
                                String timeStamp = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mediName.setText(document.get("name").toString());
                                    mediDose.setText(document.get("dose").toString());
                                    String doseTime = ((ArrayList<String>) document.get("time")).get(0);
                                    mediTime.setText(doseTime);
                                    mediDate.setText(timeStamp);
                                }
                            }
                            else{
                                Log.d("photo Activity", "dont found this users medication");
                            }
                        }
                    }
                });
        //set upcoming appointment
        String dateBound = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        mFirestore.collection("appointments")
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid())
                .whereGreaterThanOrEqualTo("date", dateBound)
                .orderBy("date", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.getResult().size() != 0){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    appointDate.setText(document.get("date").toString());
                                    appointTime.setText(document.get("time").toString());
                                    appointLoc.setText(document.get("location").toString());
                                    appointDoc.setText("Meet with: "+document.get("doctor").toString());
                                }
                            }
                            else{
                                Log.d("photo Activity", "dont found this users medication");
                            }
                        }
                    }
                });
        //set most recent doctor's note
        mFirestore.collection("doctor's note")
                .whereEqualTo("patient_id", mAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.getResult().size() != 0){
                                Log.d("photo Activity", "found this users note");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    timeAdded.setText(document.get("timestamp").toString());
                                }
                            }
                            else{
                                Log.d("photo Activity", "dont found this users note");
                            }
                        }
                    }
                });
    }

    public void onClick_signout(View view) {
        //method to sign out the current user
        mAuth.signOut();
        //back to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
