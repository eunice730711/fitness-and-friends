package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JCLIN on 2016/11/28.
 */


public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.CustomViewHolder> {

    private List<String> w;
    private List<WeekContent> content;

    public WeekAdapter(List<String> w, List<WeekContent> content) {
        this.w = w;
        this.content = content;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_week, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        //把 資料內容 和 介面 綁定


        List<String> level_name = new ArrayList<String>(Arrays.asList("初階","中階"));

        customViewHolder.textView_w.setText(w.get(i));


        if(i < content.size()) {

            String c = content.get(i).getDayCount() + " 天";
            if (content.get(i).getLevel() == 2) {
                int j = 0;
                for (j = 0; j < content.get(i).getDays().size(); j++) {
                    if (content.get(i).getDays().get(j).getChoose()) break;
                }
                c += "\n距離基礎 : " + content.get(i).getDaysItem(j).getDist() + " km";
            }
            customViewHolder.textView_content.setText(c);
            customViewHolder.textView_level.setText(level_name.get(content.get(i).getLevel()-1));
        }

    }

    @Override
    public int getItemCount() {
        return w.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        //自定義的 ViewHolder，綁定item_week的 id

        protected TextView textView_w;
        protected TextView textView_level;
        protected TextView textView_content;

        public CustomViewHolder(View view) {
            super(view);

            this.textView_w = (TextView) view.findViewById(R.id.Week_x);
            this.textView_content = (TextView) view.findViewById(R.id.RunContent);
            this.textView_level = (TextView) view.findViewById(R.id.RunLevel);

        }
    }
}