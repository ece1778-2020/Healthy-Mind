package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class AudioReplay extends AppCompatActivity {

    private File fileToPlay;
    private MediaPlayer mediaPlayer;
    private String currentAudioLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_replay);


        // from DoctorNoteDetailActivity
        Intent callingActivityIntent = getIntent();
        currentAudioLoc = callingActivityIntent.getStringExtra("audioLoc");

        // allow user to replay the audio
        fileToPlay = new File(currentAudioLoc);

        mediaPlayer = null;
    }

    public void onClick_exit(View view) {
    }

    public void onClick_replay(View view) {
    }
}
