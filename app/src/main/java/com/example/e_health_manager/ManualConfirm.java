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

public class ManualConfirm extends AppCompatActivity {

    String doctor_note_id;

    HashMap<String, Object> doctor_note_data;

    ArrayList medicationList = new ArrayList();

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;

    TextView m1, m2, m3, m4;

    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_confirm);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        // Current user.
        userID = mAuth.getCurrentUser().getUid();

        m1 = findViewById(R.id.textView11);
        m2 = findViewById(R.id.textView12);
        m3 = findViewById(R.id.textView13);
        m4 = findViewById(R.id.textView14);

        recyclerView = findViewById(R.id.recyclerView);


        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        // TODO: CAUTION: callingActivityIntent could be from the dataAdapterList.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
            if (callingActivityIntent.getStringArrayListExtra("medicationList") != null) {
                medicationList = callingActivityIntent.getStringArrayListExtra("medicationList");
            }
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }


        ArrayList<HashMap<String, Object>> medications = (ArrayList<HashMap<String, Object>>) doctor_note_data.get("medications");

        int medication_count = medications.size();

        if (!medicationList.isEmpty()) {
            // skip the following part.
            medication_count = -1;
        }

        if (medication_count > 0) {
            HashMap<String, Object> m1_item = medications.get(0);
            String m1_text = "take: " + m1_item.get("name").toString() + ", for: " + m1_item.get("reason").toString() + ", at time: " + m1_item.get("time").toString();
            medicationList.add(m1_text);
            m1.setText(m1_text);
        }

        if (medication_count > 1) {
            HashMap<String, Object> m2_item = medications.get(1);
            String m2_text = "take: " + m2_item.get("name").toString() + ", for: " + m2_item.get("reason").toString() + ", at time: " + m2_item.get("time").toString();
            medicationList.add(m2_text);
            m2.setText(m2_text);
        }

        if (medication_count > 2) {
            HashMap<String, Object> m3_item = medications.get(2);
            String m3_text = "take: " + m3_item.get("name").toString() + ", for: " + m3_item.get("reason").toString() + ", at time: " + m3_item.get("time").toString();
            medicationList.add(m3_text);
            m3.setText(m3_text);
        }

        if (medication_count > 3) {
            HashMap<String, Object> m4_item = medications.get(3);
            String m4_text = "take: " + m4_item.get("name").toString() + ", for: " + m4_item.get("reason").toString() + ", at time: " + m4_item.get("time").toString();
            medicationList.add(m4_text);
            m4.setText(m4_text);
        }


        // Show 1 medication in a row.
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);

        DataAdapterList dataAdapter = new DataAdapterList(getApplicationContext(), medicationList);
        recyclerView.setAdapter(dataAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



//        db.collection("doctor's note")
//                .document(doctor_note_id)
//                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                        ArrayList<HashMap<String, Object>> medications = (ArrayList<HashMap<String, Object>>) documentSnapshot.get("medications");
//
//                        int medication_count = medications.size();
//
//                        if (medication_count > 0) {
//                            HashMap<String, Object> m1_item = medications.get(0);
//                            String m1_text = "take: " + m1_item.get("name").toString() + ", for: " + m1_item.get("reason").toString() + ", at time: " + m1_item.get("time").toString();
//                            m1.setText(m1_text);
//                        }
//
//                        if (medication_count > 1) {
//                            HashMap<String, Object> m2_item = medications.get(1);
//                            String m2_text = "take: " + m2_item.get("name").toString() + ", for: " + m2_item.get("reason").toString() + ", at time: " + m2_item.get("time").toString();
//                            m2.setText(m2_text);
//                        }
//
//                        if (medication_count > 2) {
//                            HashMap<String, Object> m3_item = medications.get(2);
//                            String m3_text = "take: " + m3_item.get("name").toString() + ", for: " + m3_item.get("reason").toString() + ", at time: " + m3_item.get("time").toString();
//                            m3.setText(m3_text);
//                        }
//
//                        if (medication_count > 3) {
//                            HashMap<String, Object> m4_item = medications.get(3);
//                            String m4_text = "take: " + m4_item.get("name").toString() + ", for: " + m4_item.get("reason").toString() + ", at time: " + m4_item.get("time").toString();
//                            m4.setText(m4_text);
//                        }
//                    }
//                });


    }

    public void onClick_submit(View view) {

        ArrayList medicationList = (ArrayList) doctor_note_data.get("medications");
        for (Object m : medicationList) {
            db.collection("medications")
                    .add(m);
        }



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
