package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoConfirmActivity extends AppCompatActivity {

    String photoPath;
    String newPhotoPath;

    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_confirm);

        Intent intent = getIntent();
        photoPath = intent.getStringExtra("photoPath");

        photo = findViewById(R.id.currentPhoto);

        File f = new File(photoPath);
        Uri uri = Uri.fromFile(f);

        photo.setImageURI(uri);

        Log.d("PhotoConfirmActivity", "This photo is in: "+photoPath);
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        newPhotoPath = image.getAbsolutePath();
        return image;
    }

    //after user has taken the profile picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            File f = new File(newPhotoPath);
            Uri uri = Uri.fromFile(f);

            photo.setImageURI(uri);
        }
    }

    public void onClick_yes(View view) {

    }

    public void onClick_anotherPhoto(View view) {
        //create an intent to open the camera app
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast toast = Toast.makeText(PhotoConfirmActivity.this,
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
}
