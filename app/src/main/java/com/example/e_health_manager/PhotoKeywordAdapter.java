package com.example.e_health_manager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PhotoKeywordAdapter extends RecyclerView.Adapter<PhotoKeywordAdapter.ViewHolder>{
    private ArrayList<String> keywordList;
    private Context context;

    public PhotoKeywordAdapter(Context context, ArrayList<String> keywordList) {
        this.context = context;
        this.keywordList = keywordList;
    }

    @Override
    public PhotoKeywordAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_keyword_layout, viewGroup, false);
        return new PhotoKeywordAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoKeywordAdapter.ViewHolder viewHolder, int i) {
        viewHolder.keyword.setText(keywordList.get(i));
    }

    @Override
    public int getItemCount() {
        return keywordList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView keyword;

        public ViewHolder(View view) {
            super(view);
            keyword = view.findViewById(R.id.keyword);
        }
    }
}
