package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class ManualMedication extends AppCompatActivity {

    ArrayList<HashMap<String, Object>> medicationList = new ArrayList<>();

    HashMap<String, Object> doctor_note_data;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;

    Button addBtn, delBtn;
    Context context;

    View hline0, hline1, hline2, hline3, hline4;

    TextView name1, name2, name3, name4, m1, m2, m3, m0;

    TextView c1, c2, c3, c4;
    TextView c5, c6, c7, c8;
    TextView c9, c10, c11, c12;
    TextView c13, c14, c15, c16;

    TextView d1, d2, d3, d4;

    EditText n1_input, n2_input, n3_input, n0_input;
    EditText m1_input, m2_input, m3_input, m0_input;

    EditText d1_input, d2_input, d3_input, d4_input;

    CheckBox c1_input, c2_input, c3_input, c4_input;
    CheckBox c5_input, c6_input, c7_input, c8_input;
    CheckBox c9_input, c10_input, c11_input, c12_input;
    CheckBox c13_input, c14_input, c15_input, c16_input;

    int medicationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_medication);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        // Current user.
        userID = mAuth.getCurrentUser().getUid();

        addBtn = (Button) findViewById(R.id.add_m_btn);
        delBtn = (Button) findViewById(R.id.del);

        hline0 = findViewById(R.id.hline0);
        hline1 = findViewById(R.id.hline);

        hline2 = findViewById(R.id.hline2);
        hline3 = findViewById(R.id.hline4);
        hline4 = findViewById(R.id.hline6);

        name1 = findViewById(R.id.medication_name);
        name2 = findViewById(R.id.n1);
        name3 = findViewById(R.id.n2);
        name4 = findViewById(R.id.n3);

        n0_input = findViewById(R.id.medication2_input);
        n1_input = findViewById(R.id.n1_input);
        n2_input = findViewById(R.id.n2_input);
        n3_input = findViewById(R.id.n3_input);

        m0_input = findViewById(R.id.medication_name_input);
        m1_input = findViewById(R.id.m1_input);
        m2_input = findViewById(R.id.m2_input);
        m3_input = findViewById(R.id.m3_input);

        d1_input = findViewById(R.id.d_i1);
        d2_input = findViewById(R.id.d_i2);
        d3_input = findViewById(R.id.d_i3);
        d4_input = findViewById(R.id.d_i4);

        m0 = findViewById(R.id.medication2);
        m1 = findViewById(R.id.m1);
        m2 = findViewById(R.id.m2);
        m3 = findViewById(R.id.m3);

        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        c4 = findViewById(R.id.c4);

        c5 = findViewById(R.id.c5);
        c6 = findViewById(R.id.c6);
        c7 = findViewById(R.id.c7);
        c8 = findViewById(R.id.c8);

        c9 = findViewById(R.id.c9);
        c10 = findViewById(R.id.c10);
        c11 = findViewById(R.id.c11);
        c12 = findViewById(R.id.c12);

        c13 = findViewById(R.id.c13);
        c14 = findViewById(R.id.c14);
        c15 = findViewById(R.id.c15);
        c16 = findViewById(R.id.c16);

        d1 = findViewById(R.id.d1);
        d2 = findViewById(R.id.d2);
        d3 = findViewById(R.id.d3);
        d4 = findViewById(R.id.d4);

        c1_input = findViewById(R.id.c1_input);
        c2_input = findViewById(R.id.c2_input);
        c3_input = findViewById(R.id.c3_input);
        c4_input = findViewById(R.id.c4_input);

        c5_input = findViewById(R.id.c5_input);
        c6_input = findViewById(R.id.c6_input);
        c7_input = findViewById(R.id.c7_input);
        c8_input = findViewById(R.id.c8_input);

        c9_input = findViewById(R.id.c9_input);
        c10_input = findViewById(R.id.c10_input);
        c11_input = findViewById(R.id.c11_input);
        c12_input = findViewById(R.id.c12_input);

        c13_input = findViewById(R.id.c13_input);
        c14_input = findViewById(R.id.c14_input);
        c15_input = findViewById(R.id.c15_input);
        c16_input = findViewById(R.id.c16_input);

        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medicationCount == 4) {
                    addBtn.setEnabled(false);
                    Toast.makeText(ManualMedication.this, "You can only add four medications at one form.", Toast.LENGTH_SHORT).show();
                } else {

                    if (medicationCount == 1) {
                        // check if empty.
                        if (TextUtils.isEmpty(m0_input.getText().toString())) {
                            m0_input.setError("medication name cannot be blank.");
                            Toast.makeText(ManualMedication.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } if (medicationCount == 2) {
                        if (TextUtils.isEmpty(n1_input.getText().toString())) {
                            n1_input.setError("medication name cannot be blank.");
                            Toast.makeText(ManualMedication.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } if (medicationCount == 3) {
                        if (TextUtils.isEmpty(n2_input.getText().toString())) {
                            n2_input.setError("medication name cannot be blank.");
                            Toast.makeText(ManualMedication.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    medicationCount++;

                    if (medicationCount == 2) {
                        hline2.setVisibility(View.VISIBLE);
                        name2.setVisibility(View.VISIBLE);
                        n1_input.setVisibility(View.VISIBLE);
                        m1.setVisibility(View.VISIBLE);
                        m1_input.setVisibility(View.VISIBLE);
                        c5.setVisibility(View.VISIBLE);
                        c6.setVisibility(View.VISIBLE);
                        c7.setVisibility(View.VISIBLE);
                        c8.setVisibility(View.VISIBLE);

                        d2.setVisibility(View.VISIBLE);

                        c5_input.setVisibility(View.VISIBLE);
                        c6_input.setVisibility(View.VISIBLE);
                        c7_input.setVisibility(View.VISIBLE);
                        c8_input.setVisibility(View.VISIBLE);

                        d2_input.setVisibility(View.VISIBLE);

                    } else if (medicationCount == 3) {
                        hline3.setVisibility(View.VISIBLE);
                        name3.setVisibility(View.VISIBLE);
                        n2_input.setVisibility(View.VISIBLE);
                        m2.setVisibility(View.VISIBLE);
                        m2_input.setVisibility(View.VISIBLE);
                        c9.setVisibility(View.VISIBLE);
                        c10.setVisibility(View.VISIBLE);
                        c11.setVisibility(View.VISIBLE);
                        c12.setVisibility(View.VISIBLE);

                        d3.setVisibility(View.VISIBLE);

                        c9_input.setVisibility(View.VISIBLE);
                        c10_input.setVisibility(View.VISIBLE);
                        c11_input.setVisibility(View.VISIBLE);
                        c12_input.setVisibility(View.VISIBLE);

                        d3_input.setVisibility(View.VISIBLE);

                    } else if (medicationCount == 4) {
                        hline4.setVisibility(View.VISIBLE);
                        name4.setVisibility(View.VISIBLE);
                        n3_input.setVisibility(View.VISIBLE);
                        m3.setVisibility(View.VISIBLE);
                        m3_input.setVisibility(View.VISIBLE);
                        c13.setVisibility(View.VISIBLE);
                        c14.setVisibility(View.VISIBLE);
                        c15.setVisibility(View.VISIBLE);
                        c16.setVisibility(View.VISIBLE);

                        d4.setVisibility(View.VISIBLE);

                        c13_input.setVisibility(View.VISIBLE);
                        c14_input.setVisibility(View.VISIBLE);
                        c15_input.setVisibility(View.VISIBLE);
                        c16_input.setVisibility(View.VISIBLE);

                        d4_input.setVisibility(View.VISIBLE);

                    } else if (medicationCount == 1) {
                        hline0.setVisibility(View.VISIBLE);
                        hline1.setVisibility(View.VISIBLE);
                        m0.setVisibility(View.VISIBLE);
                        m0_input.setVisibility(View.VISIBLE);
                        n0_input.setVisibility(View.VISIBLE);
                        name1.setVisibility(View.VISIBLE);

                        c1.setVisibility(View.VISIBLE);
                        c2.setVisibility(View.VISIBLE);
                        c3.setVisibility(View.VISIBLE);
                        c4.setVisibility(View.VISIBLE);

                        d1.setVisibility(View.VISIBLE);

                        c1_input.setVisibility(View.VISIBLE);
                        c2_input.setVisibility(View.VISIBLE);
                        c3_input.setVisibility(View.VISIBLE);
                        c4_input.setVisibility(View.VISIBLE);

                        d1_input.setVisibility(View.VISIBLE);

                    }
                }
                delBtn.setEnabled(true);
                // Toast.makeText(ManualMedication.this, Integer.toString(medicationCount) , Toast.LENGTH_SHORT).show();
            }
        });




        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medicationCount == 0) {
                    delBtn.setEnabled(false);
                    Toast.makeText(ManualMedication.this, "You haven't enter the medication.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    medicationCount--;
                    if (medicationCount == 3) {
                        hline4.setVisibility(View.INVISIBLE);
                        name4.setVisibility(View.INVISIBLE);
                        n3_input.setVisibility(View.INVISIBLE);
                        m3.setVisibility(View.INVISIBLE);
                        m3_input.setVisibility(View.INVISIBLE);
                        c13.setVisibility(View.INVISIBLE);
                        c14.setVisibility(View.INVISIBLE);
                        c15.setVisibility(View.INVISIBLE);
                        c16.setVisibility(View.INVISIBLE);

                        c13_input.setVisibility(View.INVISIBLE);
                        c14_input.setVisibility(View.INVISIBLE);
                        c15_input.setVisibility(View.INVISIBLE);
                        c16_input.setVisibility(View.INVISIBLE);

                        d4_input.setVisibility(View.INVISIBLE);
                        d4.setVisibility(View.INVISIBLE);

                    } else if (medicationCount == 2) {
                        hline3.setVisibility(View.INVISIBLE);
                        name3.setVisibility(View.INVISIBLE);
                        n2_input.setVisibility(View.INVISIBLE);
                        m2.setVisibility(View.INVISIBLE);
                        m2_input.setVisibility(View.INVISIBLE);
                        c9.setVisibility(View.INVISIBLE);
                        c10.setVisibility(View.INVISIBLE);
                        c11.setVisibility(View.INVISIBLE);
                        c12.setVisibility(View.INVISIBLE);

                        c9_input.setVisibility(View.INVISIBLE);
                        c10_input.setVisibility(View.INVISIBLE);
                        c11_input.setVisibility(View.INVISIBLE);
                        c12_input.setVisibility(View.INVISIBLE);

                        d3_input.setVisibility(View.INVISIBLE);
                        d3.setVisibility(View.INVISIBLE);

                    } else if (medicationCount == 1) {
                        hline2.setVisibility(View.INVISIBLE);
                        name2.setVisibility(View.INVISIBLE);
                        n1_input.setVisibility(View.INVISIBLE);
                        m1.setVisibility(View.INVISIBLE);
                        m1_input.setVisibility(View.INVISIBLE);
                        c5.setVisibility(View.INVISIBLE);
                        c6.setVisibility(View.INVISIBLE);
                        c7.setVisibility(View.INVISIBLE);
                        c8.setVisibility(View.INVISIBLE);

                        c5_input.setVisibility(View.INVISIBLE);
                        c6_input.setVisibility(View.INVISIBLE);
                        c7_input.setVisibility(View.INVISIBLE);
                        c8_input.setVisibility(View.INVISIBLE);

                        d2_input.setVisibility(View.INVISIBLE);
                        d2.setVisibility(View.INVISIBLE);

                    } else if (medicationCount == 0) {
                        hline0.setVisibility(View.INVISIBLE);
                        hline1.setVisibility(View.INVISIBLE);
                        m0.setVisibility(View.INVISIBLE);
                        m0_input.setVisibility(View.INVISIBLE);
                        n0_input.setVisibility(View.INVISIBLE);
                        name1.setVisibility(View.INVISIBLE);

                        c1.setVisibility(View.INVISIBLE);
                        c2.setVisibility(View.INVISIBLE);
                        c3.setVisibility(View.INVISIBLE);
                        c4.setVisibility(View.INVISIBLE);

                        c1_input.setVisibility(View.INVISIBLE);
                        c2_input.setVisibility(View.INVISIBLE);
                        c3_input.setVisibility(View.INVISIBLE);
                        c4_input.setVisibility(View.INVISIBLE);

                        d1_input.setVisibility(View.INVISIBLE);
                        d1.setVisibility(View.INVISIBLE);

                        // The following remove the whole 'medications' field.
                        // when there is only one medication left, and delete is pressed.
                        // Remove the 'medications' field from the document.
                        // db.collection("users").document(userID).update("medications", FieldValue.delete());

                        // delete the doctor's note for doctor_note_id.
//                        db.collection("doctor's note")
//                                .document(doctor_note_id)
//                                .update("medications", FieldValue.delete());

                    }
                }
                addBtn.setEnabled(true);
                // Toast.makeText(ManualMedication.this, Integer.toString(medicationCount) , Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void onClick_next_page(View view) {
        if (medicationCount > 0) {
            medicationList.clear();

            // check if empty.
            if (TextUtils.isEmpty(m0_input.getText().toString())) {
                m0_input.setError("medication name cannot be blank.");
                return;
            }

            HashMap<String, Object> medication = new HashMap<>();
            medication.put("name", m0_input.getText().toString());
            medication.put("reason", n0_input.getText().toString());

            medication.put("dose", d1_input.getText().toString());

            ArrayList<String> timeList = new ArrayList<>();
            if (c1_input.isChecked()) {
                timeList.add("morning");
            }
            if (c2_input.isChecked()) {
                timeList.add("noon");
            }
            if (c3_input.isChecked()) {
                timeList.add("afternoon");
            }
            if (c4_input.isChecked()) {
                timeList.add("night");
            }
            medication.put("time", timeList);
            medication.put("user_id", userID);
            medicationList.add(medication);


            if (medicationCount > 1) {

                // check if empty.
                if (TextUtils.isEmpty(n1_input.getText().toString())) {
                    n1_input.setError("medication name cannot be blank.");
                    return;
                }

                HashMap<String, Object> medication2 = new HashMap<>();
                medication2.put("name", n1_input.getText().toString());
                medication2.put("reason", m1_input.getText().toString());

                medication2.put("dose", d2_input.getText().toString());

                ArrayList<String> timeList2 = new ArrayList<>();
                if (c5_input.isChecked()) {
                    timeList2.add("morning");
                }
                if (c6_input.isChecked()) {
                    timeList2.add("noon");
                }
                if (c7_input.isChecked()) {
                    timeList2.add("afternoon");
                }
                if (c8_input.isChecked()) {
                    timeList2.add("night");
                }
                medication2.put("time", timeList2);
                medication2.put("user_id", userID);
                medicationList.add(medication2);


                if (medicationCount > 2) {

                    // check if empty.
                    if (TextUtils.isEmpty(n2_input.getText().toString())) {
                        n2_input.setError("medication name cannot be blank.");
                        return;
                    }

                    HashMap<String, Object> medication3 = new HashMap<>();
                    medication3.put("name", n2_input.getText().toString());
                    medication3.put("reason", m2_input.getText().toString());

                    medication3.put("dose", d3_input.getText().toString());

                    ArrayList<String> timeList3 = new ArrayList<>();
                    if (c9_input.isChecked()) {
                        timeList3.add("morning");
                    }
                    if (c10_input.isChecked()) {
                        timeList3.add("noon");
                    }
                    if (c11_input.isChecked()) {
                        timeList3.add("afternoon");
                    }
                    if (c12_input.isChecked()) {
                        timeList3.add("night");
                    }
                    medication3.put("time", timeList3);
                    medication3.put("user_id", userID);
                    medicationList.add(medication3);

                    if (medicationCount > 3) {

                        // check if empty.
                        if (TextUtils.isEmpty(n3_input.getText().toString())) {
                            n3_input.setError("medication name cannot be blank.");
                            return;
                        }


                        HashMap<String, Object> medication4 = new HashMap<>();
                        medication4.put("name", n3_input.getText().toString());
                        medication4.put("reason", m3_input.getText().toString());

                        medication4.put("dose", d4_input.getText().toString());

                        ArrayList<String> timeList4 = new ArrayList<>();
                        if (c13_input.isChecked()) {
                            timeList4.add("morning");
                        }
                        if (c14_input.isChecked()) {
                            timeList4.add("noon");
                        }
                        if (c15_input.isChecked()) {
                            timeList4.add("afternoon");
                        }
                        if (c16_input.isChecked()) {
                            timeList4.add("night");
                        }
                        medication4.put("time", timeList4);
                        medication4.put("user_id", userID);
                        medicationList.add(medication4);

                    }

                }

            }
        }

        // medicationList is a list of medication HashMap.
        // doctor_note_data.put("medications", medicationList);



        // documentReference contains the reference to the user (collection) data in the database.
        // DocumentReference userDocumentRef = db.collection("users").document(userID);
        // update medications field in user (don't need to store it in the users collection).
        // userDocumentRef.update("medications", medicationList);
        // delete field.
        // userDocumentRef.update("medications", FieldValue.delete());


        // DocumentReference doctorNoteDocRef = db.collection("doctor's note").document(doctor_note_id);
        // update medications field.
        // doctorNoteDocRef.update("medications", medicationList);


        Log.d("test Dict", medicationList.toString());

        // go to next page intent.
        Intent intent = new Intent(this, ManualFeel.class);

        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        // send list of medications.
        intent.putExtra("medicationList", medicationList);
        startActivity(intent);
    }
}
