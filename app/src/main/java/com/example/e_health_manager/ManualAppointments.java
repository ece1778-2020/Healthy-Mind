package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.HashMap;

public class ManualAppointments extends AppCompatActivity {

    HashMap<String, Object> doctor_note_data;

    EditText pick_date;
    Calendar calendar;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_appointments);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        pick_date = findViewById(R.id.pick_date);

        pick_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ManualAppointments.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //sets date in EditText
                        String curr_date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        pick_date.setText(curr_date);
                        // make it bold.
                        pick_date.setTypeface(pick_date.getTypeface(), Typeface.BOLD);
                    }
                }, year, month, day);
                //shows DatePickerDialog
                datePickerDialog.show();
            }
        });

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
        Intent intent = new Intent(this, ManualMoreInfo.class);
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        startActivity(intent);
    }
}
