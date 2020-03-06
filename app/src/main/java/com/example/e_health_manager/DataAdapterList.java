package com.example.e_health_manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class DataAdapterList extends RecyclerView.Adapter<DataAdapterList.ViewHolder> {
    // Adapter class is used to render and handle the data collection and bind it to the view.
    private ArrayList<String> medicationTextList;
    private ArrayList<HashMap<String, Object>> medicationMapList;
    private Context context;

    private HashMap<String, Object> doctor_note_data;
    private HashMap<String, Object> appointment;

    public DataAdapterList(Context context, ArrayList<String> medicationTextList,
                           ArrayList<HashMap<String, Object>> medicationMapList,
                           HashMap<String, Object> doctor_note_data,
                           HashMap<String, Object> appointment) {
        this.context = context;
        this.medicationTextList = medicationTextList;
        this.medicationMapList = medicationMapList;
        this.doctor_note_data = doctor_note_data;
        this.appointment = appointment;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView medication;
        public Button editBtn, delBtn;

        public ViewHolder(View view) {
            super(view);
            medication = (TextView) view.findViewById(R.id.my_text_view);
            editBtn = (Button) view.findViewById(R.id.edit0);
            delBtn = (Button) view.findViewById(R.id.del0);

            editBtn.setOnClickListener(this);
            delBtn.setOnClickListener(this);

        }

        public TextView getTextView() {
            return medication;
        }

        @Override
        public void onClick(View v) {

            // get the position of the medication clicked.
            final int position = getLayoutPosition();

            if (v.getId() == R.id.edit0) {
                Toast.makeText(context, "you click on the edit button for: " + position, Toast.LENGTH_SHORT).show();

            }

            if (v.getId() == R.id.del0) {
                Toast.makeText(context, "Delete medication number " + position, Toast.LENGTH_SHORT).show();
                medicationMapList.remove(position);
                Intent intent = new Intent(context, ManualConfirm.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("curr_doctor_note_data", doctor_note_data);
                intent.putExtra("medicationList", medicationMapList);
                intent.putExtra("appointment", appointment);
                intent.putExtra("PARENT_ACTIVITY_REF", "DataAdapterList");
                context.startActivity(intent);

            }

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * gets the comment from adapter and passes to somewhere to load the comment to activity_manual_confirm.xml
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        String display_text = medicationTextList.get(i);

        viewHolder.medication.setText(display_text);

    }

    @Override
    public int getItemCount() {
        return medicationTextList.size();
    }

}