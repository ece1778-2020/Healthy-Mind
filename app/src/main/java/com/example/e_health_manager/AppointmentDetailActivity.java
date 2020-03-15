package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class AppointmentDetailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private String appointID;

    private MapView mapView;
    private TextView doctorText, reasonText, dateText, timeText, locationText, phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        mapView = findViewById(R.id.mapView);
        doctorText = findViewById(R.id.appointDetailDoc);
        reasonText = findViewById(R.id.appointDetailReason);
        dateText = findViewById(R.id.appointDetailDate);
        timeText = findViewById(R.id.appointDetailTime);
        locationText = findViewById(R.id.appointDetailLoc);
        phoneText = findViewById(R.id.appointDetailPhone);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        appointID = intent.getStringExtra("appointID");
        Log.d("testing", "onCreate: the id of this appointment is: "+appointID);

        mFirestore.collection("appointments")
                .document(appointID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            doctorText.setText(document.getString("doctor"));
                            reasonText.setText(document.getString("reason"));
                            dateText.setText(document.getString("date"));
                            timeText.setText(document.getString("time"));
                            locationText.setText(document.getString("location"));
                            phoneText.setText(document.getString("phone"));
                            //set up mapview
                            
                        }
                    }
                });
    }
}
