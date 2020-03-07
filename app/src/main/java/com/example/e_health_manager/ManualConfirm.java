package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ManualConfirm extends AppCompatActivity {

    HashMap<String, Object> doctor_note_data;

    HashMap<String, Object> appointment;

    ArrayList medicationTextList = new ArrayList();

    ArrayList<HashMap<String, Object>> medicationMapList = new ArrayList<>();

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;

    TextView appointmentTextView, date_came, date_left, reason1;

    RecyclerView recyclerView, recyclerView_feel, recyclerView_routine, recyclerView_info;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_confirm);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        // Current user.
        userID = mAuth.getCurrentUser().getUid();


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView_feel = findViewById(R.id.recyclerView2);
        recyclerView_routine = findViewById(R.id.recyclerView3);
        recyclerView_info = findViewById(R.id.recyclerView5);

        appointmentTextView = findViewById(R.id.tv4);
        date_came = findViewById(R.id.date_came_ed);
        date_left = findViewById(R.id.date_left_ed);
        reason1 = findViewById(R.id.reason10_ed);

        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        // TODO: CAUTION: callingActivityIntent could be from the dataAdapterList.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
//            String PARENT_ACTIVITY_REF = callingActivityIntent.getStringExtra("PARENT_ACTIVITY_REF");
//
//            if (PARENT_ACTIVITY_REF.equals("ManualOwnNotes")) {
//                doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
//            }
            doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
            appointment = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("appointment");
            medicationMapList = (ArrayList<HashMap<String, Object>>) callingActivityIntent.getSerializableExtra("medicationList");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }


        // ArrayList<HashMap<String, Object>> medications = (ArrayList<HashMap<String, Object>>) doctor_note_data.get("medications");


        for (HashMap<String, Object> m : medicationMapList) {
            String m_text = "Take: " + m.get("name").toString() + " with dose: " + m.get("dose") + ", for: " + m.get("reason").toString() + ", at time: " + m.get("time").toString();
            medicationTextList.add(m_text);
        }


        // Show 1 medication in a row.
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);


        // medications
        DataAdapterList dataAdapter = new DataAdapterList(ManualConfirm.this,
                                                                medicationTextList,
                                                                medicationMapList,
                                                                doctor_note_data,
                                                                appointment);

        recyclerView.setAdapter(dataAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        // feeling
        ArrayList<HashMap<String, Object>> feelingList = (ArrayList<HashMap<String, Object>>) doctor_note_data.get("feelings_and_instructions");

        DataAdapterFeel dataAdapterFeel = new DataAdapterFeel(ManualConfirm.this,
                                                                medicationMapList,
                                                                doctor_note_data,
                                                                appointment);

        recyclerView_feel.setAdapter(dataAdapterFeel);

        recyclerView_feel.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        // routine_changes
        ArrayList<HashMap<String, Object>> routine_changes = (ArrayList<HashMap<String, Object>>) doctor_note_data.get("routine_changes");

        DataAdapterRoutine dataAdapterRoutine = new DataAdapterRoutine(ManualConfirm.this,
                                                                        medicationMapList,
                                                                        doctor_note_data,
                                                                        appointment);

        recyclerView_routine.setAdapter(dataAdapterRoutine);

        recyclerView_routine.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        // more info
        DataAdapterInfo dataAdapterInfo = new DataAdapterInfo(ManualConfirm.this,
                                                                        medicationMapList,
                                                                        doctor_note_data,
                                                                        appointment);

        recyclerView_info.setAdapter(dataAdapterInfo);

        recyclerView_info.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        String appointment_text;

        if (appointment.get("doctor").toString().isEmpty()) {
            appointment_text = "";
        } else {
            appointment_text = "Meet with: "
                    + appointment.get("doctor").toString()
                    + ", on: "
                    + appointment.get("date").toString()
                    + " at: "
                    + appointment.get("location").toString()
                    + " "
                    + appointment.get("time").toString()
                    + " for "
                    + appointment.get("reason").toString()
                    + ". (call: "
                    + appointment.get("phone").toString()
                    + ")";
        }

        appointmentTextView.setText(appointment_text);

        date_came.setText(doctor_note_data.get("came_date").toString());
        date_left.setText(doctor_note_data.get("left_date").toString());
        reason1.setText(doctor_note_data.get("reason_for_hospital").toString());

    }

    public void onClick_submit(View view) {

        // list of medication id.
        ArrayList<String> medication_ids = new ArrayList<>();

        for (Object m : medicationMapList) {
            // reference to each medication.
            DocumentReference medicationRef = db.collection("medications").document();
            medicationRef.set(m);
            medication_ids.add(medicationRef.getId());
        }

        DocumentReference appointmentRef = db.collection("appointments").document();
        appointmentRef.set(appointment);
        String appointment_id = appointmentRef.getId();

        doctor_note_data.put("medication_ids", medication_ids);
        doctor_note_data.put("hasAudio", false);
        doctor_note_data.put("appointment_id", appointment_id);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        doctor_note_data.put("timeStamp", timeStamp);


        db.collection("doctor's note")
            .add(doctor_note_data)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(ManualConfirm.this, "Doctor's note submitted successfully!", Toast.LENGTH_SHORT).show();

                    String doctor_note_id = documentReference.getId();
                    // update field.
                    // for debug purpose, we don't need the field in users collection.
                    db.collection("users")
                            .document(userID)
                            .update("doctor_note_id_list", FieldValue.arrayUnion(doctor_note_id));

                    Log.d("medication", "Medication added with UID: " + documentReference.getId());

                    Intent intent = new Intent(ManualConfirm.this, ProfileActivity.class);
                    // intent.putExtra("curr_doctor_note_id", doctor_note_id);
                    startActivity(intent);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Confirm", "Confirm Error" + e.toString());
                }
            });
    }
}
