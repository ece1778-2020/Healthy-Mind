package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangeRoutine extends AppCompatActivity {

    ArrayList routine_changes = new ArrayList();
    ArrayList medicationList = new ArrayList();

    HashMap<String, Object> doctor_note_data;

    EditText activity1, instruction1;
    EditText activity2, instruction2;
    EditText activity3, instruction3;
    EditText activity4, instruction4;

    View hline0, hline1, hline2, hline3, hline4;

    Button addBtn, delBtn;

    int itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_routine);

        addBtn = findViewById(R.id.add_btn);
        delBtn = findViewById(R.id.del);

        activity1 = findViewById(R.id.input1);
        instruction1 = findViewById(R.id.input2);

        activity2 = findViewById(R.id.i3a);
        instruction2 = findViewById(R.id.i3b);

        activity3 = findViewById(R.id.i4a);
        instruction3 = findViewById(R.id.i4b);

        activity4 = findViewById(R.id.i5a);
        instruction4 = findViewById(R.id.i5b);

        hline0 = findViewById(R.id.hline0);
        hline1 = findViewById(R.id.hline1);
        hline2 = findViewById(R.id.hline2);
        hline3 = findViewById(R.id.hline3);
        hline4 = findViewById(R.id.hline4);

        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
            medicationList = callingActivityIntent.getStringArrayListExtra("medicationList");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCount == 4) {
                    addBtn.setEnabled(false);
                    Toast.makeText(ChangeRoutine.this, "You can only add four items at one form.", Toast.LENGTH_SHORT).show();
                } else {
                    if (itemCount == 1) {
                        // check if empty.
                        if (TextUtils.isEmpty(activity1.getText().toString())) {
                            activity1.setError("Cannot be blank.");
                            Toast.makeText(ChangeRoutine.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (itemCount == 2) {
                        if (TextUtils.isEmpty(activity2.getText().toString())) {
                            activity2.setError("Cannot be blank.");
                            Toast.makeText(ChangeRoutine.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (itemCount == 3) {
                        if (TextUtils.isEmpty(activity3.getText().toString())) {
                            activity3.setError("Cannot be blank.");
                            Toast.makeText(ChangeRoutine.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    itemCount++;

                    if (itemCount == 1) {
                        hline0.setVisibility(View.VISIBLE);
                        hline1.setVisibility(View.VISIBLE);
                        activity1.setVisibility(View.VISIBLE);
                        instruction1.setVisibility(View.VISIBLE);

                    } else if (itemCount == 2) {
                        hline2.setVisibility(View.VISIBLE);
                        activity2.setVisibility(View.VISIBLE);
                        instruction2.setVisibility(View.VISIBLE);

                    } else if (itemCount == 3) {
                        hline3.setVisibility(View.VISIBLE);
                        activity3.setVisibility(View.VISIBLE);
                        instruction3.setVisibility(View.VISIBLE);

                    } else if (itemCount == 4) {
                        hline4.setVisibility(View.VISIBLE);
                        activity4.setVisibility(View.VISIBLE);
                        instruction4.setVisibility(View.VISIBLE);
                    }
                }
                delBtn.setEnabled(true);
                // Toast.makeText(ManualMedication.this, Integer.toString(itemCount) , Toast.LENGTH_SHORT).show();
            }
        });


        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCount == 0) {
                    delBtn.setEnabled(false);
                    Toast.makeText(ChangeRoutine.this, "You haven't enter the medication.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    itemCount--;
                    if (itemCount == 3) {
                        hline4.setVisibility(View.INVISIBLE);
                        activity4.setVisibility(View.INVISIBLE);
                        instruction4.setVisibility(View.INVISIBLE);

                    } else if (itemCount == 2) {
                        hline3.setVisibility(View.INVISIBLE);
                        activity3.setVisibility(View.INVISIBLE);
                        instruction3.setVisibility(View.INVISIBLE);

                    } else if (itemCount == 1) {
                        hline2.setVisibility(View.INVISIBLE);
                        activity2.setVisibility(View.INVISIBLE);
                        instruction2.setVisibility(View.INVISIBLE);

                    } else if (itemCount == 0) {
                        hline0.setVisibility(View.INVISIBLE);
                        hline1.setVisibility(View.INVISIBLE);
                        activity1.setVisibility(View.INVISIBLE);
                        instruction1.setVisibility(View.INVISIBLE);

                    }
                }
                addBtn.setEnabled(true);
            }
        });
    }




    public void onClick_next_page(View view) {
        // HashMap<String, String> routine_change = new HashMap<>();
        // routine_change.put("activity", activity1.getText().toString());
        // routine_change.put("instruction", instruction1.getText().toString());
        // routine_changes.add(routine_change);
        // doctor_note_data.put("routine_changes", routine_changes);

        Intent intent = new Intent(this, ManualAppointments.class);
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        // send list of medications.
        intent.putExtra("medicationList", medicationList);
        startActivity(intent);
    }
}
