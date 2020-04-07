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
    private TextView clickNoteDetail;
    private TextView clickAppointDetail;

    private String upcomingAppointID = "";
    private String recentNoteID = "";

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
        clickNoteDetail = findViewById(R.id.clickNoteDetail);
        clickAppointDetail = findViewById(R.id.clickAppointDetail);

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

                    case R.id.reminder:
                        startActivity(new Intent(getApplicationContext(), AddAppointmentActivity.class));
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
                                String timestamp = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mediName.setText(document.get("name").toString());

                                    if (document.get("dose").toString().isEmpty()) {
                                        mediDose.setText("Take as needed");
                                    } else {
                                        mediDose.setText(document.get("dose").toString());
                                    }

                                    String doseTime = "";
                                    ArrayList<String> timeList = (ArrayList<String>) document.get("time");

                                    if (timeList.size() != 0) {
                                        for (String t : timeList) {
                                            doseTime += t;
                                            doseTime += "/";
                                        }
                                    }
                                    mediTime.setText(doseTime);
                                    mediDate.setText(timestamp);
                                }
                            }
                            else{
                                mediName.setText("");
                                mediDose.setText("");
                                mediTime.setText("");
                                mediDate.setText("No medication need to take yet.");
                                Log.d("photo Activity", "can't found this users medication");
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
                                    upcomingAppointID = document.getId();
                                }
                            }
                            else{
                                appointDate.setText("You don't have any appointment yet!");
                                appointTime.setText("");
                                appointLoc.setText("");
                                appointDoc.setText("");
                                clickAppointDetail.setClickable(false);

                            }
                        }
                    }
                });
        //set most recent doctor's note
        mFirestore.collection("doctor's note")
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.getResult().size() != 0){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String timestamp = document.get("timestamp").toString();
                                    recentNoteID = document.getId();
                                    String date = timestamp.split("_")[0];
                                    String time = timestamp.split("_")[1];
                                    date = date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8);
                                    time = time.substring(0,2)+":"+time.substring(2,4);
                                    timestamp = date + "  " + time;
                                    timeAdded.setText(timestamp);
                                }
                            }
                            else{
                                timeAdded.setText("You don't have any doctor's note yet!");
                                clickNoteDetail.setText("");
                                clickNoteDetail.setClickable(false);
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

    public void onClick_appointDetail(View view){
        Intent intent = new Intent(ProfileActivity.this, AppointmentDetailActivity.class);
        intent.putExtra("appointID", upcomingAppointID);
        startActivity(intent);
    }

    public void onClick_NoteDetail(View view){
        Intent intent = new Intent(ProfileActivity.this, DoctorNoteDetailActivity.class);
        intent.putExtra("noteID", recentNoteID);
        startActivity(intent);
    }

    public void onClick_AudioDetail(View view) {
        Intent intent = new Intent(ProfileActivity.this, AudioList.class);
        startActivity(intent);
    }
}
