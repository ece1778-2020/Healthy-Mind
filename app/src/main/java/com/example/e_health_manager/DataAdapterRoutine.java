package com.example.e_health_manager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

            if (v.getId() == R.id.edit0) {
                Toast.makeText(context, "you click on the edit button for: " + position, Toast.LENGTH_SHORT).show();

            }

            if (v.getId() == R.id.del0) {
                Toast.makeText(context, "Delete item number " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ManualConfirm.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // update doctor_note_data and send it back.
                routine_changes.remove(position);
                doctor_note_data.put("routine_changes", routine_changes);

                intent.putExtra("curr_doctor_note_data", doctor_note_data);

                intent.putExtra("medicationList", medicationMapList);
                intent.putExtra("appointment", appointment);
                intent.putExtra("PARENT_ACTIVITY_REF", "DataAdapterRoutine");
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