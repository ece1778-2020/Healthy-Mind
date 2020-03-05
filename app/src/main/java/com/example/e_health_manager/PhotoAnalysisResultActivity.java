package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;

public class PhotoAnalysisResultActivity extends AppCompatActivity {

    private RecyclerView keywordListView;
    GridLayoutManager gridLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private ArrayList keywordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_analysis_result);

        keywordListView = findViewById(R.id.keywordList);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        keywordList = new ArrayList<String>();

        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        keywordListView.setLayoutManager(gridLayoutManager);

        //use real photo
        Intent intent = getIntent();
        String photoPath = intent.getStringExtra("photoPath");
        File f = new File(photoPath);
        Uri uri = Uri.fromFile(f);

        FirebaseVisionImage cuimage = null;
        try {
            cuimage = FirebaseVisionImage.fromFilePath(this, uri);
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
                        for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                            String blockText = block.getText();
                            blockText = blockText.substring(0, blockText.length() - 1);
                            //Log.d("Photo Confirm Activity", "This POD is:"+blockText+" with s: "+blockText.length());
                            keywordList.add(blockText);
                        }
                        //analyze result
                        analyzeResult(keywordList);
                        PhotoKeywordAdapter newAdapter = new PhotoKeywordAdapter(getApplicationContext(), keywordList);
                        keywordListView.setAdapter(newAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Photo Confirm Activity", "The result of PODs text recognition is fail: "+e);
                    }
                });
    }

    void analyzeResult(ArrayList<String> keywordList){
        //get name
        String name = keywordList.get(0).split("'")[0];
        Log.d("analyzeResult", "The owner of POD is: "+name);

        //get come date and leave date
        String comeDate = keywordList.get(1).split(" ")[5];
        String leaveDate = keywordList.get(1).split(" ")[9];
        Log.d("analyzeResult", "The come date of POD is: "+comeDate);
        Log.d("analyzeResult", "The leave date of POD is: "+leaveDate);

        //get why come to hospital
        String reasonToHospital = keywordList.get(3).substring(keywordList.get(3).lastIndexOf("have") + 5);
        Log.d("analyzeResult", "The reason to hospital on POD is: "+reasonToHospital);

        //get feelings
        String emergency = null;
        int startPos = keywordList.indexOf("How I might feel and what to do");
        int endPos = keywordList.indexOf("Changes to my routine");
        Log.d("analyzeResult", "The start position of feeling section on POD is: "+startPos);
        Log.d("analyzeResult", "The end position of feeling section on POD is: "+endPos);
        if(endPos-startPos == 4){
            Log.d("analyzeResult", "The feeling section of this POD is empty");
        }
        else if (endPos-startPos == 5){
            Log.d("analyzeResult", "The feeling section of this POD has only emergency part");
            //find emergency part
            emergency = keywordList.get(startPos+4);
        }
        else{
            Log.d("analyzeResult", "The feeling part of this POD is not empty");
            //based on how many items in between startPos and endPos
            //if odd, means have emergency part
            //if even, means emergency part is empty
            if((endPos-startPos)%2 != 0){
                Log.d("analyzeResult", "The feeling section of this POD has emergency part");
                //find emergency part
                emergency = keywordList.get(startPos+3+3);
                Log.d("analyzeResult", "The emergency part of this POD is: "+emergency);
                //find feeling part. Firstly, determine how many feelings

            }
            else{
                Log.d("analyzeResult", "The feeling section of this POD does not has emergency part");
                //find feeling part. Firstly, determine how many feelings

            }
        }
    }

}
