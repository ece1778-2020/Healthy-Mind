package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SpeechToTextActivity extends AppCompatActivity {

    private TextView audioText;
    private ImageView recordBtn;

    private SpeechRecognizer mSpeechRecognizer;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);

        audioText = findViewById(R.id.text1);
        recordBtn = findViewById(R.id.recordBtn);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {
            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    audioText.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });

//        recordBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_UP:
//                        mSpeechRecognizer.stopListening();
//                        audioText.setHint("You will see input here");
//                        break;
//
//                    case MotionEvent.ACTION_DOWN:
//                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
//
//                        audioText.setText("Listening...");
//                        break;
//                }
//                return false;
//            }
//        });
    }

    public void onClick_startSpeechToText(View view) {

        if (isRecording) {
            mSpeechRecognizer.stopListening();
            recordBtn.setImageResource(R.mipmap.ic_mic_off_round);
            isRecording = false;
        } else {
            Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            recordBtn.setImageResource(R.mipmap.ic_mic_on_round);
            isRecording = true;
        }

    }

}






