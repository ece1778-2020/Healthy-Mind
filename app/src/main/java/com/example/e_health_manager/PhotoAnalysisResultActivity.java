package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhotoAnalysisResultActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    //for confirmation form
    //for personal information
    private EditText nameText, reasonHosText, cameDateText, leaveDateText;
    //for medication section
    private EditText mediNameText1, mediDoseText1, mediForText1;
    private Switch mediMornSwitch1, mediNoonSwitch1, mediAftSwitch1, mediNightSwitch1;
    private EditText mediNameText2, mediDoseText2, mediForText2;
    private Switch mediMornSwitch2, mediNoonSwitch2, mediAftSwitch2, mediNightSwitch2;
    private EditText mediNameText3, mediDoseText3, mediForText3;
    private Switch mediMornSwitch3, mediNoonSwitch3, mediAftSwitch3, mediNightSwitch3;
    private EditText mediNameText4, mediDoseText4, mediForText4;
    private Switch mediMornSwitch4, mediNoonSwitch4, mediAftSwitch4, mediNightSwitch4;
    //for feeling section
    private EditText emergencyText;
    private EditText feelText1, feelText2, feelText3, feelText4;
    private EditText feelDoText1, feelDoText2, feelDoText3, feelDoText4;
    //for routine section
    private EditText routineActivityText1, routineActivityText2, routineActivityText3, routineActivityText4;
    private EditText routineInstructionText1, routineInstructionText2, routineInstructionText3, routineInstructionText4;
    //for appointment section
    private EditText appointSeeText, appointReasonText, appointDateText, appointTimeText, appointLocationText, appointPhoneText;

    //for photo analysis
    private ArrayList keywordList;
    String appointID;
    ArrayList medicationIDs = new ArrayList<String>();

    void initializeForm(){
        //for personal information
        nameText = findViewById(R.id.nameText);
        reasonHosText = findViewById(R.id.reasonHosText);
        cameDateText = findViewById(R.id.cameDateText);
        leaveDateText = findViewById(R.id.leaveDateText);
        //for medication section
        //1
        mediNameText1 = findViewById(R.id.mediNameText1);
        mediDoseText1 = findViewById(R.id.mediDoseText1);
        mediForText1 = findViewById(R.id.mediForText1);
        mediMornSwitch1 = findViewById(R.id.mediMornSwitch1);
        mediNoonSwitch1 = findViewById(R.id.mediNoonSwitch1);
        mediAftSwitch1 = findViewById(R.id.mediAftSwitch1);
        mediNightSwitch1 = findViewById(R.id.mediNightSwitch1);
        //2
        mediNameText2 = findViewById(R.id.mediNameText2);
        mediDoseText2 = findViewById(R.id.mediDoseText2);
        mediForText2 = findViewById(R.id.mediForText2);
        mediMornSwitch2 = findViewById(R.id.mediMornSwitch2);
        mediNoonSwitch2 = findViewById(R.id.mediNoonSwitch2);
        mediAftSwitch2 = findViewById(R.id.mediAftSwitch2);
        mediNightSwitch2 = findViewById(R.id.mediNightSwitch2);
        //3
        mediNameText3 = findViewById(R.id.mediNameText3);
        mediDoseText3 = findViewById(R.id.mediDoseText3);
        mediForText3 = findViewById(R.id.mediForText3);
        mediMornSwitch3 = findViewById(R.id.mediMornSwitch3);
        mediNoonSwitch3 = findViewById(R.id.mediNoonSwitch3);
        mediAftSwitch3 = findViewById(R.id.mediAftSwitch3);
        mediNightSwitch3 = findViewById(R.id.mediNightSwitch3);
        //4
        mediNameText4 = findViewById(R.id.mediNameText4);
        mediDoseText4 = findViewById(R.id.mediDoseText4);
        mediForText4 = findViewById(R.id.mediForText4);
        mediMornSwitch4 = findViewById(R.id.mediMornSwitch4);
        mediNoonSwitch4 = findViewById(R.id.mediNoonSwitch4);
        mediAftSwitch4 = findViewById(R.id.mediAftSwitch4);
        mediNightSwitch4 = findViewById(R.id.mediNightSwitch4);
        //for feeling section
        emergencyText = findViewById(R.id.emergencyText);
        feelText1 = findViewById(R.id.feelText1);
        feelText2 = findViewById(R.id.feelText2);
        feelText3 = findViewById(R.id.feelText3);
        feelText4 = findViewById(R.id.feelText4);
        feelDoText1 = findViewById(R.id.feelDoText1);
        feelDoText2 = findViewById(R.id.feelDoText2);
        feelDoText3 = findViewById(R.id.feelDoText3);
        feelDoText4 = findViewById(R.id.feelDoText4);
        //for routine section
        routineActivityText1 = findViewById(R.id.routineActivityText1);
        routineActivityText2 = findViewById(R.id.routineActivityText2);
        routineActivityText3 = findViewById(R.id.routineActivityText3);
        routineActivityText4 = findViewById(R.id.routineActivityText4);
        routineInstructionText1 = findViewById(R.id.routineInstructionText1);
        routineInstructionText2 = findViewById(R.id.routineInstructionText2);
        routineInstructionText3 = findViewById(R.id.routineInstructionText3);
        routineInstructionText4 = findViewById(R.id.routineInstructionText4);
        //for appointment section
        appointSeeText = findViewById(R.id.appointSeeText);
        appointReasonText = findViewById(R.id.appointReasonText);
        appointDateText = findViewById(R.id.appointDateText);
        appointTimeText = findViewById(R.id.appointTimeText);
        appointLocationText = findViewById(R.id.appointLocationText);
        appointPhoneText = findViewById(R.id.appointPhoneText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_analysis_result);

        initializeForm();
        keywordList = new ArrayList<String>();

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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


        String patientName, comeDate, leaveDate, reasonToHospital;
        //get name
        patientName = keywordList.get(0).split("'")[0];
        Log.d("analyzeResult", "The owner of POD is: "+patientName);
        //get come date and leave date
        String[] elements = keywordList.get(1).split(" ");
        comeDate = keywordList.get(1).split(" ")[5];
        leaveDate = elements[elements.length-1];
        Log.d("analyzeResult", "The come date of POD is: "+comeDate);
        Log.d("analyzeResult", "The leave date of POD is: "+leaveDate);
        //get why come to hospital
        reasonToHospital = keywordList.get(3).substring(keywordList.get(3).lastIndexOf("have") + 5);
        Log.d("analyzeResult", "The reason to hospital on POD is: "+reasonToHospital);
        //set personal information
        nameText.setText(patientName);
        reasonHosText.setText(reasonToHospital);
        cameDateText.setText(comeDate);
        leaveDateText.setText(leaveDate);


        //get feelings
        String emergency = null;
        ArrayList feelings = new ArrayList<String>();
        ArrayList feelToDo = new ArrayList<String>();
        if(feelPos-routinePos == 4){
            Log.d("analyzeResult", "The feeling section of this POD is empty");
        } else if (routinePos-feelPos == 5){
            Log.d("analyzeResult", "The feeling section of this POD has only emergency part");
            //find emergency part
            emergency = keywordList.get(feelPos+4);
        } else{
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
                    } else{
                        feelings.add(keywordList.get(feelPos+7+(i-1)*2));
                        feelToDo.add(keywordList.get(feelPos+8+(i-1)*2));
                    }
                }
            } else{
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
        //set feeling section
        setFeelSection(emergency, feelings, feelToDo);


        //Find medication
        ArrayList mediNames = new ArrayList<String>();
        ArrayList mediFors = new ArrayList<String>();
        ArrayList mediIntros = new ArrayList<String>();
        int numMedication = (feelPos-mediPos-5)/4;
        Log.d("testing", "analyzeResult: the number of medication is: "+numMedication);
        for(int i = 0; i<numMedication; i++){
            mediNames.add(keywordList.get(mediPos+5+i*4));
            mediFors.add(keywordList.get(mediPos+6+i*4));
            mediIntros.add(keywordList.get(mediPos+7+i*4));
        }
        for(int i = 0; i<mediNames.size(); i++){
            Log.d("analyzeResult", "The name of medication of this POD is: "+mediNames.get(i));
            Log.d("analyzeResult", "The for of medication of this POD is: "+mediFors.get(i));
            Log.d("analyzeResult", "The intro of medication of this POD is: "+mediIntros.get(i));
        }
        //set medication section
        setMedicationSection(mediNames, mediFors, mediIntros);


        //Find changes to routine
        ArrayList activityRoutines = new ArrayList<String>();
        ArrayList introRoutines = new ArrayList<String>();
        if(appointmentPos-routinePos < 5){
            Log.d("analyzeResult", "The routine section of this POD is empty");
        } else{
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
        setRoutineSection(activityRoutines, introRoutines);


        //Find appointment to go
        String appointDoc = null, appointReason = null, appointDate = null, appointTime = null, appointLoc = null, appointPhone = null;
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
        //convert appointment date to required format
        appointDate = formatAppointDate(appointDate);
        appointPhone = keywordList.get(appointmentPos+5);
        Log.d("analyzeResult", "The doctor of appointment of this POD is: "+appointDoc);
        Log.d("analyzeResult", "The reason of appointment of this POD is: "+appointReason);
        Log.d("analyzeResult", "The date of appointment of this POD is: "+appointDate);
        Log.d("analyzeResult", "The time of appointment of this POD is: "+appointTime);
        Log.d("analyzeResult", "The location of appointment of this POD is: "+appointLoc);
        Log.d("analyzeResult", "The phone of appointment of this POD is: "+appointPhone);
        //set appointment
        appointSeeText.setText(appointDoc);
        appointReasonText.setText(appointReason);
        appointDateText.setText(appointDate);
        appointTimeText.setText(appointTime);
        appointLocationText.setText(appointLoc);
        appointPhoneText.setText(appointPhone);


        //Find more info
    }

    void setMedicationSection(ArrayList<String>mediNames, ArrayList<String>mediFors, ArrayList<String>mediIntros){
        int mediSize = mediNames.size();
        if(mediNames.size() == 0){
            mediNameText1.setText("N/A");
        }
        else{
            for(int i = 0; i<mediSize; i++){
                if(i == 0){
                    mediNameText1.setText(mediNames.get(i));
                    mediForText1.setText(mediFors.get(i));
                    mediDoseText1.setText(mediIntros.get(i));
                }
                else if(i == 1){
                    mediNameText2.setText(mediNames.get(i));
                    mediForText2.setText(mediFors.get(i));
                    mediDoseText2.setText(mediIntros.get(i));
                }
                else if(i == 2){
                    mediNameText3.setText(mediNames.get(i));
                    mediForText3.setText(mediFors.get(i));
                    mediDoseText3.setText(mediIntros.get(i));
                }
                else if(i == 3){
                    mediNameText4.setText(mediNames.get(i));
                    mediForText4.setText(mediFors.get(i));
                    mediDoseText4.setText(mediIntros.get(i));
                }
            }
        }
    }

    void setFeelSection(String emergency, ArrayList<String> feelings, ArrayList<String> feelToDo){
        if(emergency == null){
            emergencyText.setText("");
        }
        else{
            emergencyText.setText(emergency);
        }
        int feelSize = feelings.size();
        if(feelings.size() == 0){
            feelText1.setText("N/A");
        }
        else{
            for(int i = 0; i<feelSize; i++){
                if(i == 0){
                    feelText1.setText(feelings.get(i));
                    feelDoText1.setText(feelToDo.get(i));
                }
                else if(i == 1){
                    feelText2.setText(feelings.get(i));
                    feelDoText2.setText(feelToDo.get(i));
                }
                else if(i == 2){
                    feelText3.setText(feelings.get(i));
                    feelDoText3.setText(feelToDo.get(i));
                }
                else if(i == 3){
                    feelText4.setText(feelings.get(i));
                    feelDoText4.setText(feelToDo.get(i));
                }
            }
        }
    }

    void setRoutineSection(ArrayList<String> activityRoutines, ArrayList<String> introRoutines){
        //routine section is empty
        if(activityRoutines.size() == 0 && introRoutines.size() == 0){
            routineActivityText1.setText("N/A");
        }
        else{
            int routineSize = activityRoutines.size();
            for(int i = 0; i<routineSize; i++){
                if(i == 0){
                    routineActivityText1.setText(activityRoutines.get(i));
                    routineInstructionText1.setText(introRoutines.get(i));
                }
                else if(i == 1){
                    routineActivityText2.setText(activityRoutines.get(i));
                    routineInstructionText2.setText(introRoutines.get(i));
                }
                else if(i == 2){
                    routineActivityText3.setText(activityRoutines.get(i));
                    routineInstructionText3.setText(introRoutines.get(i));
                }
                else if(i == 3){
                    routineActivityText4.setText(activityRoutines.get(i));
                    routineInstructionText4.setText(introRoutines.get(i));
                }
            }
        }
    }

    public void onClick_cancelResult(View view){
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    public void onClick_submitResult(View view){
        final String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //store doctor note
        Map<String, Object> doctorNote = new HashMap<>();
        doctorNote.put("appointment_id", appointID);
        doctorNote.put("came_date", cameDateText.getText().toString());
        doctorNote.put("go_to_emergency_if", emergencyText.getText().toString());
        doctorNote.put("hasAudio", false);
        doctorNote.put("left_date", leaveDateText.getText().toString());
        doctorNote.put("medication_ids", medicationIDs);
        doctorNote.put("notes", "");
        doctorNote.put("reason_for_hospital", reasonHosText.getText().toString());
        doctorNote.put("timestamp", timestamp);
        doctorNote.put("user_id", mAuth.getCurrentUser().getUid());
        //get feelings
        ArrayList<Map<String, Object>> feelings = new ArrayList<>();
        if(feelText1.getText().toString().length() > 1){
            Map<String, Object> feeling = new HashMap<>();
            feeling.put("feeling", feelText1.getText().toString());
            feeling.put("instruction", feelDoText1.getText().toString());
            feelings.add(feeling);
        }
        if(feelText2.getText().toString().length() > 1){
            Map<String, Object> feeling = new HashMap<>();
            feeling.put("feeling", feelText2.getText().toString());
            feeling.put("instruction", feelDoText2.getText().toString());
            feelings.add(feeling);
        }
        if(feelText3.getText().toString().length() > 1){
            Map<String, Object> feeling = new HashMap<>();
            feeling.put("feeling", feelText3.getText().toString());
            feeling.put("instruction", feelDoText3.getText().toString());
            feelings.add(feeling);
        }
        if(feelText4.getText().toString().length() > 1){
            Map<String, Object> feeling = new HashMap<>();
            feeling.put("feeling", feelText4.getText().toString());
            feeling.put("instruction", feelDoText4.getText().toString());
            feelings.add(feeling);
        }
        doctorNote.put("feelings_and_instructions", feelings);
        //get more info
        ArrayList<Map<String, Object>> moreInfo = new ArrayList<>();
        doctorNote.put("more_info", moreInfo);
        //get routines
        ArrayList<Map<String, Object>> routines = new ArrayList<>();
        if(routineActivityText1.getText().toString().length() > 1 && routineInstructionText1.getText().toString().length() > 1){
            Map<String, Object> routine = new HashMap<>();
            routine.put("activity", routineActivityText1.getText().toString());
            routine.put("instruction", routineInstructionText1.getText().toString());
            routines.add(routine);
        }
        if(routineActivityText2.getText().toString().length() > 1 && routineInstructionText2.getText().toString().length() > 1){
            Map<String, Object> routine = new HashMap<>();
            routine.put("activity", routineActivityText2.getText().toString());
            routine.put("instruction", routineInstructionText2.getText().toString());
            routines.add(routine);
        }
        if(routineActivityText3.getText().toString().length() > 1 && routineInstructionText3.getText().toString().length() > 1){
            Map<String, Object> routine = new HashMap<>();
            routine.put("activity", routineActivityText3.getText().toString());
            routine.put("instruction", routineInstructionText3.getText().toString());
            routines.add(routine);
        }
        if(routineActivityText4.getText().toString().length() > 1 && routineInstructionText4.getText().toString().length() > 1){
            Map<String, Object> routine = new HashMap<>();
            routine.put("activity", routineActivityText4.getText().toString());
            routine.put("instruction", routineInstructionText4.getText().toString());
            routines.add(routine);
        }
        doctorNote.put("routine_changes", routines);
        mFirestore.collection("doctor's note").add(doctorNote)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("photo analysis", "saving: New doctor note has been stored to the firestore");
                    }
                });


        //store appointment
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("user_id", mAuth.getCurrentUser().getUid());
        appointment.put("date", appointDateText.getText().toString());
        appointment.put("doctor", appointSeeText.getText().toString());
        appointment.put("location", appointLocationText.getText().toString());
        appointment.put("phone", appointPhoneText.getText().toString());
        appointment.put("reason", appointReasonText.getText().toString());
        appointment.put("time", appointTimeText.getText().toString());
        mFirestore.collection("appointments").add(appointment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        appointID = documentReference.getId();
                        mFirestore.collection("doctor's note")
                                .whereEqualTo("timestamp", timestamp)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isComplete()) {
                                            if (task.getResult().size() != 0){
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    document.getReference().update("appointment_id", appointID);
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                });


        //store medications
        if(mediNameText1.getText().toString().length() > 2){
            Map<String, Object> medication = new HashMap<>();
            medication.put("dose", mediDoseText1.getText().toString());
            medication.put("name", mediNameText1.getText().toString());
            medication.put("reason", mediForText1.getText().toString());
            medication.put("user_id", mAuth.getCurrentUser().getUid());
            //get time
            ArrayList<String> medicationTimes = new ArrayList<>();
            if(mediMornSwitch1.isChecked()){
                medicationTimes.add("Morning");
            }
            if(mediNoonSwitch1.isChecked()){
                medicationTimes.add("Noon");
            }
            if(mediAftSwitch1.isChecked()){
                medicationTimes.add("Afternoon");
            }
            if(mediNightSwitch1.isChecked()){
                medicationTimes.add("Night");
            }
            medication.put("time", medicationTimes);
            Task mediTask = mFirestore.collection("medications").add(medication)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            final String mediID = documentReference.getId();
                            mFirestore.collection("doctor's note")
                                    .whereEqualTo("timestamp", timestamp)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isComplete()) {
                                                if (task.getResult().size() != 0){
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().update("medication_ids", FieldValue.arrayUnion(mediID));
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    });
        }
        if(mediNameText2.getText().toString().length() > 2){
            Map<String, Object> medication = new HashMap<>();
            medication.put("dose", mediDoseText2.getText().toString());
            medication.put("name", mediNameText2.getText().toString());
            medication.put("reason", mediForText2.getText().toString());
            medication.put("user_id", mAuth.getCurrentUser().getUid());
            //get time
            ArrayList<String> medicationTimes = new ArrayList<>();
            if(mediMornSwitch2.isChecked()){
                medicationTimes.add("Morning");
            }
            if(mediNoonSwitch2.isChecked()){
                medicationTimes.add("Noon");
            }
            if(mediAftSwitch2.isChecked()){
                medicationTimes.add("Afternoon");
            }
            if(mediNightSwitch2.isChecked()){
                medicationTimes.add("Night");
            }
            medication.put("time", medicationTimes);
            Task mediTask = mFirestore.collection("medications").add(medication)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            final String mediID = documentReference.getId();
                            mFirestore.collection("doctor's note")
                                    .whereEqualTo("timestamp", timestamp)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isComplete()) {
                                                if (task.getResult().size() != 0){
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().update("medication_ids", FieldValue.arrayUnion(mediID));
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    });
        }
        if(mediNameText3.getText().toString().length() > 2){
            Map<String, Object> medication = new HashMap<>();
            medication.put("dose", mediDoseText3.getText().toString());
            medication.put("name", mediNameText3.getText().toString());
            medication.put("reason", mediForText3.getText().toString());
            medication.put("user_id", mAuth.getCurrentUser().getUid());
            //get time
            ArrayList<String> medicationTimes = new ArrayList<>();
            if(mediMornSwitch3.isChecked()){
                medicationTimes.add("Morning");
            }
            if(mediNoonSwitch3.isChecked()){
                medicationTimes.add("Noon");
            }
            if(mediAftSwitch3.isChecked()){
                medicationTimes.add("Afternoon");
            }
            if(mediNightSwitch3.isChecked()){
                medicationTimes.add("Night");
            }
            medication.put("time", medicationTimes);
            Task mediTask = mFirestore.collection("medications").add(medication)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            final String mediID = documentReference.getId();
                            mFirestore.collection("doctor's note")
                                    .whereEqualTo("timestamp", timestamp)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isComplete()) {
                                                if (task.getResult().size() != 0){
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().update("medication_ids", FieldValue.arrayUnion(mediID));
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    });
        }
        if(mediNameText4.getText().toString().length() > 2){
            Map<String, Object> medication = new HashMap<>();
            medication.put("dose", mediDoseText4.getText().toString());
            medication.put("name", mediNameText4.getText().toString());
            medication.put("reason", mediForText4.getText().toString());
            medication.put("user_id", mAuth.getCurrentUser().getUid());
            //get time
            ArrayList<String> medicationTimes = new ArrayList<>();
            if(mediMornSwitch4.isChecked()){
                medicationTimes.add("Morning");
            }
            if(mediNoonSwitch4.isChecked()){
                medicationTimes.add("Noon");
            }
            if(mediAftSwitch4.isChecked()){
                medicationTimes.add("Afternoon");
            }
            if(mediNightSwitch4.isChecked()){
                medicationTimes.add("Night");
            }
            medication.put("time", medicationTimes);
            Task mediTask = mFirestore.collection("medications").add(medication)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            final String mediID = documentReference.getId();
                            mFirestore.collection("doctor's note")
                                    .whereEqualTo("timestamp", timestamp)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isComplete()) {
                                                if (task.getResult().size() != 0){
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().update("medication_ids", FieldValue.arrayUnion(mediID));
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    });
        }


        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public String formatAppointDate(String appointDate){
        String formatedDate = null;
        if(appointDate == null || appointDate.indexOf("/") == -1){
            return formatedDate;
        }
        else{
            String appoint1 = null, appoint2 = null, appoint3 = null;
            appoint1 = appointDate.split("/")[0];
            appoint2 = appointDate.split("/")[1];
            appoint3 = appointDate.split("/")[2];
            appoint1 = appoint1.replace(" ", "");
            appoint2 = appoint2.replace(" ", "");
            appoint3 = appoint3.replace(" ", "");
            if(appoint1.length() >= 4){
                formatedDate = appoint1 + "/" + appoint2 + "/" + appoint3;
            }
            else if(appoint3.length() >= 4){
                formatedDate = appoint3 + "/" + appoint1 + "/" + appoint2;
            }
            formatedDate.replace(" ", "");
            return formatedDate;
        }
    }
}
