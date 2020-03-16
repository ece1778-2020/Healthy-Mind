package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.DescriptorProtos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppointmentDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private String appointID, appointAddress = null;

    private MapView mapView;
    private TextView doctorText, reasonText, dateText, timeText, locationText, phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        //get current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

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
    }

    private void fetchLastLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment)
                            getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(AppointmentDetailActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //set up appointment info
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
                            appointAddress = document.getString("location");
                            //setup google map
                            //show current location
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(markerOptions);
                            //show apppointment location
                            LatLng appointLatLng = getLatLngFromAddress(appointAddress);
                            Log.d("google map", "onComplete: the lat and long of appointment location is: "+appointLatLng.toString());
                            MarkerOptions appointMarker = new MarkerOptions().position(appointLatLng).title("Appointment");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(appointLatLng));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(appointLatLng, 15));
                            googleMap.addMarker(appointMarker);
                        }
                    }
                });
    }

    private LatLng getLatLngFromAddress(String inputAddress){
        Geocoder coder = new Geocoder(AppointmentDetailActivity.this);
        List<Address> address;
        LatLng resLatLng = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(inputAddress, 5);
            if (address == null) {
                return null;
            }

            if (address.size() == 0) {
                return null;
            }

            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            resLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
            Toast.makeText(AppointmentDetailActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        return resLatLng;
    }

}
