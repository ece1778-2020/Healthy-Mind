package com.example.e_health_manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DataAdapterSelectMedication extends RecyclerView.Adapter<DataAdapterSelectMedication.ViewHolder> {
    // Adapter class is used to render and handle the data collection and bind it to the view.
    private ArrayList<String> transcriptList;
    private Context context;
    private String audio_id;

    public DataAdapterSelectMedication(Context context, ArrayList<String> transcriptList, String audio_id) {
        this.context = context;
        this.transcriptList = transcriptList;
        this.audio_id = audio_id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // NOTE: the transcript here is refer to new info map.
        public TextView transcript;

        public ViewHolder(View view) {
            super(view);
            // id.my_text_view is in list_layout.
            transcript = (TextView) view.findViewById(R.id.my_text_view);
            transcript.setOnClickListener(this);
        }

        public TextView getTextView() {
            return transcript;
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.my_text_view) {
                // Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // Get the layout inflater
                final LayoutInflater inflater = LayoutInflater.from(context);
                View my_view = inflater.inflate(R.layout.dialog_select_medication, null);

                final EditText transcript_view_holder = my_view.findViewById(R.id.med1);

                String display_text = transcript.getText().toString();

                String[] med_array = {"ativan", "cipralex", "citalopram", "zoloft", "celexa", "edronax",
                        "escitalopram", "trazadone", "lorazepam", "fluoxetine", "lexapro", "cipramil",
                        "paxil", "luvox", "elavil", "prozac", "sertraline", "paroxetine", "pexeva",
                        "seroxat", "ibuprofen", "silenor", "ambien", "lunesta", "sonata", "silenor",
                        "rozerem", "belsomra", "restoril", "benadryl"};

                for (String t : med_array) {
                    if (display_text.toLowerCase().contains(t)) {
                        display_text = t.substring(0, 1).toUpperCase() + t.substring(1).toLowerCase();
                        break;
                    }
                }

                transcript_view_holder.setText(display_text);

                builder.setView(my_view);
                builder.setTitle("Please only keep the medication name and delete any irrelevant words");

                builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseFirestore.getInstance().collection("audio")
                                .document(audio_id)
                                .update("medication_name", transcript_view_holder.getText().toString());

                        transcript.setText(transcript_view_holder.getText().toString());
                        transcript.setTextColor(context.getColor(android.R.color.holo_red_dark));
                    }
                });
                builder.setNegativeButton("No relevant info", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transcript.setText("");
                        transcript.setBackgroundColor(context.getColor(android.R.color.transparent));
                    }
                });
                builder.show();
            }

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_layout_audio, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        // NOTE: the transcript here is refer to new info map.
        viewHolder.transcript.setText(this.transcriptList.get(i));
    }

    @Override
    public int getItemCount() {
        return this.transcriptList.size();
    }

}