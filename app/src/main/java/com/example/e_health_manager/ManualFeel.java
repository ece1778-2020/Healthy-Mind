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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ManualFeel extends AppCompatActivity {

    ArrayList<HashMap<String, Object>> medicationList = new ArrayList<>();

    ArrayList<HashMap<String, Object>> feelingList = new ArrayList<>();

    HashMap<String, Object> doctor_note_data;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    View hline0, hline1, hline2, hline3, hline4;

    Button addBtn, delBtn;

    EditText go_to_emergency_if;

    EditText my_feeling1, instruction1, my_feeling2, instruction2, my_feeling3, instruction3, my_feeling4, instruction4;

    int itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_feel);

        addBtn = findViewById(R.id.add_btn);
        delBtn = findViewById(R.id.del);

        go_to_emergency_if = findViewById(R.id.feel3_input);

        my_feeling1 = findViewById(R.id.feel1_input);
        instruction1 = findViewById(R.id.feel2_input);

        my_feeling2 = findViewById(R.id.f3a);
        instruction2 = findViewById(R.id.f3b);

        my_feeling3 = findViewById(R.id.f4a);
        instruction3 = findViewById(R.id.f4b);

        my_feeling4 = findViewById(R.id.f5a);
        instruction4 = findViewById(R.id.f5b);

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
            medicationList = (ArrayList<HashMap<String, Object>>) callingActivityIntent.getSerializableExtra("medicationList");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCount == 4) {
                    addBtn.setEnabled(false);
                    Toast.makeText(ManualFeel.this, "You can only add four items at one form.", Toast.LENGTH_SHORT).show();
                } else {
                    if (itemCount == 1) {
                        // check if empty.
                        if (TextUtils.isEmpty(my_feeling1.getText().toString())) {
                            my_feeling1.setError("Cannot be blank.");
                            Toast.makeText(ManualFeel.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (itemCount == 2) {
                        if (TextUtils.isEmpty(my_feeling2.getText().toString())) {
                            my_feeling2.setError("Cannot be blank.");
                            Toast.makeText(ManualFeel.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (itemCount == 3) {
                        if (TextUtils.isEmpty(my_feeling3.getText().toString())) {
                            my_feeling3.setError("Cannot be blank.");
                            Toast.makeText(ManualFeel.this, "Please finish the current form before you add the next one.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    itemCount++;

                    if (itemCount == 1) {
                        hline0.setVisibility(View.VISIBLE);
                        hline1.setVisibility(View.VISIBLE);
                        my_feeling1.setVisibility(View.VISIBLE);
                        instruction1.setVisibility(View.VISIBLE);

                    } else if (itemCount == 2) {
                        hline2.setVisibility(View.VISIBLE);
                        my_feeling2.setVisibility(View.VISIBLE);
                        instruction2.setVisibility(View.VISIBLE);

                    } else if (itemCount == 3) {
                        hline3.setVisibility(View.VISIBLE);
                        my_feeling3.setVisibility(View.VISIBLE);
                        instruction3.setVisibility(View.VISIBLE);

                    } else if (itemCount == 4) {
                        hline4.setVisibility(View.VISIBLE);
                        my_feeling4.setVisibility(View.VISIBLE);
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
                    Toast.makeText(ManualFeel.this, "You haven't enter the medication.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    itemCount--;
                    if (itemCount == 3) {
                        hline4.setVisibility(View.INVISIBLE);
                        my_feeling4.setVisibility(View.INVISIBLE);
                        instruction4.setVisibility(View.INVISIBLE);

                    } else if (itemCount == 2) {
                        hline3.setVisibility(View.INVISIBLE);
                        my_feeling3.setVisibility(View.INVISIBLE);
                        instruction3.setVisibility(View.INVISIBLE);

                    } else if (itemCount == 1) {
                        hline2.setVisibility(View.INVISIBLE);
                        my_feeling2.setVisibility(View.INVISIBLE);
                        instruction2.setVisibility(View.INVISIBLE);

                    } else if (itemCount == 0) {
                        hline0.setVisibility(View.INVISIBLE);
                        hline1.setVisibility(View.INVISIBLE);
                        my_feeling1.setVisibility(View.INVISIBLE);
                        instruction1.setVisibility(View.INVISIBLE);
                        
                    }
                }
                addBtn.setEnabled(true);
            }
        });

    }

    public void onClick_next_page(View view) {

        if (itemCount > 0) {
            feelingList.clear();

            // check if empty.
            if (TextUtils.isEmpty(my_feeling1.getText().toString())) {
                my_feeling1.setError("cannot be blank.");
                return;
            }

            if (TextUtils.isEmpty(instruction1.getText().toString())) {
                instruction1.setError("cannot be blank.");
                return;
            }

            HashMap<String, Object> f_i1 = new HashMap<>();
            f_i1.put("feeling", my_feeling1.getText().toString());
            f_i1.put("instruction", instruction1.getText().toString());

            feelingList.add(f_i1);

            if (itemCount > 1) {
                // check if empty.
                if (TextUtils.isEmpty(my_feeling2.getText().toString())) {
                    my_feeling2.setError("cannot be blank.");
                    return;
                }

                if (TextUtils.isEmpty(instruction2.getText().toString())) {
                    instruction2.setError("cannot be blank.");
                    return;
                }

                HashMap<String, Object> f_i2 = new HashMap<>();
                f_i2.put("feeling", my_feeling2.getText().toString());
                f_i2.put("instruction", instruction2.getText().toString());

                feelingList.add(f_i2);

                if (itemCount > 2) {
                    // check if empty.
                    if (TextUtils.isEmpty(my_feeling3.getText().toString())) {
                        my_feeling3.setError("cannot be blank.");
                        return;
                    }

                    if (TextUtils.isEmpty(instruction3.getText().toString())) {
                        instruction3.setError("cannot be blank.");
                        return;
                    }

                    HashMap<String, Object> f_i3 = new HashMap<>();
                    f_i3.put("feeling", my_feeling3.getText().toString());
                    f_i3.put("instruction", instruction3.getText().toString());
                    
                    feelingList.add(f_i3);

                    if (itemCount > 3) {
                        // check if empty.
                        if (TextUtils.isEmpty(my_feeling4.getText().toString())) {
                            my_feeling4.setError("cannot be blank.");
                            return;
                        }

                        if (TextUtils.isEmpty(instruction4.getText().toString())) {
                            instruction4.setError("cannot be blank.");
                            return;
                        }

                        HashMap<String, Object> f_i4 = new HashMap<>();
                        f_i4.put("feeling", my_feeling4.getText().toString());
                        f_i4.put("instruction", instruction4.getText().toString());
                        
                        feelingList.add(f_i4);
                    }
                }
            }
        }



        if (go_to_emergency_if.getText().toString().isEmpty()) {
            doctor_note_data.put("go_to_emergency_if", "");
        } else {
            doctor_note_data.put("go_to_emergency_if", go_to_emergency_if.getText().toString());
        }

        // feelingList is a list of feeling and instruction HashMap.
        doctor_note_data.put("feelings_and_instructions", feelingList);

        Intent intent = new Intent(this, ChangeRoutine.class);
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        // send list of medications.
        intent.putExtra("medicationList", medicationList);
        startActivity(intent);
    }
}
