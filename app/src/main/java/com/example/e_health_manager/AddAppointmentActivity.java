package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddAppointmentActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private BottomNavigationView navbar;
    private EditText nameText;
    private EditText forText;
    private EditText dateText;
    private EditText timeText;
    private EditText locationText;
    private EditText phoneText;

    Calendar calendar;
    int year, month, day;
    String name, reason, date, time, location, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        navbar = findViewById(R.id.bottomNavigation);
        nameText = findViewById(R.id.addAppointName);
        forText = findViewById(R.id.addAppointFor);
        dateText = findViewById(R.id.addAppointDate);
        timeText = findViewById(R.id.addAppointTime);
        locationText = findViewById(R.id.addAppointLoc);
        phoneText = findViewById(R.id.addAppointPhone);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        //set profile button selected
        navbar.setSelectedItemId(R.id.reminder);
        //perform itemselected listener
        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.note_list:
                        startActivity(new Intent(getApplicationContext(), NoteListActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.add:
                        startActivity(new Intent(getApplicationContext(), AddNoteActivity.class));
                        overridePendingTransition(0,0);
                        return true;


                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        //set date picker
        dateText.setShowSoftInputOnFocus(false);
        dateText.setInputType(InputType.TYPE_NULL);
        dateText.setFocusable(false);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddAppointmentActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //sets date in EditText
                        String curr_date;
                        if (month + 1 < 10) {
                            curr_date = year + "/" + "0" + (month + 1) + "/" + dayOfMonth;
                        } else {
                            curr_date = year + "/" + (month + 1) + "/" + dayOfMonth;
                        }

                        dateText.setText(curr_date);
                        // make it bold.
                    }
                }, year, month, day);
                //shows DatePickerDialog
                datePickerDialog.show();
            }
        });

        //set time picker
        timeText.setShowSoftInputOnFocus(false);
        timeText.setInputType(InputType.TYPE_NULL);
        timeText.setFocusable(false);
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddAppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedMinute<10){
                            timeText.setText( selectedHour + ":0" + selectedMinute);
                        }
                        else{
                            timeText.setText( selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time(24-hour Clock)");
                mTimePicker.show();
            }
        });
    }

    public void onClick_addAppointment(View view){
        name = nameText.getText().toString();
        reason = forText.getText().toString();
        date = dateText.getText().toString();
        time = timeText.getText().toString();
        location = locationText.getText().toString();
        phone = phoneText.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to set a notification one day and 2 hours before the appointment time?")
                .setTitle("Set Appointment")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        storeAppointment(name, reason, date, time, location, phone);
                        //set notification
                        setupNotificationChannel();
                        setupNotificationDay(date, time);
                        setupNotificationHour(date, time);
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        storeAppointment(name, reason, date, time, location, phone);
                    }})
                .show();
    }

    private void storeAppointment(String name, String reason, String date, String time, String location, String phone){
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("user_id", mAuth.getCurrentUser().getUid());
        appointment.put("date", date);
        appointment.put("doctor", name);
        appointment.put("location", location);
        appointment.put("phone", phone);
        appointment.put("reason", reason);
        appointment.put("time", time);
        mFirestore.collection("appointments").add(appointment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(AddAppointmentActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void setupNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "AppointmentReminderChannel";
            String description = "Channel for appointment reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("notifyAppointment", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupNotificationDay(String date, String time){
        int year, month, day, hour, minute;
        year = Integer.parseInt(date.split("/")[0]);
        month = Integer.parseInt(date.split("/")[1]);;
        day = Integer.parseInt(date.split("/")[2]);;
        hour = Integer.parseInt(time.split(":")[0]);;
        minute = Integer.parseInt(time.split(":")[1]);;

        Calendar myAlarmDate = Calendar.getInstance();
        myAlarmDate.setTimeInMillis(System.currentTimeMillis());
        myAlarmDate.set(year, month, day-1, hour, minute, 0);

        Intent intent = new Intent(AddAppointmentActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddAppointmentActivity.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonClick = System.currentTimeMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), pendingIntent);

    }

    private void setupNotificationHour(String date, String time){
        int year, month, day, hour, minute;
        year = Integer.parseInt(date.split("/")[0]);
        month = Integer.parseInt(date.split("/")[1]);;
        day = Integer.parseInt(date.split("/")[2]);;
        hour = Integer.parseInt(time.split(":")[0]);;
        minute = Integer.parseInt(time.split(":")[1]);;

        Calendar myAlarmDate = Calendar.getInstance();
        myAlarmDate.setTimeInMillis(System.currentTimeMillis());
        myAlarmDate.set(year, month, day, hour-2, minute, 0);

        Intent intent = new Intent(AddAppointmentActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddAppointmentActivity.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonClick = System.currentTimeMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), pendingIntent);

    }
}
