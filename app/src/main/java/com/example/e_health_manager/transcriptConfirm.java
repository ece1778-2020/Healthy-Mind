package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class transcriptConfirm extends AppCompatActivity {

    private static final String STATE_RESULTS = "results";

    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<String> transcriptList = new ArrayList<>();

    private SpeechService mSpeechService;
    private DataAdapterTranscript dataAdapter;


//    // Initialize a new ServiceConnection Object to handle Google Service Connection.
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder binder) {
//            mSpeechService = SpeechService.from(binder);
//            mSpeechService.addListener(mSpeechServiceListener);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mSpeechService = null;
//        }
//
//    };
//
//    // SpeechService.Listener Object, handle storing the results of transcription.
//    private final SpeechService.Listener mSpeechServiceListener =
//            new SpeechService.Listener() {
//                @Override
//                public void onSpeechRecognized(final String text, final boolean isFinal) {
//                    if (!TextUtils.isEmpty(text)) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (isFinal) {
//                                    // add to the transcription List only when a sentence is finished.
//                                    transcriptList.add(text);
//                                    dataAdapter.addResult(text);
//
//                                }
//                            }
//                        });
//                    }
//                }
//            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript_confirm);

        mRecyclerView = findViewById(R.id.recyclerView);

        // Prepare Cloud Speech API
        // bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

        Intent callingActivityIntent = getIntent();
        // the following line is from the audio transcript at the same time. (from AudioConfirmActivity).
        transcriptList = callingActivityIntent.getStringArrayListExtra("transcriptList");

//        // firebase storage uri.
//        String storageUri = callingActivityIntent.getStringExtra("storageUri");
//        // experiment 1
//        Log.d("uri", storageUri);
//        // let google service do the transcription.
//        mSpeechService.recognizeInputStreamFromUri("gs://ece1778-project.appspot.com/zJIhPIIUaIbdpqZPnNNEtZ84eBZ2/20200316_051649");
//        // mSpeechService.recognizeInputStreamFromUri(storageUri);


        // Show one paragraph per row.
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);

        final ArrayList<String> results = savedInstanceState == null ? null :
                savedInstanceState.getStringArrayList(STATE_RESULTS);

        if (results != null) {
            transcriptList.addAll(results);
        }


        dataAdapter = new DataAdapterTranscript(transcriptConfirm.this, transcriptList);

        mRecyclerView.setAdapter(dataAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        // Prepare Cloud Speech API
//        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);
//
//        Intent callingActivityIntent = getIntent();
//        // the following line is from the audio transcript at the same time. (from AudioConfirmActivity).
//        // transcriptList = callingActivityIntent.getStringArrayListExtra("transcriptList");
//
//        // firebase storage uri.
//        String storageUri = callingActivityIntent.getStringExtra("storageUri");
//        // experiment 1
//        Log.d("uri", storageUri);
//        // let google service do the transcription.
//        mSpeechService.recognizeInputStreamFromUri("gs://ece1778-project.appspot.com/zJIhPIIUaIbdpqZPnNNEtZ84eBZ2/20200316_051649");
//        // mSpeechService.recognizeInputStreamFromUri(storageUri);
//    }
//
//    @Override
//    protected void onStop() {
//        // Stop Cloud Speech API
//        mSpeechService.removeListener(mSpeechServiceListener);
//        unbindService(mServiceConnection);
//        mSpeechService = null;
//
//        super.onStop();
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dataAdapter != null) {
            outState.putStringArrayList(STATE_RESULTS, dataAdapter.getResults());
        }
    }


    public class DataAdapterTranscript extends RecyclerView.Adapter<DataAdapterTranscript.ViewHolder> {
        // Adapter class is used to render and handle the data collection and bind it to the view.
        private ArrayList<String> transcriptList;
        private Context context;

        public DataAdapterTranscript(Context context, ArrayList<String> transcriptList) {
            this.context = context;
            this.transcriptList = transcriptList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView paragraph;

            public ViewHolder(View view) {
                super(view);
                paragraph = (TextView) view.findViewById(R.id.text);
            }

            public TextView getTextView() {
                return paragraph;
            }

        }


        @Override
        public DataAdapterTranscript.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_result, viewGroup, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            viewHolder.paragraph.setText(transcriptList.get(i));

        }

        @Override
        public int getItemCount() {
            return transcriptList.size();
        }


        void addResult(String result) {
            this.transcriptList.add(0, result);
            notifyItemInserted(0);
        }


        public ArrayList<String> getResults() {
            return this.transcriptList;
        }

    }

    public void onClick_exit(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
