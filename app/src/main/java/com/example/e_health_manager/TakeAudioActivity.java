package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TakeAudioActivity extends AppCompatActivity {

    private ImageView recordBtn;
    private Chronometer record_timer;

    private boolean isRecording = false;

    private MediaRecorder mediaRecorder;
    private String fileName;
    private String audioLoc;

    private ProgressBar progressBar;
    // icon indicating if there is no voice left (if the current sentence is finished).
    private ImageView hearing;
    private boolean hasVoice = false;

    // Store all the transcription of the sentences.
    private ArrayList<String> transcriptList = new ArrayList<>();

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    private static final String STATE_RESULTS = "results";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    private SpeechService mSpeechService;

    private VoiceRecorder mVoiceRecorder;

    // Initialize a new VoiceRecorder.Callback Object.
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            showStatus(true);
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };

    // Initialize a new ServiceConnection Object to handle Google Service Connection.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };


    // SpeechService.Listener Object, handle storing the results of transcription.
    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        if (mVoiceRecorder != null) {
                            mVoiceRecorder.dismiss();
                        }
                    }
                    // mText is the sentence that is currently transcribing.
                    // text is the transcribed voice at the current state.
                    if (!TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    // Finish one sentence.
                                    // mText.setText(null);

                                    // add to the transcription List only when a sentence is finished.
                                    transcriptList.add(text);

                                    // mAdapter.addResult(text);
                                    // mRecyclerView.smoothScrollToPosition(0);
                                } else if (mVoiceRecorder == null) {
                                    // didn't finish sentence.
                                    transcriptList.add(text);
                                }
                            }
                        });
                    }
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_audio);

        recordBtn = findViewById(R.id.recordBtn);
        record_timer = findViewById(R.id.record_timer);
        progressBar = findViewById(R.id.progress_circular);
        hearing = findViewById(R.id.hearingVoice);
    }

    public void onclick_takeAudio(View view) {

        if (isRecording) {
            // Wait until there is no voice (current sentence is finished).
//            while (true) {
//                if (mVoiceRecorder) {
//                    // Intent intent = new Intent(AudioConfirmActivity.this, TakeAudioActivity.class);
//                    // startActivity(intent);
//                    break;
//                }
//            }

            // it might take some time to stop the voice recorder.
            progressBar.setVisibility(View.VISIBLE);
            // Stop listening to voice
            stopVoiceRecorder();

            // Stop Cloud Speech API
            if (mSpeechService != null) {
                mSpeechService.removeListener(mSpeechServiceListener);
                unbindService(mServiceConnection);
                mSpeechService = null;
            }
            // Stop the audio recorder.
            stopRecording();
            recordBtn.setImageResource(R.mipmap.ic_mic_off_round);
            isRecording = false;
        } else {
            progressBar.setVisibility(View.INVISIBLE);

            // Start the transcription process.
            // Prepare Cloud Speech API
            bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

            // Start the audio recording.
            startRecording();
            recordBtn.setImageResource(R.mipmap.ic_mic_on_round);
            isRecording = true;

            // Start listening to voices
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION);
            }

        }
    }


    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }


    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    public void stopRecording() {
        progressBar.setVisibility(View.VISIBLE);
        record_timer.stop();

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        Toast toast = Toast.makeText(this, "Recording has finished", Toast.LENGTH_SHORT);
        toast.show();

        // Go to confirm page
        Intent intent = new Intent(this, AudioConfirmActivity.class);
        intent.putExtra("audioLoc", audioLoc);
        intent.putExtra("transcriptList", transcriptList);
        startActivity(intent);
    }

    public void startRecording() {

        record_timer.setBase(SystemClock.elapsedRealtime());
        record_timer.start();

        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        fileName = (new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        audioLoc = recordPath + "/" + fileName;

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // the following is slow.
        // mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setOutputFile(audioLoc);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // The following is slow.
        // mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        // mediaRecorder.setAudioSamplingRate(16000);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Recording has started", Toast.LENGTH_SHORT).show();

        mediaRecorder.start();
    }



    private void showStatus(final boolean hearingVoice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // mStatus.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
                // disable Exit button.
                // mExit.setEnabled(!hearingVoice);
                // mComplete.setEnabled(!hearingVoice);
                // Show progress_circular
                hearing.setVisibility(hearingVoice ? View.VISIBLE : View.INVISIBLE);
                hasVoice = hearingVoice;
            }
        });
    }

}
