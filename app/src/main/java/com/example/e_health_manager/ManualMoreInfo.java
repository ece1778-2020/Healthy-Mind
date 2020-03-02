package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class ManualMoreInfo extends AppCompatActivity {

    HashMap<String, Object> doctor_note_data;

    ArrayList<HashMap<String, Object>> medicationList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_more_info);

        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
            medicationList = (ArrayList<HashMap<String, Object>>) callingActivityIntent.getSerializableExtra("medicationList");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }
    }

    public void onClick_next_page(View view) {
        Intent intent = new Intent(this, ManualOwnNotes.class);
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        // send list of medications.
        intent.putExtra("medicationList", medicationList);
        startActivity(intent);
    }
}
