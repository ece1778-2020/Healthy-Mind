package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private ProgressBar progressBar;
    private BottomNavigationView navbar;

    private ExpandableListView noteList;
    private ExpandableNoteListAdapter noteListAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<HashMap>> listHashMap;

    List<HashMap> noteChildren = new ArrayList<>();
    List<HashMap> mediChildren = new ArrayList<>();
    List<HashMap> appointChildren = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.noteListProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        navbar = findViewById(R.id.bottomNavigation);
        navbar.setSelectedItemId(R.id.note_list);
        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.add:
                        startActivity(new Intent(getApplicationContext(), AddNoteActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.reminder:
                        startActivity(new Intent(getApplicationContext(), AddAppointmentActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        noteList = findViewById(R.id.noteList);
        initializeData();

        noteList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                // Doctor's notes section
                if (groupPosition == 0) {

                    String timestamp = ((HashMap) noteListAdapter.getChild(groupPosition, childPosition)).get("timestamp").toString();
                    if (!timestamp.equals("You don't have any doctor's notes")) {
                        // if there is at least one doctor's note.
                        String noteID = ((HashMap) noteListAdapter.getChild(groupPosition, childPosition)).get("noteID").toString();
                        Intent intent = new Intent(NoteListActivity.this, DoctorNoteDetailActivity.class);
                        intent.putExtra("noteID", noteID);
                        startActivity(intent);
                    }
                }

                // Appointments section.
                if (groupPosition == 2) {

                    String see = ((HashMap) noteListAdapter.getChild(groupPosition, childPosition)).get("see").toString();
                    if (!see.equals("You don't have any appointments")) {
                        // if there is at least one appointment.
                        String appointID = ((HashMap) noteListAdapter.getChild(groupPosition, childPosition)).get("appointID").toString();
                        Intent intent = new Intent(NoteListActivity.this, AppointmentDetailActivity.class);
                        intent.putExtra("appointID", appointID);
                        startActivity(intent);
                    }

                }
                return true;
            }
        });
    }

    private void initializeData() {
        listDataHeader = new ArrayList<>();
        listHashMap = new HashMap<>();
        listDataHeader.add("Doctor's Notes");
        listDataHeader.add("Medications");
        listDataHeader.add("Appointments");

        initializeNote();
    }

    private void initializeNote() {
        mFirestore.collection("doctor's note")
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.getResult().size() != 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    HashMap<String, Object> itemHashMap = new HashMap<>();
                                    itemHashMap.put("timestamp", document.get("timestamp"));
                                    itemHashMap.put("comeDate", document.get("came_date"));
                                    itemHashMap.put("leaveDate", document.get("left_date"));
                                    itemHashMap.put("reason", document.get("reason_for_hospital"));
                                    itemHashMap.put("noteID", document.getId());
                                    noteChildren.add(itemHashMap);
                                }
                                listHashMap.put(listDataHeader.get(0), noteChildren);
                                initializeMedication();
                            } else {
                                HashMap<String, Object> itemHashMap = new HashMap<>();
                                itemHashMap.put("timestamp", "You don't have any doctor's notes");
                                itemHashMap.put("comeDate", "");
                                itemHashMap.put("leaveDate", "");
                                itemHashMap.put("reason", "");
                                noteChildren.add(itemHashMap);
                                listHashMap.put(listDataHeader.get(0), noteChildren);
                                initializeMedication();
                            }
                        }
                    }
                });
    }

    private void initializeMedication() {
        mFirestore.collection("medications")
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.getResult().size() != 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    HashMap<String, Object> itemHashMap = new HashMap<>();
                                    itemHashMap.put("name", document.get("name"));
                                    itemHashMap.put("for", document.get("reason"));
                                    itemHashMap.put("dose", document.get("dose"));
                                    if (((ArrayList<String>) document.get("time")).size() != 0) {
                                        String dosetime = "";
                                        for (String element : (ArrayList<String>) document.get("time")) {
                                            element = element.toLowerCase();
                                            element = element.substring(0, 1).toUpperCase() + element.substring(1);
                                            dosetime = dosetime + element + ", ";
                                        }
                                        dosetime = dosetime.substring(0, dosetime.length() - 2);
                                        itemHashMap.put("time", dosetime);
                                    } else {
                                        itemHashMap.put("time", "Take it as needed");
                                    }
                                    mediChildren.add(itemHashMap);
                                }
                                listHashMap.put(listDataHeader.get(1), mediChildren);
                                initializeAppointment();
                            } else {
                                HashMap<String, Object> itemHashMap = new HashMap<>();
                                itemHashMap.put("name", "You don't have any medications");
                                itemHashMap.put("for", "");
                                itemHashMap.put("dose", "");
                                itemHashMap.put("time", "");
                                mediChildren.add(itemHashMap);
                                listHashMap.put(listDataHeader.get(1), mediChildren);
                                initializeAppointment();
                            }
                        }
                    }
                });
    }

    private void initializeAppointment() {
        mFirestore.collection("appointments")
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.getResult().size() != 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    HashMap<String, Object> itemHashMap = new HashMap<>();
                                    itemHashMap.put("see", document.get("doctor"));
                                    itemHashMap.put("date", document.get("date"));
                                    itemHashMap.put("time", document.get("time"));
                                    itemHashMap.put("location", document.get("location"));
                                    itemHashMap.put("appointID", document.getId());
                                    appointChildren.add(itemHashMap);
                                }
                                listHashMap.put(listDataHeader.get(2), appointChildren);
                                progressBar.setVisibility(View.INVISIBLE);
                                noteListAdapter = new ExpandableNoteListAdapter(NoteListActivity.this, listDataHeader, listHashMap);
                                noteList.setAdapter(noteListAdapter);
                            } else {
                                HashMap<String, Object> itemHashMap = new HashMap<>();
                                itemHashMap.put("see", "You don't have any appointments");
                                itemHashMap.put("date", "");
                                itemHashMap.put("time", "");
                                itemHashMap.put("location", "");
                                appointChildren.add(itemHashMap);
                                listHashMap.put(listDataHeader.get(2), appointChildren);
                                progressBar.setVisibility(View.INVISIBLE);
                                noteListAdapter = new ExpandableNoteListAdapter(NoteListActivity.this, listDataHeader, listHashMap);
                                noteList.setAdapter(noteListAdapter);
                            }
                        }
                    }
                });
    }
}
