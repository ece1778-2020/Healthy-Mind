package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class AudioReplay extends AppCompatActivity {

    private boolean isPlaying = false;
    private boolean isPaused = false;

    private ImageView replayBtn;
    private SeekBar seekBar;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;

    private MediaPlayer mediaPlayer;
    private String currentAudioLoc;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    // doctor's note id.
    private String noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_replay);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        replayBtn = findViewById(R.id.replayBtn);
        seekBar = findViewById(R.id.seekBar);


        // from DoctorNoteDetailActivity
        Intent callingActivityIntent = getIntent();
        currentAudioLoc = callingActivityIntent.getStringExtra("audioLoc");
        // noteID = callingActivityIntent.getStringExtra("noteID");

        mediaPlayer = null;
    }

    public void onClick_exit(View view) {
        stopAudio();
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void onClick_replay(View view) {
        //isPlaying = true
        if (isPlaying) {
            pauseAudio();
        }
        //isPlaying = false
        else {
            if (isPaused) {
                resumeAudio();
            } else {
                startAudio(currentAudioLoc);
            }
        }
    }

    private void pauseAudio() {
        isPlaying = false;
        isPaused = true;
        replayBtn.setImageResource(R.drawable.ic_play_arrow);
        mediaPlayer.pause();
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void resumeAudio() {
        isPlaying = true;
        isPaused = false;
        replayBtn.setImageResource(R.drawable.ic_pause_circle_outline);
        mediaPlayer.start();

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this, 100);
            }
        };
        seekBarHandler.postDelayed(updateSeekBar, 0);
    }

    private void stopAudio() {
        isPlaying = false;
        isPaused = false;
        mediaPlayer.stop();
        replayBtn.setImageResource(R.drawable.ic_replay_primary);
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void startAudio(String currentAudioLoc) {
        isPlaying = true;
        isPaused = false;
        replayBtn.setImageResource(R.drawable.ic_pause_circle_outline);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(currentAudioLoc);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

        //set up seek bar
        seekBar.setMax(mediaPlayer.getDuration());
        seekBarHandler = new Handler();
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this, 100);
            }
        };
        seekBarHandler.postDelayed(updateSeekBar, 0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });
    }
}
