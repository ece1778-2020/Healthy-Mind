package com.example.e_health_manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class DataAdapterRoutine extends RecyclerView.Adapter<DataAdapterRoutine.ViewHolder> {
    // Adapter class is used to render and handle the data collection and bind it to the view.
    private ArrayList<HashMap<String, Object>> medicationMapList;
    private ArrayList<HashMap<String, Object>> routine_changes;
    private Context context;

    private HashMap<String, Object> doctor_note_data;
    private HashMap<String, Object> appointment;

    public DataAdapterRoutine(Context context,
                              ArrayList<HashMap<String, Object>> medicationMapList,
                              HashMap<String, Object> doctor_note_data,
                              HashMap<String, Object> appointment) {
        this.context = context;
        this.medicationMapList = medicationMapList;
        this.doctor_note_data = doctor_note_data;
        this.appointment = appointment;
        this.routine_changes = (ArrayList<HashMap<String, Object>>) this.doctor_note_data.get("routine_changes");
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // NOTE: the medication here is refer to routine_changes.
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
                View my_view = inflater.inflate(R.layout.dialog_edit_routine, null);

                final EditText activity = my_view.findViewById(R.id.activity);
                activity.setText(routine_changes.get(position).get("activity").toString());

                final EditText instruction = my_view.findViewById(R.id.instruction);
                instruction.setText(routine_changes.get(position).get("instruction").toString());

                builder.setView(my_view);
                builder.setTitle("Edit information");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        HashMap<String, Object> routine_instruction = new HashMap<>();
                        routine_instruction.put("activity", activity.getText().toString());
                        routine_instruction.put("instruction", instruction.getText().toString());

                        // replace the medication at index position.
                        routine_changes.set(position, routine_instruction);

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


            // delete button
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
                        routine_changes.remove(position);
                        doctor_note_data.put("routine_changes", routine_changes);

                        intent.putExtra("curr_doctor_note_data", doctor_note_data);

                        intent.putExtra("medicationList", medicationMapList);
                        intent.putExtra("appointment", appointment);
                        intent.putExtra("PARENT_ACTIVITY_REF", "DataAdapterRoutine");
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

        ArrayList<HashMap<String, Object>> routine_changes = (ArrayList<HashMap<String, Object>>) this.doctor_note_data.get("routine_changes");
        String display_text =
                "Activity: " +
                    routine_changes.get(i).get("activity").toString() +
                    ". Instruction: " +
                    routine_changes.get(i).get("instruction").toString() + ".";

        // NOTE: the medication here is refer to routine_changes.
        viewHolder.medication.setText(display_text);
    }

    @Override
    public int getItemCount() {
        return this.routine_changes.size();
    }

}