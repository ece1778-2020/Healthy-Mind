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
import java.util.Arrays;
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

        ArrayList<Integer> selectedTimes;

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
                View my_view = inflater.inflate(R.layout.dialog_edit_medications, null);
                final EditText medication_name = my_view.findViewById(R.id.medication_name);
                medication_name.setText(medicationMapList.get(position).get("name").toString());

                final EditText dose = my_view.findViewById(R.id.dose);
                dose.setText(medicationMapList.get(position).get("dose").toString());

                final EditText medication_reason = my_view.findViewById(R.id.medication_reason);
                medication_reason.setText(medicationMapList.get(position).get("reason").toString());

                builder.setView(my_view);
                builder.setTitle("Edit medications");

                selectedTimes = new ArrayList<Integer>();

                builder.setMultiChoiceItems(R.array.time_array, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedTimes.add(which);
                                } else if (selectedTimes.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedTimes.remove(Integer.valueOf(which));
                                }
                            }
                        });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        HashMap<String, Object> new_medication = new HashMap<>();
                        new_medication.put("name", medication_name.getText().toString());

                        new_medication.put("reason", dose.getText().toString());

                        new_medication.put("dose", medication_reason.getText().toString());

                        ArrayList<String> timeList = new ArrayList<>();
                        if (selectedTimes.contains(0)) {
                            timeList.add("morning");
                        }
                        if (selectedTimes.contains(1)) {
                            timeList.add("noon");
                        }
                        if (selectedTimes.contains(2)) {
                            timeList.add("afternoon");
                        }
                        if (selectedTimes.contains(3)) {
                            timeList.add("night");
                        }

                        new_medication.put("time", timeList);
                        new_medication.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        // replace the medication at index position.
                        medicationMapList.set(position, new_medication);

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

            // delete button.
            if (v.getId() == R.id.del0) {
                // Dialog to make user confirm.
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete the medication?");
                builder.setTitle("Alert");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(context, "Delete medication number " + position, Toast.LENGTH_SHORT).show();
                        medicationMapList.remove(position);
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