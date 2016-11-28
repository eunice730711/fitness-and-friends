package com.google.firebase.codelab.friendlychat;

import android.content.Context;
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


public class WeekAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> w;
    private List<WeekContent> content;
    public WeekAdapter(Context c, List<String> w, List<WeekContent> content){
        inflater = LayoutInflater.from(c);
        this.w = w;
        this.content = content;
    }
    @Override
    public int getCount() {
        return w.size();
    }

    @Override
    public Object getItem(int i) {
        return w.get(i);
    }

    @Override
    public long getItemId(int i) {
        return w.indexOf(getItem(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_week,viewGroup,false);

        String week = (String) getItem(i);
        TextView textView_w = (TextView) view.findViewById(R.id.Week_x);
        textView_w.setText(week);

        if(i<content.size()){
            List<String> level_name = new ArrayList<String>(Arrays.asList("初階","中階"));
            TextView textView_level = (TextView) view.findViewById(R.id.RunLevel);
            textView_level.setText(level_name.get(content.get(i).getLevel()-1));

            TextView textView_content = (TextView) view.findViewById(R.id.RunContent);
            String c = content.get(i).getDayCount()+" 天";

            if(content.get(i).getLevel() == 2){
                int j=0;
                for(j=0; j<content.get(i).getDays().size(); j++){
                    if(content.get(i).getDays().get(j).getChoose())break;
                }
                c += "\n距離基礎 : " + content.get(i).getDaysItem(j).getDist()+" km";
            }

            textView_content.setText(c);

        }

        return view;
    }
}