package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ManualOwnNotes extends AppCompatActivity {

    HashMap<String, Object> doctor_note_data;

    HashMap<String, Object> appointment;

    ArrayList<HashMap<String, Object>> medicationList = new ArrayList<>();

    EditText ownNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_own_notes);

        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
            appointment = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("appointment");
            medicationList = (ArrayList<HashMap<String, Object>>) callingActivityIntent.getSerializableExtra("medicationList");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }
    }

    public void onClick_next_page(View view) {

        ownNotes = findViewById(R.id.ed10);
        String notes = ownNotes.getText().toString();
        if (notes.isEmpty()) {
            doctor_note_data.put("notes", "");
        } else {
            doctor_note_data.put("notes", notes);
        }

        Intent intent = new Intent(this, ManualConfirm.class);
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        // send list of medications.
        intent.putExtra("medicationList", medicationList);
        intent.putExtra("PARENT_ACTIVITY_REF", "ManualOwnNotes");
        intent.putExtra("appointment", appointment);
        startActivity(intent);
    }
}
