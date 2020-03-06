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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                            Log.d("Photo Confirm Activity", "This POD is: "+blockText);
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
        int mediPos = keywordList.indexOf("Medications I need to take");
        int feelPos = keywordList.indexOf("How I might feel and what to do");
        int routinePos = keywordList.indexOf("Changes to my routine");
        int appointmentPos = keywordList.indexOf("Appointments I have to go to");
        int moreInfoPos = keywordList.indexOf("Where to go for more information");
        int endPos = keywordList.size();
        Log.d("analyzeResult", "The start position of medication section on POD is: "+mediPos);
        Log.d("analyzeResult", "The start position of feeling section on POD is: "+feelPos);
        Log.d("analyzeResult", "The start position of routine section on POD is: "+routinePos);
        Log.d("analyzeResult", "The start position of appointment section on POD is: "+appointmentPos);
        Log.d("analyzeResult", "The start position of more info section on POD is: "+moreInfoPos);
        Log.d("analyzeResult", "The end position of POD is: "+endPos);

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
        ArrayList feelings = new ArrayList<String>();
        ArrayList feelToDo = new ArrayList<String>();
        if(feelPos-routinePos == 4){
            Log.d("analyzeResult", "The feeling section of this POD is empty");
        }
        else if (routinePos-feelPos == 5){
            Log.d("analyzeResult", "The feeling section of this POD has only emergency part");
            //find emergency part
            emergency = keywordList.get(feelPos+4);
        }
        else{
            Log.d("analyzeResult", "The feeling part of this POD is not empty");
            int numFeelingSet = 0;
            //based on how many items in between startPos and endPos
            //if odd, means have emergency part
            //if even, means emergency part is empty
            if((routinePos-feelPos)%2 != 0){
                Log.d("analyzeResult", "The feeling section of this POD has emergency part");
                //find emergency part
                emergency = keywordList.get(feelPos+3+3);
                Log.d("analyzeResult", "The emergency part of this POD is: "+emergency);
                //find feeling part. Firstly, determine how many feelings
                numFeelingSet = (routinePos - feelPos - 3 - 2)/2;
                for(int i = 0; i<numFeelingSet; i++){
                    if(i == 0){
                        feelings.add(keywordList.get(feelPos+4));
                        feelToDo.add(keywordList.get(feelPos+5));
                    }
                    else{
                        feelings.add(keywordList.get(feelPos+7+(i-1)*2));
                        feelToDo.add(keywordList.get(feelPos+8+(i-1)*2));
                    }
                }
            }
            else{
                //find feeling part. Firstly, determine how many feelings
                numFeelingSet = (routinePos - feelPos - 3 - 1)/2;
                for(int i = 0; i<numFeelingSet; i++){
                    if(i == 0){
                        feelings.add(keywordList.get(feelPos+4));
                        feelToDo.add(keywordList.get(feelPos+5));
                    }
                    else{
                        feelings.add(keywordList.get(feelPos+7+(i-1)*2));
                        feelToDo.add(keywordList.get(feelPos+8+(i-1)*2));
                    }
                }
            }
            for(int i = 0; i<feelings.size(); i++){
                Log.d("analyzeResult", "The feeling of this POD is: "+feelings.get(i));
                Log.d("analyzeResult", "The solution to feeling of this POD is: "+feelToDo.get(i));
            }
        }

        //Find medication
        int numMedication = (feelPos-mediPos-4)/3;
        ArrayList mediNames = new ArrayList<String>();
        ArrayList mediFors = new ArrayList<String>();
        ArrayList mediIntros = new ArrayList<String>();
        Log.d("testing", "analyzeResult: the number of medication is: "+numMedication);
        for(int i = 0; i<numMedication; i++){
            mediNames.add(keywordList.get(mediPos+4));
            mediFors.add(keywordList.get(mediPos+5));
            mediIntros.add(keywordList.get(mediPos+6));
        }
        for(int i = 0; i<mediNames.size(); i++){
            Log.d("analyzeResult", "The name of medication of this POD is: "+mediNames.get(i));
            Log.d("analyzeResult", "The for of medication of this POD is: "+mediFors.get(i));
            Log.d("analyzeResult", "The intro of medication of this POD is: "+mediIntros.get(i));
        }

        //Find changes to routine
        ArrayList activityRoutines = new ArrayList<String>();
        ArrayList introRoutines = new ArrayList<String>();
        if(appointmentPos-routinePos < 5){
            Log.d("analyzeResult", "The routine section of this POD is empty");
        }
        else{
            int numRoutines = (appointmentPos - routinePos - 1 - 2)/2;
            for(int i = 0; i<numRoutines; i++){
                activityRoutines.add(keywordList.get(routinePos + 3 + i*2));
                introRoutines.add(keywordList.get(routinePos + 4 + i*2));
            }
            for(int i = 0; i<activityRoutines.size(); i++){
                Log.d("analyzeResult", "The activity of this POD is: "+activityRoutines.get(i));
                Log.d("analyzeResult", "The intro of this activity of this POD is: "+introRoutines.get(i));
            }
        }

        //Find appointment to go
        String appointDoc = null;
        String appointReason = null;
        String appointDate = null;
        String appointTime = null;
        String appointLoc = null;
        String appointPhone = null;
        for(int i = 1; i<6; i++){
            if(keywordList.get(appointmentPos+i).contains("see") && (keywordList.get(appointmentPos+i).contains("Location") == false)){
                appointDoc = keywordList.get(appointmentPos + i).substring(keywordList.get(appointmentPos + i).lastIndexOf("see") + 4);
            }
            else if(keywordList.get(appointmentPos+i).contains("for") && (keywordList.get(appointmentPos+i).contains("Location") == false)){
                appointReason = keywordList.get(appointmentPos + i).substring(keywordList.get(appointmentPos + i).lastIndexOf("for") + 4);
            }
            else if(keywordList.get(appointmentPos+i).contains("on") && (keywordList.get(appointmentPos+i).contains("Location") == false)){
                Pattern pattern = Pattern.compile("on(.*?)at", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(keywordList.get(appointmentPos + i));
                while (matcher.find()) {
                    appointDate = matcher.group(1);
                }
                appointTime = keywordList.get(appointmentPos + i).substring(keywordList.get(appointmentPos + i).lastIndexOf("at") + 3);
            }
            else if(keywordList.get(appointmentPos+i).contains("Location")){
                appointLoc = keywordList.get(appointmentPos + i).substring(keywordList.get(appointmentPos + i).lastIndexOf(":") + 2);
            }
        }
        appointPhone = keywordList.get(appointmentPos+5);
        Log.d("analyzeResult", "The doctor of appointment of this POD is: "+appointDoc);
        Log.d("analyzeResult", "The reason of appointment of this POD is: "+appointReason);
        Log.d("analyzeResult", "The date of appointment of this POD is: "+appointDate);
        Log.d("analyzeResult", "The time of appointment of this POD is: "+appointTime);
        Log.d("analyzeResult", "The location of appointment of this POD is: "+appointLoc);
        Log.d("analyzeResult", "The phone of appointment of this POD is: "+appointPhone);

        //Find more info

    }

}
