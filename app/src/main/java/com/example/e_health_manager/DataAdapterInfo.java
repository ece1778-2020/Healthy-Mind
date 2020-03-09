package com.example.e_health_manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class DataAdapterInfo extends RecyclerView.Adapter<DataAdapterInfo.ViewHolder> {
    // Adapter class is used to render and handle the data collection and bind it to the view.
    private ArrayList<HashMap<String, Object>> medicationMapList;
    private ArrayList<HashMap<String, Object>> infoList;
    private Context context;

    private HashMap<String, Object> doctor_note_data;
    private HashMap<String, Object> appointment;

    public DataAdapterInfo(Context context,
                           ArrayList<HashMap<String, Object>> medicationMapList,
                           HashMap<String, Object> doctor_note_data,
                           HashMap<String, Object> appointment) {
        this.context = context;
        this.medicationMapList = medicationMapList;
        this.doctor_note_data = doctor_note_data;
        this.appointment = appointment;

        this.infoList = (ArrayList<HashMap<String, Object>>) this.doctor_note_data.get("more_info");

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

            if (v.getId() == R.id.edit0) {
                Toast.makeText(context, "you click on the edit button for: " + position, Toast.LENGTH_SHORT).show();

            }

            // delete button
            if (v.getId() == R.id.del0) {

                // Dialog to make user confirm delete.
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
                        infoList.remove(position);
                        doctor_note_data.put("more_info", infoList);

                        intent.putExtra("curr_doctor_note_data", doctor_note_data);

                        intent.putExtra("medicationList", medicationMapList);
                        intent.putExtra("appointment", appointment);
                        intent.putExtra("PARENT_ACTIVITY_REF", "DataAdapterInfo");
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

        ArrayList<HashMap<String, Object>> infoList = (ArrayList<HashMap<String, Object>>) this.doctor_note_data.get("more_info");
        String display_text =
                "For: " +
                    infoList.get(i).get("purpose").toString() +
                    ", go to: " +
                    infoList.get(i).get("who").toString() +
                        "call: " +
                        infoList.get(i).get("phone").toString();

        // NOTE: the medication here is refer to feeling.
        viewHolder.medication.setText(display_text);
    }

    @Override
    public int getItemCount() {
        return this.infoList.size();
    }

}