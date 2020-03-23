package com.example.e_health_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class transcriptConfirm extends AppCompatActivity {

    private static final String STATE_RESULTS = "results";

    private RecyclerView mRecyclerView;
    private RecyclerView recyclerView_doctor_notes;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<String> transcriptList = new ArrayList<>();

    private String firebaseStorageUri;

    private DataAdapterTranscript dataAdapter;
    private DataAdapterNotes dataAdapterNotes;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    ArrayList<HashMap> noteList = new ArrayList<>();
    ArrayList<String> doctorNotesIDSelected = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript_confirm);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mRecyclerView = findViewById(R.id.recyclerView);

        recyclerView_doctor_notes = findViewById(R.id.recyclerView_doctor_notes);

        mFirestore.collection("doctor's note")
                .whereEqualTo("user_id", mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, Object> itemHashMap = new HashMap<>();
                                itemHashMap.put("timestamp", document.get("timestamp"));
                                itemHashMap.put("came_date", document.get("came_date"));
                                itemHashMap.put("left_date", document.get("left_date"));
                                itemHashMap.put("reason", document.get("reason_for_hospital"));
                                itemHashMap.put("noteID", document.getId());
                                noteList.add(itemHashMap);
                            }

                            // NOTE: Cannot put the following outside of this OnCompleteListener.
                            dataAdapterNotes = new DataAdapterNotes(transcriptConfirm.this, noteList);
                            recyclerView_doctor_notes.setAdapter(dataAdapterNotes);
                            recyclerView_doctor_notes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        }
                    }
                });


        Intent callingActivityIntent = getIntent();
        // the following line is from the audio transcript at the same time. (from AudioConfirmActivity).
        transcriptList = callingActivityIntent.getStringArrayListExtra("transcriptList");
        // firebaseStorageUri = callingActivityIntent.getStringExtra("storageUri");
        firebaseStorageUri = callingActivityIntent.getData().toString();

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

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView paragraph;
            public Button editBtn, delBtn;

            public ViewHolder(View view) {
                super(view);
                paragraph = (TextView) view.findViewById(R.id.text);
                editBtn = (Button) view.findViewById(R.id.edit0);
                delBtn = (Button) view.findViewById(R.id.del0);

                editBtn.setOnClickListener(this);
                delBtn.setOnClickListener(this);
            }

            public TextView getTextView() {
                return paragraph;
            }

            @Override
            public void onClick(View v) {

                // get the position of the item clicked.
                final int position = getLayoutPosition();

                // Edit button.
                if (v.getId() == R.id.edit0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // Get the layout inflater
                    final LayoutInflater inflater = LayoutInflater.from(context);
                    View my_view = inflater.inflate(R.layout.dialog_edit_transcript, null);

                    final EditText transcript_view_holder = my_view.findViewById(R.id.transcript);
                    transcript_view_holder.setText(paragraph.getText());

                    builder.setView(my_view);
                    builder.setTitle("Edit transcript");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            // replace the transcript text at the given position.
                            transcriptList.set(position, transcript_view_holder.getText().toString());

                            Toast.makeText(context, "Edited Successfully!", Toast.LENGTH_SHORT).show();
                            paragraph.setText(transcript_view_holder.getText());

                            //Intent intent = new Intent(context, transcriptConfirm.class);
                            //intent.putExtra("transcriptList", transcriptList);
                            //context.startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(android.R.string.no, null);
                    builder.show();
                }

                // delete button.
                if (v.getId() == R.id.del0) {

                    // Dialog to make user confirm.
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete this item?");
                    builder.setTitle("Alert");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(context, "Delete item number " + position, Toast.LENGTH_SHORT).show();

                            // update doctor_note_data and send it back.
                            transcriptList.remove(position);

                            Intent intent = new Intent(context, transcriptConfirm.class);
                            intent.putExtra("transcriptList", transcriptList);
                            context.startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(android.R.string.no, null);
                    builder.show();

                }

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


    public class DataAdapterNotes extends RecyclerView.Adapter<DataAdapterNotes.ViewHolder> {
        // Adapter class is used to render and handle the data collection and bind it to the view.
        private ArrayList<HashMap> doctorNoteList;
        public ArrayList<String> doctorNotesIDList;
        private Context context;

        public DataAdapterNotes(Context context, ArrayList<HashMap> doctorNoteList) {
            this.context = context;
            this.doctorNoteList = doctorNoteList;
            this.doctorNotesIDList = new ArrayList<>();

            for (HashMap<String, Object> m : doctorNoteList) {
                this.doctorNotesIDList.add(m.get("noteID").toString());
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView paragraph;
            public CheckBox selected;

            public ViewHolder(View view) {
                super(view);
                paragraph = (TextView) view.findViewById(R.id.text);
                selected = view.findViewById(R.id.checkbox);
                selected.setOnClickListener(this);
            }

            public TextView getTextView() {
                return paragraph;
            }

            @Override
            public void onClick(View v) {
                // NOTE: when click on a checkbox, the checkbox change state first, then perform onClick.

                // get the position of the medication clicked.
                final int position = getLayoutPosition();
                String dID = dataAdapterNotes.doctorNotesIDList.get(position);


                if (doctorNotesIDSelected.size() == 0) {
                    // all box unchecked.
                    // Toast.makeText(getApplicationContext(), dID+" added to the list!", Toast.LENGTH_LONG).show();
                    doctorNotesIDSelected.add(dID);
                } else if (doctorNotesIDSelected.contains(dID)) {
                    // uncheck a checked box.
                    doctorNotesIDSelected.remove(dID);
                    selected.setChecked(false);
                } else if (doctorNotesIDSelected.size() > 0) {
                    Toast.makeText(getApplicationContext(), "Cannot attach the audio and transcript to more than one doctor's note.", Toast.LENGTH_SHORT).show();
                    selected.setChecked(false);
                }

            }
        }

        @Override
        public DataAdapterNotes.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.list_layout_doctor_notes, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            HashMap<String, Object> itemHashMap = doctorNoteList.get(i);
            String timestamp = itemHashMap.get("timestamp").toString();
            timestamp = timestamp.split("_")[0];
            timestamp = timestamp.substring(0, 4) + "/" + timestamp.substring(4, 6) + "/" + timestamp.substring(6, 8);

            String displayText = "Added at: "
                    + timestamp
                    + ". Came Date: "
                    + itemHashMap.get("came_date").toString()
                    + ". Left Date: "
                    + itemHashMap.get("left_date").toString()
                    + ". Reason: "
                    + itemHashMap.get("reason").toString();

            viewHolder.paragraph.setText(displayText);
        }

        @Override
        public int getItemCount() {
            return doctorNoteList.size();
        }


        public ArrayList<HashMap> getResults() {
            return this.doctorNoteList;
        }
    }

    public void onClick_exit(View view) {

        if (doctorNotesIDSelected.size() == 0) {
            // if there is no existing doctor notes.
            // store to some extra database.

        } else if (doctorNotesIDSelected.size() == 1) {
            // change hasAudio field to true.
            mFirestore.collection("doctor's note")
                    .document(doctorNotesIDSelected.get(0))
                    .update("hasAudio", true);

            // add a new or override any existing transcript.
            mFirestore.collection("doctor's note")
                    .document(doctorNotesIDSelected.get(0))
                    .update("transcript_text", transcriptList);

            // add a new or override any existing audio path.
            mFirestore.collection("doctor's note")
                    .document(doctorNotesIDSelected.get(0))
                    .update("audio_path", firebaseStorageUri);

        }

        //Toast.makeText(getApplicationContext(), doctorNotesIDSelected.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
