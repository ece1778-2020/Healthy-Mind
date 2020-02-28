package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

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

public class PhotoAnalysisResultActivity extends AppCompatActivity {

    File localFile = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_analysis_result);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Log.d("Photo Confirm Activity", "block: I'm in photo analysis result activity right now");

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

}
