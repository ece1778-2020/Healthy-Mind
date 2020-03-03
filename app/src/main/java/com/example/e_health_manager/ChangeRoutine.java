package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangeRoutine extends AppCompatActivity {

    ArrayList routine_changes = new ArrayList();
    ArrayList medicationList = new ArrayList();

    HashMap<String, Object> doctor_note_data;

    EditText activity1, instruction1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_routine);

        activity1 = findViewById(R.id.input1);
        instruction1 = findViewById(R.id.input2);

        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
            medicationList = callingActivityIntent.getStringArrayListExtra("medicationList");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }
    }


    public void onClick_next_page(View view) {
        HashMap<String, String> routine_change = new HashMap<>();
        routine_change.put("activity", activity1.getText().toString());
        routine_change.put("instruction", instruction1.getText().toString());
        routine_changes.add(routine_change);
        doctor_note_data.put("routine_changes", routine_changes);
        Intent intent = new Intent(this, ManualAppointments.class);
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        // send list of medications.
        intent.putExtra("medicationList", medicationList);
        startActivity(intent);
    }
}
