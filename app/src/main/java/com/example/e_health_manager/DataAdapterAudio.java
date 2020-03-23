package com.example.e_health_manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class DataAdapterAudio extends RecyclerView.Adapter<DataAdapterAudio.ViewHolder> {
    // Adapter class is used to render and handle the data collection and bind it to the view.
    private ArrayList<String> transcriptList;
    private ArrayList<String> audioPathList;
    private Context context;

    public DataAdapterAudio(Context context, ArrayList<String> transcriptList, ArrayList<String> audioPathList) {
        this.context = context;
        this.transcriptList = transcriptList;
        this.audioPathList = audioPathList;
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
                Intent intent = new Intent(context, AudioReplay.class);
                intent.putExtra("audioLoc", audioPathList.get(getLayoutPosition()));
                context.startActivity(intent);
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