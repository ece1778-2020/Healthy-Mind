package com.example.e_health_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;

public class ManualConfirm extends AppCompatActivity {

    String doctor_note_id;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userID;

    TextView m1, m2, m3, m4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_confirm);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        // Current user.
        userID = mAuth.getCurrentUser().getUid();

        m1 = findViewById(R.id.textView11);
        m2 = findViewById(R.id.textView12);
        m3 = findViewById(R.id.textView13);
        m4 = findViewById(R.id.textView14);


        // handle the intent called from the previous page.
        // callingActivityIntent is from the previous page.
        Intent callingActivityIntent = getIntent();

        if (callingActivityIntent != null) {
            doctor_note_id = callingActivityIntent.getStringExtra("curr_doctor_note_id");
        }

        db.collection("doctor's note")
                .document(doctor_note_id)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        ArrayList<HashMap<String, Object>> medications = (ArrayList<HashMap<String, Object>>) documentSnapshot.get("medications");
                        HashMap<String, Object> m1_item = medications.get(0);
                        String m1_text = "take: " + m1_item.get("name").toString() + "for: " + m1_item.get("reason").toString() + "at time: " + m1_item.get("time").toString();
                        m1.setText(m1_text);

                        HashMap<String, Object> m2_item = medications.get(1);
                        String m2_text = "take: " + m2_item.get("name").toString() + "for: " + m2_item.get("reason").toString() + "at time: " + m2_item.get("time").toString();
                        m2.setText(m2_text);
                    }
                });


    }

    public void onClick_submit(View view) {

    }
}
