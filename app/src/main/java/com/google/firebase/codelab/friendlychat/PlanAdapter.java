package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.firebase.codelab.friendlychat.Day.DatetoString;

/**
 * Created by JCLIN on 2016/11/28.
 */

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.CustomViewHolder> {

    //這邊是要顯示的資料

    private List<Day> dayList;
    private Context c;
    private int level;
    private List<WeekContent> myplan;
    private int week_position;
    private Toast toast;


    //建構子
    public PlanAdapter(Context c, List<WeekContent> myplan, int position){
        this.c = c;
        this.level = myplan.get(position).getLevel();
        this.dayList = myplan.get(position).getDays();
        this.myplan = myplan;
        this.week_position = position;
        this.toast = null;
    }

    //以下都要複寫!

    //建立一個ViewHolder 顯示的事情交給他做
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //這裡要綁客製化的item.xml
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_plan, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        //把 data內容 和 介面 綁定
        //setText在這裡做

        final List<String> weekname = new ArrayList<String>(Arrays.asList("Mon","Tue","Wed","Thu","Fri","Sat","Sun"));
        final int p = i;
        final ScheduleIO fileIO = new ScheduleIO(c);
        String beginner = "跑步時間 :   " ;
        String medium = "跑步距離 :   " ;

        customViewHolder.textView_w.setText(weekname.get(i));
        customViewHolder.textView_date.setText(DatetoString(dayList.get(i).getDate(),1));

        if(dayList.get(i).getChoose()){


            if(level == 1){
                customViewHolder.textView.setText(beginner + dayList.get(i).getTime() + "   mins ");
            }
            else if(level == 2){
                customViewHolder.textView.setText(medium + dayList.get(i).getDist() + "   km ");
            }
            customViewHolder.done.setVisibility(View.VISIBLE);



            if(dayList.get(i).getComplete()){
                customViewHolder.done.setChecked(true);
            }

            customViewHolder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        myplan.get(week_position).getDays().get(p).setComplete(true);

                        if(toast == null){
                            toast = Toast.makeText(c,"已完成訓練" ,Toast.LENGTH_SHORT);
                        }
                        else{
                            toast.setText("已完成訓練");
                        }
                        toast.show();

                    }
                    else{
                        myplan.get(week_position).getDays().get(p).setComplete(false);

                        if(toast == null){
                            toast = Toast.makeText(c,":(" ,Toast.LENGTH_SHORT);
                        }
                        else{
                            toast.setText(":(");
                        }
                        toast.show();

                    }
                }
            });

        }
        else{
            customViewHolder.textView.setText("無");
            customViewHolder.done.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public List<WeekContent> getMyplan(){
        return myplan;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        //自定義的 ViewHolder，綁定item_week的 id
        //xml裡面有甚麼TextView等等的都在這裡綁定id

        protected TextView textView;
        protected TextView textView_w;
        protected TextView textView_date;
        protected CheckBox done;

        public CustomViewHolder(View view) {
            super(view);

            this.textView = (TextView) view.findViewById(R.id.DayRunContent);
            this.textView_w = (TextView) view.findViewById(R.id.Week_x);
            this.textView_date = (TextView) view.findViewById(R.id.plan_date);
            this.done = (CheckBox) view.findViewById(R.id.Day_done);


        }
    }

}