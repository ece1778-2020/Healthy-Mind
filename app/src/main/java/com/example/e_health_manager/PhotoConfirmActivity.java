package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoConfirmActivity extends AppCompatActivity {

    String photoPath;
    String newPhotoPath;
    File localFile = null;

    private ImageView photo;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_confirm);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Intent intent = getIntent();
        photoPath = intent.getStringExtra("photoPath");
        photo = findViewById(R.id.currentPhoto);

        File f = new File(photoPath);
        Uri uri = Uri.fromFile(f);
        photo.setImageURI(uri);
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
        //use firebase MLKit to analysis the photo
        StorageReference filepath = storageRef.child("assets").child("filled_PODs.png");
        final Context ctx = this;

        try{
            localFile = File.createTempFile("images", ".jpg");
        } catch (IOException e){
            e.printStackTrace();
        }
        filepath.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Uri uri = Uri.fromFile(localFile);

                Log.d("Photo Confirm Activity", "The local uri of PODs is: "+uri);

                FirebaseVisionImage cuimage = null;
                try {
                    cuimage = FirebaseVisionImage.fromFilePath(ctx, uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //start analysis
                FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer();

                final Task<FirebaseVisionText> result = detector.processImage(cuimage)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                //text recognition is done, start analysis
                                String resultText = firebaseVisionText.getText();
                                for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                                    String blockText = block.getText();
                                    Log.d("Photo Confirm Activity", "The result of block is: "+blockText);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Photo Confirm Activity", "The result of PODs text recognition is fail: "+e);
                            }
                        });
            }
        });
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
