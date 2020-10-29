package com.example.poetrywhatusee;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class poemRecyclerAdapter extends RecyclerView.Adapter<poemRecyclerAdapter.PoemViewHolder> {

    private Context mContext;
    private ArrayList<String> mPoems;
    private OnItemClickListener mListener;

    public poemRecyclerAdapter(Context context,
                               ArrayList<String> poems, OnItemClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mPoems = poems;
    }

    @NonNull
    @Override
    public poemRecyclerAdapter.PoemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new PoemViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.layout_recycler_poem_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final poemRecyclerAdapter
            .PoemViewHolder holder, final int position) {

        //设置毛笔字字体
        Typeface typeface = ResourcesCompat.getFont(mContext, R.font.maobizi);
        holder.checkBox.setTypeface(typeface);

        try {
            JSONObject jsonObject = new JSONObject(mPoems.get(position));
            String text = jsonObject.getString("txt");
            String name = jsonObject.getString("name");
            String author = jsonObject.getString("author");
            String show = text + "\n\n\t ——" + name + " " + author;
            holder.checkBox.setText(show);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (position == 0) {
            holder.checkBox.setChecked(true);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPoems.size();
    }

    static class PoemViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;

        public PoemViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_in_gridP);
        }

    }

    public interface OnItemClickListener {
        void onClick(int pos);
    }

}
