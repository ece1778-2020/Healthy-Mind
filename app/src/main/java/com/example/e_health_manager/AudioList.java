package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AudioList extends AppCompatActivity {

    private ArrayList<String> transcriptList = new ArrayList<>();
    private ArrayList<String> audioPathList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);

        db.collection("audio")
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ArrayList<String> lst = (ArrayList<String>) document.get("transcript_text");

                                String display_text = "";
                                for (String s : lst) {
                                    display_text = display_text.concat(s);
                                    display_text = display_text + ".";
                                    display_text = display_text + System.lineSeparator();
                                }

                                transcriptList.add(display_text);
                                audioPathList.add(document.get("audio_path").toString());
                            }

                            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                            DataAdapterAudio dataAdapterAudio = new DataAdapterAudio(AudioList.this, transcriptList, audioPathList);
                            recyclerView.setAdapter(dataAdapterAudio);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        }
                    }
                });
    }

    public void onClick_exit(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
