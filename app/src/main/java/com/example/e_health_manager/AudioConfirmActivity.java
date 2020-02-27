package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;

public class AudioConfirmActivity extends AppCompatActivity {

    String currentAudioLoc;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private String currentSeek;

    private File fileToPlay;
    private MediaPlayer mediaPlayer;


    private ImageView replayBtn;
    private SeekBar seekBar;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_confirm);

        replayBtn = findViewById(R.id.replayBtn);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        currentAudioLoc = intent.getStringExtra("audioLoc");
        Log.d("Audio Confirm Activity", "The location of audio is: "+currentAudioLoc);

        //allow user to replay the audio
        fileToPlay = new File(currentAudioLoc);

        mediaPlayer = null;
    }

    public void onClick_replay(View view) {

        //isPlaying = true
        if(isPlaying){
            pauseAudio();
        }
        //isPlaying = false
        else{
            if(isPaused){
                resumeAudio();
            }
            else{
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

    private void startAudio(File fileToPlay){
        isPlaying = true;
        isPaused = false;
        replayBtn.setImageResource(R.drawable.ic_pause_circle_outline);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IOException e) { e.printStackTrace(); }

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
        seekBarHandler.postDelayed(updateSeekBar,0);
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
        seekBarHandler.postDelayed(updateSeekBar,0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isPlaying){
            stopAudio();
        }
    }

    public void onClick_analyzeAudio(View view) {
        //start analyzing

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
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
