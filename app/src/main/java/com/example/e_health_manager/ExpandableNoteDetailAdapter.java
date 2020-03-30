package com.example.e_health_manager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class ExpandableNoteDetailAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> detailDataHeader;
    private HashMap<String, List<HashMap>> detailHashMap;

    public ExpandableNoteDetailAdapter(Context context, List<String> detailDataHeader, HashMap<String, List<HashMap>> detailHashMap) {
        this.context = context;
        this.detailDataHeader = detailDataHeader;
        this.detailHashMap = detailHashMap;
    }

    @Override
    public int getGroupCount() {
        return detailDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return detailHashMap.get(detailDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return detailDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition);
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

        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.note_list_group, null);
        }

        TextView groupHeader = convertView.findViewById(R.id.expandableListTitle);
//        if(groupPosition == 3){
//            if(detailHashMap.get(detailDataHeader.get(3)).get(0).get("activity") == "You don't have to change your routine"){
//                groupHeader.setBackgroundResource(R.color.ic_mic_background_disable);
//                Log.d("adapter", "getGroupView: the color of background with id: "+groupPosition+" has been changed to grey");
//            }
//            groupHeader.setText(headerTitle);
//        }
//        else if(groupPosition == 4){
//            if(detailHashMap.get(detailDataHeader.get(4)).get(0).get("date") == ""){
//                groupHeader.setBackgroundResource(R.color.ic_mic_background_disable);
//                Log.d("adapter", "getGroupView: the color of background with id: "+groupPosition+" has been changed to grey");
//            }
//            groupHeader.setText(headerTitle);
//        }
//        else if(groupPosition == 5){
//            if(detailHashMap.get(detailDataHeader.get(5)).get(0).get("ownNotes") == "You don't have additional notes."){
//                groupHeader.setBackgroundResource(R.color.ic_mic_background_disable);
//                Log.d("adapter", "getGroupView: the color of background with id: "+groupPosition+" has been changed to grey");
//            }
//            groupHeader.setText(headerTitle);
//        }
//        else{
//            groupHeader.setText(headerTitle);
//        }
        groupHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (groupPosition == 0) {
            LayoutInflater inflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.note_detail_general, null);
            TextView child1 = convertView.findViewById(R.id.detailGeneral1);
            TextView child2 = convertView.findViewById(R.id.detailGeneral2);
            TextView child3 = convertView.findViewById(R.id.detailGeneral3);
            TextView child4 = convertView.findViewById(R.id.detailGeneral4);
            String timestamp = detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("timestamp").toString();
            timestamp = timestamp.split("_")[0];
            timestamp = timestamp.substring(0, 4) + "/" + timestamp.substring(4, 6) + "/" + timestamp.substring(6, 8);
            child1.setText("Added at: " + timestamp);
            child2.setText("Come Date: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("comeDate").toString());
            child3.setText("Leave Date: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("leaveDate").toString());
            child4.setText("Reason: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("reason").toString());
        } else if (groupPosition == 1) {
            LayoutInflater inflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.note_detail_medications, null);
            TextView child1 = convertView.findViewById(R.id.detailMedi1);
            TextView child2 = convertView.findViewById(R.id.detailMedi2);
            TextView child3 = convertView.findViewById(R.id.detailMedi3);
            TextView child4 = convertView.findViewById(R.id.detailMedi4);
            child1.setText("Name: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("name").toString());
            child2.setText("What it is for: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("reason").toString());
            child3.setText("Dose: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("dose").toString());
            child4.setText("You should take at: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("time").toString());
        } else if (groupPosition == 2) {
            if (childPosition == 0) {
                LayoutInflater inflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflator.inflate(R.layout.note_detail_emergency, null);
                TextView child1 = convertView.findViewById(R.id.detailEmer1);
                child1.setText("Go to Emergency if: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("emergency").toString());
            } else {
                LayoutInflater inflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflator.inflate(R.layout.note_detail_feelings, null);
                TextView child1 = convertView.findViewById(R.id.detailFeel1);
                TextView child2 = convertView.findViewById(R.id.detailFeel2);
                child1.setText("Feeling: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("feeling").toString());
                child2.setText("What to do: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("instruction").toString());
            }
        } else if (groupPosition == 3) {
            LayoutInflater inflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.note_detail_routines, null);
            TextView child1 = convertView.findViewById(R.id.detailRoutine1);
            TextView child2 = convertView.findViewById(R.id.detailRoutine2);
            if (detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("activity").toString() ==
                    "You don't have to change your routine") {
                child1.setText(detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("activity").toString());
                child2.setText("");
            } else {
                child1.setText("Activity: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("activity").toString());
                child2.setText("Instruction: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("instruction").toString());
            }
        } else if (groupPosition == 4) {
            LayoutInflater inflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.note_detail_appintment, null);
            TextView child1 = convertView.findViewById(R.id.detailAppoint1);
            TextView child2 = convertView.findViewById(R.id.detailAppoint2);
            TextView child3 = convertView.findViewById(R.id.detailAppoint3);
            TextView child4 = convertView.findViewById(R.id.detailAppoint4);
            TextView child5 = convertView.findViewById(R.id.detailAppoint5);
            TextView child6 = convertView.findViewById(R.id.detailAppoint6);
            child1.setText("Go see: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("doctor").toString());
            child2.setText("For: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("reason").toString());
            child3.setText("Date: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("date").toString());
            child4.setText("Time: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("time").toString());
            child5.setText("Location: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("location").toString());
            child6.setText("Phone #: " + detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("phone").toString());

        } else if (groupPosition == 5) {
            LayoutInflater inflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.own_notes, null);
            TextView notes = convertView.findViewById(R.id.detailOwnNotes1);
            notes.setText(detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("ownNotes").toString());

        } else if (groupPosition == 6) {
            LayoutInflater inflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.note_detail_audio, null);
            TextView notes = convertView.findViewById(R.id.detailOwnNotes1);
            notes.setText(detailHashMap.get(detailDataHeader.get(groupPosition)).get(childPosition).get("transcriptList").toString());

        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
