package com.google.firebase.codelab.friendlychat;

import android.content.Context;
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

public class PlanAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Day> dayList;
    private Context c;
    private int level;
    private List<WeekContent> myplan;
    private int week_position;

    private Toast toast;


    public PlanAdapter(Context c, List<WeekContent> myplan, int position){
        inflater = LayoutInflater.from(c);
        this.c = c;
        this.level = myplan.get(position).getLevel();
        this.dayList = myplan.get(position).getDays();
        this.myplan = myplan;
        this.week_position = position;

        this.toast = null;
    }
    @Override
    public int getCount() {
        return dayList.size();
    }

    @Override
    public Object getItem(int i) {
        return dayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return dayList.indexOf(getItem(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.item_plan,viewGroup,false);
        final List<String> weekname = new ArrayList<String>(Arrays.asList("Mon","Tue","Wed","Thu","Fri","Sat","Sun"));
        final int p = i;
        final ScheduleIO fileIO = new ScheduleIO(c);

        String beginner = "跑步時間 :   " ;
        String medium = "跑步距離 :   " ;
        TextView textView = (TextView) view.findViewById(R.id.runContent);
        TextView textView_w = (TextView) view.findViewById(R.id.Week_x);
        TextView textView_date = (TextView) view.findViewById(R.id.plan_date);

        CheckBox done = (CheckBox) view.findViewById(R.id.Day_done);

        textView_w.setText(weekname.get(i));
        textView_date.setText(DatetoString(dayList.get(i).getDate(),1));

        if(dayList.get(i).getChoose()){


            if(level == 1){
                textView.setText(beginner + dayList.get(i).getTime() + "   mins ");
            }
            else if(level == 2){
                textView.setText(medium + dayList.get(i).getDist() + "   km ");
            }
            done.setVisibility(View.VISIBLE);



            if(dayList.get(i).getComplete()){
                done.setChecked(true);
            }

            done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        myplan.get(week_position).getDays().get(p).setComplete(true);
                        fileIO.DeleteFile();
                        fileIO.WriteFile(myplan);

                        if(toast == null){
                            toast = Toast.makeText(c,"已完成訓練" ,Toast.LENGTH_SHORT);
                        }
                        else{
                            toast.setText("已完成訓練");
                        }
                        toast.show();

                        //Toast.makeText(c,"已完成訓練" ,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        myplan.get(week_position).getDays().get(p).setComplete(false);
                        fileIO.DeleteFile();
                        fileIO.WriteFile(myplan);

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
            done.setVisibility(View.GONE);
        }

        return view;
    }
}