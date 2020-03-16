package com.example.e_health_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class transcriptConfirm extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<String> transcriptList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript_confirm);

        mRecyclerView = findViewById(R.id.recyclerView);

        Intent callingActivityIntent = getIntent();
        transcriptList = callingActivityIntent.getStringArrayListExtra("transcriptList");

        // Show one paragraph per row.
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);

        DataAdapterTranscript dataAdapter = new DataAdapterTranscript(transcriptConfirm.this, transcriptList);

        mRecyclerView.setAdapter(dataAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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

    }
}
