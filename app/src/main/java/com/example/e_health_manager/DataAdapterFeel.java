package com.example.e_health_manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class DataAdapterFeel extends RecyclerView.Adapter<DataAdapterFeel.ViewHolder> {
    // Adapter class is used to render and handle the data collection and bind it to the view.
    private ArrayList<HashMap<String, Object>> medicationMapList;
    private ArrayList<HashMap<String, Object>> feelingList;
    private Context context;

    private HashMap<String, Object> doctor_note_data;
    private HashMap<String, Object> appointment;

    public DataAdapterFeel(Context context,
                           ArrayList<HashMap<String, Object>> medicationMapList,
                           HashMap<String, Object> doctor_note_data,
                           HashMap<String, Object> appointment) {
        this.context = context;
        this.medicationMapList = medicationMapList;
        this.doctor_note_data = doctor_note_data;
        this.appointment = appointment;

        this.feelingList = (ArrayList<HashMap<String, Object>>) this.doctor_note_data.get("feelings_and_instructions");

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // NOTE: the medication here is refer to feeling.
        public TextView medication;
        public Button editBtn, delBtn;

        public ViewHolder(View view) {
            super(view);
            // id.my_text_view is in list_layout.
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

            // Edit button.
            if (v.getId() == R.id.edit0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // Get the layout inflater
                LayoutInflater inflater = LayoutInflater.from(context);
                View my_view = inflater.inflate(R.layout.dialog_edit_feel, null);

                final EditText feel = my_view.findViewById(R.id.feel);
                feel.setText(feelingList.get(position).get("feeling").toString());

                final EditText todo = my_view.findViewById(R.id.todo);
                todo.setText(feelingList.get(position).get("instruction").toString());

                builder.setView(my_view);
                builder.setTitle("Edit information");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        HashMap<String, Object> new_feel_instruction = new HashMap<>();
                        new_feel_instruction.put("feeling", feel.getText().toString());
                        new_feel_instruction.put("instruction", todo.getText().toString());

                        // replace the medication at index position.
                        feelingList.set(position, new_feel_instruction);

                        Toast.makeText(context, "Edited Successfully!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context, ManualConfirm.class);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("curr_doctor_note_data", doctor_note_data);
                        intent.putExtra("medicationList", medicationMapList);
                        intent.putExtra("appointment", appointment);
                        intent.putExtra("PARENT_ACTIVITY_REF", "DataAdapterList");
                        context.startActivity(intent);
                    }
                });
                builder.setNegativeButton(android.R.string.no, null);
                builder.show();

            }

            // Delete button
            if (v.getId() == R.id.del0) {

                // Dialog to make user confirm.
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete the medication?");
                builder.setTitle("Alert");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(context, "Delete item number " + position, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, ManualConfirm.class);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        // update doctor_note_data and send it back.
                        feelingList.remove(position);
                        doctor_note_data.put("feelings_and_instructions", feelingList);

                        intent.putExtra("curr_doctor_note_data", doctor_note_data);

                        intent.putExtra("medicationList", medicationMapList);
                        intent.putExtra("appointment", appointment);
                        intent.putExtra("PARENT_ACTIVITY_REF", "DataAdapterFeel");
                        context.startActivity(intent);
                    }
                });
                builder.setNegativeButton(android.R.string.no, null);
                builder.show();
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

        ArrayList<HashMap<String, Object>> feelingList = (ArrayList<HashMap<String, Object>>) this.doctor_note_data.get("feelings_and_instructions");
        String display_text =
                "I might feel: " +
                        feelingList.get(i).get("feeling").toString() +
                        ". What to do: " +
                        feelingList.get(i).get("instruction").toString() + ".";

        // NOTE: the medication here is refer to feeling.
        viewHolder.medication.setText(display_text);
    }

    @Override
    public int getItemCount() {
        return this.feelingList.size();
    }

}