package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AudioConfirmActivity extends AppCompatActivity {

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private String currentSeek;

    private File fileToPlay;
    private MediaPlayer mediaPlayer;


    private ImageView replayBtn;
    private SeekBar seekBar;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private String currentAudioLoc;
    private ArrayList<String> transcriptList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_confirm);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        replayBtn = findViewById(R.id.replayBtn);
        seekBar = findViewById(R.id.seekBar);

        Intent callingActivityIntent = getIntent();
        currentAudioLoc = callingActivityIntent.getStringExtra("audioLoc");
        transcriptList = callingActivityIntent.getStringArrayListExtra("transcriptList");
        Log.d("Audio Confirm Activity", "The location of audio is: " + currentAudioLoc);

        // allow user to replay the audio
        fileToPlay = new File(currentAudioLoc);

        mediaPlayer = null;
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
                startAudio(fileToPlay);
            }
        }
    }

    private void stopAudio() {
        isPlaying = false;
        isPaused = false;
        mediaPlayer.stop();
        replayBtn.setImageResource(R.drawable.ic_replay_primary);
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void startAudio(File fileToPlay) {
        isPlaying = true;
        isPaused = false;
        replayBtn.setImageResource(R.drawable.ic_pause_circle_outline);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
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

    @Override
    protected void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }

    public void onClick_analyzeAudio(View view) {
        //start analyzing

        //store audio into firebase storage
        FirebaseUser user = mAuth.getCurrentUser();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StorageReference filepath = storageRef.child(user.getUid()).child(timeStamp);
        Uri uri = Uri.fromFile(fileToPlay);
        StorageTask uploadTask = filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("upload picture", "*************it has been succeed*****************");
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.d("upload picture", "*************it has been completed*****************");
            }
        });
        while (true) {
            // wait until the uploading is complete.
            if (uploadTask.isComplete()) {
                // Intent intent = new Intent(AudioConfirmActivity.this, ProfileActivity.class);
                Intent intent = new Intent(AudioConfirmActivity.this, transcriptConfirm.class);
                intent.putExtra("transcriptList", transcriptList);

                // intent.setData(uri);
                startActivity(intent);
                break;
            }
        }
    }

    public void onClick_takeAnotherAudio(View view) {
        final Context ctx = this;
        //Dialog to make user confirm
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to take a new audio?(Current one will be overwritten)")
                .setTitle("Alert")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(ctx, TakeAudioActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
