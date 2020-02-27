package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

public class ManualFeel extends AppCompatActivity {

    HashMap<String, String> feelings_and_instructions = new HashMap<>();

    HashMap<String, Object> doctor_note_data;

    EditText my_feeling1, instruction1, go_to_emergency_if;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_feel);

        my_feeling1 = findViewById(R.id.feel1_input);
        instruction1 = findViewById(R.id.feel2_input);
        go_to_emergency_if = findViewById(R.id.feel3_input);

        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            doctor_note_data = (HashMap<String, Object>) callingActivityIntent.getSerializableExtra("curr_doctor_note_data");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }



    }


    public void onClick_next_page(View view) {
        feelings_and_instructions.put("My Feeling", "What to do");
        feelings_and_instructions.put(my_feeling1.getText().toString(), instruction1.getText().toString());
        doctor_note_data.put("feelings_and_instructions", feelings_and_instructions);
        doctor_note_data.put("go_to_emergency_if", go_to_emergency_if.getText().toString());

        Intent intent = new Intent(this, ChangeRoutine.class);
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        startActivity(intent);
    }
}
