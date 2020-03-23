package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DoctorNoteDetailActivity extends AppCompatActivity {

    String noteID;

    ExpandableListView noteDetailListView;
    private ExpandableNoteDetailAdapter noteDetailAdapter;
    private List<String> detailDataHeader;
    private HashMap<String, List<HashMap>> detailHashMap;

    List<HashMap> generalChildren = new ArrayList<>();
    List<HashMap> mediChildren = new ArrayList<>();
    List<HashMap> feelingChildren = new ArrayList<>();
    List<HashMap> routineChildren = new ArrayList<>();
    List<HashMap> appointChildren = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String audioLoc;

    int count;
    int count2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_note_detail);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        noteID = intent.getStringExtra("noteID");
        audioLoc = intent.getStringExtra("audioLoc");
        Log.d("testing", "onCreate: the id of this doctor note is: " + noteID);

        noteDetailListView = findViewById(R.id.noteDetailListView);

        showSelectedNote();

        noteDetailListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                // Appointments section.
//                if (groupPosition == 4) {
//
//                    String see = ((HashMap) noteDetailAdapter.getChild(groupPosition, childPosition)).get("see").toString();
//                    if (!see.equals("You don't have any appointments.")) {
//                        // if there is at least one appointment.
//                        String appointID = ((HashMap) noteDetailAdapter.getChild(groupPosition, childPosition)).get("appointID").toString();
//                        Intent intent = new Intent(DoctorNoteDetailActivity.this, AppointmentDetailActivity.class);
//                        intent.putExtra("appointID", appointID);
//                        startActivity(intent);
//                    }
//
//                }

                // Replay audio recording.
                if (groupPosition == 6) {

                    String temp = ((HashMap) noteDetailAdapter.getChild(groupPosition, childPosition)).get("transcriptList").toString();
                    if (!temp.equals("You didn't record any audio.")) {


                        mFirestore.collection("doctor's note").document(noteID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Intent intent = new Intent(DoctorNoteDetailActivity.this, AudioReplay.class);
                                        intent.putExtra("noteID", noteID);
                                        intent.putExtra("audioLoc", documentSnapshot.get("audio_path").toString());
                                        startActivity(intent);
                                    }
                                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(DoctorNoteDetailActivity.class.toString(), e.toString());
                            }
                        });

                    }
                }

                return true;
            }
        });
    }

    private void showSelectedNote() {
        detailDataHeader = new ArrayList<>();
        detailHashMap = new HashMap<>();
        detailDataHeader.add("General Information");
        detailDataHeader.add("Medications");
        detailDataHeader.add("Feelings and What to do");
        detailDataHeader.add("Changes to my routine");
        detailDataHeader.add("Appointment");
        detailDataHeader.add("Additional notes");
        detailDataHeader.add("Doctor's audio transcript and recording");

        mFirestore.collection("doctor's note").document(noteID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            //set general information
                            HashMap<String, Object> itemHashMap = new HashMap<>();
                            itemHashMap.put("timestamp", document.get("timestamp"));
                            itemHashMap.put("comeDate", document.get("came_date"));
                            itemHashMap.put("leaveDate", document.get("left_date"));
                            itemHashMap.put("reason", document.get("reason_for_hospital"));
                            generalChildren.add(itemHashMap);
                            detailHashMap.put(detailDataHeader.get(0), generalChildren);
                            //set emergency
                            HashMap<String, Object> emerHashMap = new HashMap<>();
                            emerHashMap.put("emergency", document.get("go_to_emergency_if"));
                            feelingChildren.add(emerHashMap);
                            //set feelings
                            for (HashMap<String, Object> element : (List<HashMap<String, Object>>) document.get("feelings_and_instructions")) {
                                HashMap<String, Object> feelingHashMap = new HashMap<>();
                                feelingHashMap.put("feeling", element.get("feeling"));
                                feelingHashMap.put("instruction", element.get("instruction"));
                                feelingChildren.add(feelingHashMap);
                            }
                            detailHashMap.put(detailDataHeader.get(2), feelingChildren);
                            //set routines
                            if (((List<HashMap<String, Object>>) document.get("routine_changes")).size() != 0) {
                                for (HashMap<String, Object> element : (List<HashMap<String, Object>>) document.get("routine_changes")) {
                                    HashMap<String, Object> routineHashMap = new HashMap<>();
                                    routineHashMap.put("activity", element.get("activity"));
                                    routineHashMap.put("instruction", element.get("instruction"));
                                    routineChildren.add(routineHashMap);
                                }
                                detailHashMap.put(detailDataHeader.get(3), routineChildren);
                            } else {
                                HashMap<String, Object> routineHashMap = new HashMap<>();
                                routineHashMap.put("activity", "You don't have to change your routine");
                                routineChildren.add(routineHashMap);
                                detailHashMap.put(detailDataHeader.get(3), routineChildren);
                            }

                            //set own notes
                            String own_notes = document.get("notes").toString();
                            HashMap<String, Object> ownNotesHashMap = new HashMap<>();
                            List<HashMap> ownNotes = new ArrayList<>();
                            if (!own_notes.isEmpty()) {
                                ownNotesHashMap.put("ownNotes", own_notes);
                            } else {
                                ownNotesHashMap.put("ownNotes", "You don't have additional notes.");
                            }
                            ownNotes.add(ownNotesHashMap);
                            detailHashMap.put(detailDataHeader.get(5), ownNotes);


                            //set audio text.
                            ArrayList<String> transcriptList = (ArrayList<String>) document.get("transcript_text");

                            String display_text = "";
                            for (String s : transcriptList) {
                                display_text = display_text.concat(s);
                                display_text = display_text + ". ";
                            }

                            boolean hasAudio = (boolean) document.get("hasAudio");

                            HashMap<String, Object> transcripHashMap = new HashMap<>();
                            List<HashMap> lst1 = new ArrayList<>();
                            if (hasAudio) {
                                transcripHashMap.put("transcriptList", display_text);
                            } else {
                                transcripHashMap.put("transcriptList", "You didn't record any audio.");
                            }
                            lst1.add(transcripHashMap);

                            detailHashMap.put(detailDataHeader.get(6), lst1);

                            //set medications and appointment
                            List<String> medicationIDs = (List<String>) document.get("medication_ids");
                            String appointID = (String) document.get("appointment_id");
                            fetchMedications(medicationIDs, appointID);
                        }
                    }
                });
    }

    private void fetchMedications(final List<String> medicationIDs, final String appointID) {

        count2 = 0;

        for (count = 0; count < medicationIDs.size(); count++) {
            String mediid = medicationIDs.get(count);
            Log.d("testing", "onCreate: the id of this doctor note medication is: " + mediid);
            mFirestore.collection("medications").document(mediid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            HashMap<String, Object> mediHashMap = new HashMap<>();
                            mediHashMap.put("name", document.get("name"));
                            mediHashMap.put("dose", document.get("dose"));
                            mediHashMap.put("reason", document.get("reason"));
                            if (((ArrayList<String>) document.get("time")).size() != 0) {
                                String dosetime = "";
                                for (String element : (ArrayList<String>) document.get("time")) {
                                    element = element.toLowerCase();
                                    element = element.substring(0, 1).toUpperCase() + element.substring(1);
                                    dosetime = dosetime + element + ", ";
                                }
                                dosetime = dosetime.substring(0, dosetime.length() - 2);
                                mediHashMap.put("time", dosetime);
                            } else {
                                mediHashMap.put("time", "Take it as needed");
                            }
                            mediChildren.add(mediHashMap);
                            //last medication has been fetched
                            count2 = count2 + 1;
                            if (count2 == medicationIDs.size()) {
                                Log.d("testing", "doctor note: start fetch appointment");
                                detailHashMap.put(detailDataHeader.get(1), mediChildren);
                                //set appointment
                                fetchAppointment(appointID);
                            }
                        }
                    });
        }
    }

    private void fetchAppointment(String appointID) {
        Log.d("testing", "doctor note: the appointID is: " + appointID);
        mFirestore.collection("appointments").document(appointID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        HashMap<String, Object> appointHashMap = new HashMap<>();
                        appointHashMap.put("date", document.get("date"));
                        appointHashMap.put("doctor", document.get("doctor"));
                        appointHashMap.put("location", document.get("location"));
                        appointHashMap.put("phone", document.get("phone"));
                        appointHashMap.put("reason", document.get("reason"));
                        appointHashMap.put("time", document.get("time"));
                        appointChildren.add(appointHashMap);
                        detailHashMap.put(detailDataHeader.get(4), appointChildren);

                        Log.d("testing", "doctor note: the complete detailHashMap is: " + detailHashMap.toString());

                        // initialize expandable list view
                        noteDetailAdapter = new ExpandableNoteDetailAdapter(DoctorNoteDetailActivity.this, detailDataHeader, detailHashMap);
                        noteDetailListView.setAdapter(noteDetailAdapter);
                    }
                });
    }
}
