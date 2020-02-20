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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    private BottomNavigationView navbar;

    private String currentPhotoPath;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

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
                }
                return false;
            }
        });
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timeStamp,  /* prefix */
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
            File f = new File(currentPhotoPath);
            uri = Uri.fromFile(f);
            Log.d("upload picture", "The current picture path is: "+currentPhotoPath);
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

    }

    public void onClick_chooseManually(View view) {

    }
}
