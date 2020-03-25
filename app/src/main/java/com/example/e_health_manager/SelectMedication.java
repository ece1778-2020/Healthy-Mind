package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectMedication extends AppCompatActivity {

    private ArrayList<String> transcriptList = new ArrayList<>();
    private String audio_id;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView, recyclerView2, recyclerView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_medication);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

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

        DataAdapterSelectAppointment dataAdapter3 = new DataAdapterSelectAppointment(SelectMedication.this, transcriptList, audio_id);
        recyclerView3.setAdapter(dataAdapter3);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void onClick_exit(View view) {
        // to profile page.
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
