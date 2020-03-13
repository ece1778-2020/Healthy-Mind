package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakeAudioActivity extends AppCompatActivity {

    private ImageView recordBtn;
    private Chronometer record_timer;

    private boolean isRecording = false;

    private MediaRecorder mediaRecorder;
    private String fileName;
    String audioLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_audio);

        recordBtn = findViewById(R.id.recordBtn);
        record_timer = findViewById(R.id.record_timer);
    }

    public void onclick_takeAudio(View view) {

        if(isRecording){
            stopRecording();
            recordBtn.setImageResource(R.mipmap.ic_mic_off_round);
            isRecording = false;
        }
        else{
            startRecording();
            recordBtn.setImageResource(R.mipmap.ic_mic_on_round);
            isRecording = true;
        }
    }

    public void stopRecording(){
        record_timer.stop();

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        Toast toast = Toast.makeText(this, "Recording has finished", Toast.LENGTH_SHORT );
        toast.show();

        //go to confirm page
        Intent intent = new Intent(this, AudioConfirmActivity.class);
        intent.putExtra("audioLoc", audioLoc);
        startActivity(intent);
    }

    public void startRecording(){
        record_timer.setBase(SystemClock.elapsedRealtime());
        record_timer.start();

        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        fileName = (new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        audioLoc = recordPath+"/"+fileName;

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setOutputFile(audioLoc);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try{
            mediaRecorder.prepare();
        } catch (IOException e){
            e.printStackTrace();
        }

        Toast toast = Toast.makeText(this, "Recording has started", Toast.LENGTH_SHORT );
        toast.show();

        mediaRecorder.start();
    }

}
