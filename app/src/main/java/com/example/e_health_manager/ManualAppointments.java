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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ManualAppointments extends AppCompatActivity {

    HashMap<String, Object> doctor_note_data;

    HashMap<String, Object> appointment = new HashMap<>();

    ArrayList<HashMap<String, Object>> medicationList = new ArrayList();

    EditText pick_date;
    Calendar calendar;
    int year, month, day;

    EditText doc_name, for1, app_time, location, phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_appointments);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        pick_date = findViewById(R.id.pick_date);

        doc_name = findViewById(R.id.ed1);
        for1 = findViewById(R.id.ed2);
        app_time = findViewById(R.id.pick_time);
        location = findViewById(R.id.ed3);
        phone = findViewById(R.id.ed4);


        pick_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ManualAppointments.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //sets date in EditText
                        String curr_date;
                        if (month + 1 < 10) {
                            curr_date = year + "/" + "0" + (month + 1) + "/" + dayOfMonth;
                        } else {
                            curr_date = year + "/" + (month + 1) + "/" + dayOfMonth;
                        }

                        pick_date.setText(curr_date);
                        // make it bold.
                        // pick_date.setTypeface(pick_date.getTypeface(), Typeface.BOLD);
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
            medicationList = (ArrayList<HashMap<String, Object>>) callingActivityIntent.getSerializableExtra("medicationList");
        } else {
            Log.w("ManualMedicationError", "callingActivityIntent is empty");
        }
    }

    public void onClick_next_page(View view) {
        Intent intent = new Intent(this, ManualMoreInfo.class);
        intent.putExtra("curr_doctor_note_data", doctor_note_data);
        // send list of medications.
        intent.putExtra("medicationList", medicationList);

        appointment.put("doctor", doc_name.getText().toString());
        appointment.put("date", pick_date.getText().toString());
        appointment.put("location", location.getText().toString());
        appointment.put("phone", phone.getText().toString());
        appointment.put("reason", for1.getText().toString());
        appointment.put("time", app_time.getText().toString());
        appointment.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

        intent.putExtra("appointment", appointment);
        startActivity(intent);
    }
}
