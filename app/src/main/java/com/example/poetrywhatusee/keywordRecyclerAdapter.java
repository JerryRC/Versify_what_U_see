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

import java.util.ArrayList;

public class keywordRecyclerAdapter
        extends RecyclerView.Adapter<keywordRecyclerAdapter.LinearViewHolder> {

    private Context mContext;
    private ArrayList<String> resultList;

    public keywordRecyclerAdapter(Context context, ArrayList<String> resList) {
        this.mContext = context;
        this.resultList = resList;
    }

    @NonNull
    @Override
    public keywordRecyclerAdapter.LinearViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        return new LinearViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.layout_recycler_linear_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull keywordRecyclerAdapter.
            LinearViewHolder holder, int position) {

        //设置毛笔字字体
        Typeface typeface = ResourcesCompat.getFont(mContext, R.font.maobizi);
        holder.checkBox.setTypeface(typeface);

        //因为这里adapter用的是wrap contain 所以避免单字的控件太窄 给出最小dp值
        if (ScreenDisplay.pxToDp(mContext, holder.checkBox.getWidth()) < 70) {
            holder.checkBox.setWidth(ScreenDisplay.dpToPx(mContext, 70));
        }
        //设置意象名字
        holder.checkBox.setText(resultList.get(position));

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    static class LinearViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_in_grid);
        }
    }

    /**
     * dp与px单位之间的互相转化功能类
     */
    public static class ScreenDisplay {

        /**
         * 根据手机的分辨率从dp的单位转成为px(像素)
         *
         * @param context 上下文
         * @param dpValue 目标dp值的数值
         * @return 对应像素值px的数值
         */
        public static int dpToPx(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        /**
         * 根据手机的分辨率从 px(像素)的单位转成为dp
         *
         * @param context 上下文
         * @param pxValue 目标像素值px的数值
         * @return 对应dp值的数值
         */
        public static int pxToDp(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
    }
}
