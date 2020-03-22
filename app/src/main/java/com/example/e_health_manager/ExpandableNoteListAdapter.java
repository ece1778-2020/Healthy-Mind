package com.example.e_health_manager;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpandableNoteListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<HashMap>>listHashMap;

    public ExpandableNoteListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<HashMap>> listHashMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater inflator = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.note_list_group, null);
        }

        TextView groupHeader = convertView.findViewById(R.id.expandableListTitle);
        groupHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflator = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.note_list_item, null);
        }
        String child1Text=null, child2Text=null, child3Text=null, child4Text=null;
        ImageView noteIcon = (ImageView)convertView.findViewById(R.id.noteListIcon);

        if(groupPosition == 0){
            if(listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("timestamp").toString() ==
                    "You don't have any doctor's notes."){
                child1Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("timestamp").toString();
                child2Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("comeDate").toString();
                child3Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("leaveDate").toString();
                child4Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("reason").toString();
                noteIcon.setImageResource(R.drawable.ic_doctor_note);
            }
            else{
                String timestamp = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("timestamp").toString();
                timestamp = timestamp.split("_")[0];
                timestamp = timestamp.substring(0,4)+"/"+timestamp.substring(4,6)+"/"+timestamp.substring(6,8);
                child1Text = "Added at: "+ timestamp;
                child2Text = "Came Date: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("comeDate").toString();
                child3Text = "Left Date: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("leaveDate").toString();
                child4Text = "Reason: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("reason").toString();
                noteIcon.setImageResource(R.drawable.ic_doctor_note);
            }
        }
        else if(groupPosition == 1){
            if(listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("name").toString() ==
                    "You don't have any medications."){
                child1Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("name").toString();
                child2Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("for").toString();
                child3Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("dose").toString();
                child4Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("time").toString();
                noteIcon.setImageResource(R.drawable.ic_local_hospital);
            }
            else{
                child1Text = "Name: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("name").toString();
                child2Text = "For: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("for").toString();
                child3Text = "Dose: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("dose").toString();
                child4Text = "You should take at: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("time").toString();
                noteIcon.setImageResource(R.drawable.ic_local_hospital);
            }
        }
        else if(groupPosition == 2){
            if(listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("see").toString() ==
                    "You don't have any appointments."){
                child1Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("see").toString();
                child2Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("date").toString();
                child3Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("time").toString();
                child4Text = listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("location").toString();
                noteIcon.setImageResource(R.drawable.ic_access_alarm);
            }
            else{
                child1Text = "See: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("see").toString();
                child2Text = "Date: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("date").toString();
                child3Text = "Time: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("time").toString();
                child4Text = "Location: "+listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition).get("location").toString();
                noteIcon.setImageResource(R.drawable.ic_access_alarm);
            }
        }

        TextView child1 = convertView.findViewById(R.id.child1Text);
        TextView child2 = convertView.findViewById(R.id.child2Text);
        TextView child3 = convertView.findViewById(R.id.child3Text);
        TextView child4 = convertView.findViewById(R.id.child4Text);
        child1.setText(child1Text);
        child2.setText(child2Text);
        child3.setText(child3Text);
        child4.setText(child4Text);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
