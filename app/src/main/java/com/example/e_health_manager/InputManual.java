package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.reflect.Type;
import java.util.Calendar;

public class InputManual extends AppCompatActivity {

    String doctor_note_id;
    EditText pick_date_came, pick_date_left;
    Calendar calendar;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_manual);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        pick_date_came = findViewById(R.id.pick_date_came);
        pick_date_left = findViewById(R.id.pick_date_left);


        pick_date_came.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(InputManual.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //sets date in EditText
                        String curr_date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        pick_date_came.setText(curr_date);
                        // make it bold.
                        pick_date_came.setTypeface(pick_date_came.getTypeface(), Typeface.BOLD);
                    }
                }, year, month, day);
                //shows DatePickerDialog
                datePickerDialog.show();
            }
        });


        pick_date_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(InputManual.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //sets date in EditText
                        String curr_date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        pick_date_left.setText(curr_date);
                        // make it bold.
                        pick_date_left.setTypeface(pick_date_left.getTypeface(), Typeface.BOLD);
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
            doctor_note_id = callingActivityIntent.getStringExtra("curr_doctor_note_id");
        }

    }




    public void onClick_next_page(View view) {
        Intent intent = new Intent(this, ManualMedication.class);
        intent.putExtra("curr_doctor_note_id", doctor_note_id);
        startActivity(intent);
    }

}
