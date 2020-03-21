package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {

    ArrayList medicationList = new ArrayList();
    private String userID;

    private BottomNavigationView navbar;

    private String currentPhotoPath;
    private Uri uri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Current user.
        userID = mAuth.getCurrentUser().getUid();

        navbar = findViewById(R.id.bottomNavigation);

        //set profile button selected
        navbar.setSelectedItemId(R.id.add);
        //perform itemselected listener
        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.note_list:
                        startActivity(new Intent(getApplicationContext(), NoteListActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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
    }

    public File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timestamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //after user has taken the profile picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //go to set caption activity
            Intent intent = new Intent(this, PhotoConfirmActivity.class);
            intent.putExtra("photoPath", currentPhotoPath);
            startActivity(intent);
        }
    }

    public void onClick_choosePhoto(View view) {
        //create an intent to open the camera app
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast toast = Toast.makeText(AddNoteActivity.this,
                        "Error in creating file", Toast.LENGTH_SHORT);
                toast.show();
            }
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.androidproject.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            startActivityForResult(takePictureIntent, 1);
        }

    }

    public void onClick_chooseAudio(View view) {
        Intent intent = new Intent(this, TakeAudioActivity.class);
        startActivity(intent);
    }

    public void onClick_transcriptAudio(View view) {
        // transcript the audio directly, for testing purpose.
        // Intent intent = new Intent(this, SpeechToTextActivity.class);
        Intent intent = new Intent(this, SpeechTranscriptActivity.class);
        startActivity(intent);
    }

    public void onClick_chooseManually(View view) {
        // each time user click on this button, and new doctor note is created.
        HashMap<String, Object> doctor_note_data = new HashMap<>();
        // doctor_note_data.put("medications", medicationList);
        doctor_note_data.put("user_id", userID);

        Intent intent = new Intent(AddNoteActivity.this, InputManual.class);
        // put the current doctor's note id, send it to the next page.
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        startActivity(intent);
    }
}
