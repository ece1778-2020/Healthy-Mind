package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SelectMedication extends AppCompatActivity {

    private ArrayList<String> transcriptList = new ArrayList<>();
    ArrayList<Integer> selectedTimes;
    private String audio_id;
    private String appointment_id = "";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView, recyclerView2, recyclerView3;

    EditText doctor_name, appointment_date, appointment_time, reason_ed, where, phone_ed;

    Calendar calendar;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_medication);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView3 = findViewById(R.id.recyclerView3);

        Intent callingActivityIntent = getIntent();
        transcriptList = callingActivityIntent.getStringArrayListExtra("transcriptList");
        audio_id = callingActivityIntent.getStringExtra("audio_id");

        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);

        DataAdapterSelectMedication dataAdapter = new DataAdapterSelectMedication(SelectMedication.this, transcriptList, audio_id);
        recyclerView.setAdapter(dataAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DataAdapterSelectMedicationDose dataAdapter2 = new DataAdapterSelectMedicationDose(SelectMedication.this, transcriptList, audio_id);
        recyclerView2.setAdapter(dataAdapter2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

//        DataAdapterSelectAppointment dataAdapter3 = new DataAdapterSelectAppointment(SelectMedication.this, transcriptList, audio_id);
//        recyclerView3.setAdapter(dataAdapter3);
//        recyclerView3.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void onClick_exit(View view) {

        mFirestore.collection("audio").document(audio_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        final String audio_path = documentSnapshot.get("audio_path").toString();
                        final ArrayList<String> transcript_text = (ArrayList<String>) documentSnapshot.get("transcript_text");

                        String name = "";
                        if (documentSnapshot.get("medication_name") != null) {
                            name = documentSnapshot.get("medication_name").toString();
                        }

                        String dose = "";
                        if (documentSnapshot.get("dose") != null) {
                            dose = documentSnapshot.get("dose").toString();
                        }

//                        if (name.isEmpty() && dose.isEmpty()) {
//                            // user does not specify the medication name and dose.
//                            // to profile page.
//                            Intent intent = new Intent(SelectMedication.this, ProfileActivity.class);
//                            startActivity(intent);
//                        } else {
//
//                        }

                        // else pop up a dialog to confirm medication.
                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectMedication.this);
                        // Get the layout inflater
                        LayoutInflater inflater = LayoutInflater.from(SelectMedication.this);
                        View my_view = inflater.inflate(R.layout.dialog_edit_medications, null);

                        final EditText medication_name = my_view.findViewById(R.id.medication_name);
                        final EditText m_dose = my_view.findViewById(R.id.dose);
                        final EditText medication_reason = my_view.findViewById(R.id.medication_reason);

                        if (!name.isEmpty()) {
                            medication_name.setText(name);
                        }

                        if (!dose.isEmpty()) {
                            m_dose.setText(dose);
                        }

                        builder.setView(my_view);
                        builder.setTitle("Confirm the medication information");

                        selectedTimes = new ArrayList<>();

                        builder.setMultiChoiceItems(R.array.time_array, null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        if (isChecked) {
                                            // If the user checked the item, add it to the selected items
                                            selectedTimes.add(which);
                                        } else if (selectedTimes.contains(which)) {
                                            // Else, if the item is already in the array, remove it
                                            selectedTimes.remove(Integer.valueOf(which));
                                        }
                                    }
                                });

                        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                Map<String, Object> medication = new HashMap<>();

                                if (m_dose.getText().toString().isEmpty()) {
                                    medication.put("dose", "Take as needed.");
                                } else {
                                    medication.put("dose", m_dose.getText().toString());
                                }

                                medication.put("name", medication_name.getText().toString());
                                medication.put("reason", medication_reason.getText().toString());
                                medication.put("user_id", mAuth.getCurrentUser().getUid());
                                //get time
                                ArrayList<String> timeList = new ArrayList<>();
                                if (selectedTimes.contains(0)) {
                                    timeList.add("Morning");
                                }
                                if (selectedTimes.contains(1)) {
                                    timeList.add("Noon");
                                }
                                if (selectedTimes.contains(2)) {
                                    timeList.add("Afternoon");
                                }
                                if (selectedTimes.contains(3)) {
                                    timeList.add("Night");
                                }

                                medication.put("time", timeList);

                                DocumentReference medicationRef = mFirestore.collection("medications").document();
                                medicationRef.set(medication);
                                String medicationIDs = medicationRef.getId();

                                final String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                                // store doctor note
                                Map<String, Object> doctorNote = new HashMap<>();

                                Toast.makeText(getApplicationContext(), appointment_id, Toast.LENGTH_SHORT).show();

                                doctorNote.put("appointment_id", appointment_id);
                                doctorNote.put("came_date", "");
                                doctorNote.put("go_to_emergency_if", "");

                                doctorNote.put("hasAudio", true);
                                doctorNote.put("audio_path", audio_path);
                                doctorNote.put("transcript_text", transcript_text);


                                doctorNote.put("left_date", "");
                                ArrayList<String> lst = new ArrayList<>();
                                lst.add(medicationIDs);
                                doctorNote.put("medication_ids", lst);
                                doctorNote.put("notes", "");
                                doctorNote.put("reason_for_hospital", "");
                                doctorNote.put("timestamp", timestamp);
                                doctorNote.put("user_id", mAuth.getCurrentUser().getUid());

                                // get feelings
                                ArrayList<Map<String, Object>> feelings = new ArrayList<>();
                                doctorNote.put("feelings_and_instructions", feelings);
                                // get more info
                                ArrayList<Map<String, Object>> moreInfo = new ArrayList<>();
                                doctorNote.put("more_info", moreInfo);
                                //get routines
                                ArrayList<Map<String, Object>> routines = new ArrayList<>();
                                doctorNote.put("routine_changes", routines);

                                // update doctor's note.
                                mFirestore.collection("doctor's note").add(doctorNote);

                                // to profile page.
                                Intent intent = new Intent(SelectMedication.this, ProfileActivity.class);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton(android.R.string.no, null);
                        builder.show();

                    }
                });

    }

    public void onClick_setAppointment(View view) {
        String date_keyword = "";
        String doctor_keyword = "";
        String location_keyword = "";
        String phone_keyword = "";
        String time_keyword = "";

        String[] month_array = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        for (String sentence : transcriptList) {
            String[] words = sentence.split("\\s+");
            int count = 0;
            for (String w : words) {
                if (TextUtils.isDigitsOnly(w) && w.length() > 5) {
                    phone_keyword = phone_keyword + " " + w;
                }

                if (Arrays.asList(month_array).contains(w)) {
                    if (words.length > (count + 1)) {
                        date_keyword = date_keyword + w + " " + words[count + 1] + " (Please click here to reformat the date).";
                    }
                }

                if (w.toLowerCase().equals("am") || w.toLowerCase().equals("pm") || w.toLowerCase().equals("a.m.") || w.toLowerCase().equals("p.m.")) {
                    if (count - 1 >= 0) {
                        time_keyword = time_keyword + words[count - 1] + w + " (Please click here to reformat the time).";
                    }
                }

                if (w.toLowerCase().equals("street") || w.toLowerCase().equals("avenue")) {
                    if (count - 2 >= 0) {
                        location_keyword = location_keyword + words[count - 2] + " " + words[count - 1] + " " + w;
                    } else if (count - 1 >= 0) {
                        location_keyword = location_keyword + words[count - 1] + " " + w;
                    }
                }

                if (w.toLowerCase().equals("dr") || w.toLowerCase().equals("doctor") || w.toLowerCase().equals("dr.")) {
                    if (words.length > (count + 1)) {
                        if (!words[count + 1].toLowerCase().equals("ok")) {
                            doctor_keyword = doctor_keyword + words[count + 1];
                        }
                    }
                }
                count += 1;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(SelectMedication.this);
        // Get the layout inflater
        LayoutInflater inflater = SelectMedication.this.getLayoutInflater();
        View view_edit = inflater.inflate(R.layout.dialog_edit_appointment, null);
        doctor_name = view_edit.findViewById(R.id.doctor_name);
        doctor_name.setText(doctor_keyword);
        appointment_date = view_edit.findViewById(R.id.appointment_date);
        appointment_date.setText(date_keyword);
        appointment_time = view_edit.findViewById(R.id.appointment_time);
        appointment_time.setText(time_keyword);
        reason_ed = view_edit.findViewById(R.id.reason_ed);

        where = view_edit.findViewById(R.id.where);
        where.setText(location_keyword);
        phone_ed = view_edit.findViewById(R.id.phone);
        phone_ed.setText(phone_keyword);
        builder.setView(view_edit);
        builder.setTitle("Edit appointment information");

        appointment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SelectMedication.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //sets date in EditText
                        String curr_date;
                        if (month + 1 < 10) {
                            curr_date = year + "/" + "0" + (month + 1) + "/" + dayOfMonth;
                        } else {
                            curr_date = year + "/" + (month + 1) + "/" + dayOfMonth;
                        }

                        appointment_date.setText(curr_date);
                        // make it bold.
                        appointment_date.setTypeface(appointment_date.getTypeface(), Typeface.BOLD);
                    }
                }, year, month, day);
                //shows DatePickerDialog
                datePickerDialog.show();
            }
        });


        // set time picker
        appointment_time.setShowSoftInputOnFocus(false);
        appointment_time.setInputType(InputType.TYPE_NULL);
        appointment_time.setFocusable(false);
        appointment_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SelectMedication.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedMinute<10){
                            appointment_time.setText( selectedHour + ":0" + selectedMinute);
                        }
                        else{
                            appointment_time.setText( selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);// Yes 24 hour time
                mTimePicker.setTitle("Select Time(24-hour Clock)");
                mTimePicker.show();
            }
        });


        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Map<String, Object> appointment_data = new HashMap<>();

                appointment_data.put("date", appointment_date.getText().toString());
                appointment_data.put("doctor", doctor_name.getText().toString());
                appointment_data.put("location", where.getText().toString());
                appointment_data.put("phone", phone_ed.getText().toString());

                if (reason_ed.getText().toString().isEmpty()) {
                    appointment_data.put("reason", "");
                } else {
                    appointment_data.put("reason", reason_ed.getText().toString());
                }

                appointment_data.put("time", appointment_time.getText().toString());
                appointment_data.put("user_id", mAuth.getCurrentUser().getUid());

                DocumentReference appointmentRef = mFirestore.collection("appointments").document();
                appointmentRef.set(appointment_data);
                appointment_id = appointmentRef.getId();
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();

    }

//    public void onClick_submitAudio(View view) {
//        // to profile page, only submitting the audio and transcript.
//        Intent intent = new Intent(this, ProfileActivity.class);
//        startActivity(intent);
//    }
}
