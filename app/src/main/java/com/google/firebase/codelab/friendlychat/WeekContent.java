package com.google.firebase.codelab.friendlychat;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JCLIN on 2016/11/28.
 */

public class WeekContent implements Serializable {
    private List<Day> days;
    private int week;
    private int level;

    public WeekContent() {

    }

    public WeekContent(List<Day> days, int week, int level) {
        this.days = days;
        this.week = week;
        this.level = level;
    }

    public void setDays(List<Day> days){
        this.days = days;
    }

    public void setWeek(int week){
        this.week = week;
    }

    public void setLevel(int level) { this.level = level; }

    public List<Day> getDays(){
        return this.days;
    }

    public Day getDaysItem(int i){
        return this.days.get(i);
    }

    public int getWeek(){
        return this.week;
    }

    public int getLevel() { return this.level; }

    public int getDayCount(){
        int count =0;
        for(int i=0;i<days.size();i++){
            if(days.get(i).getChoose() ) count++;
        }
        return count;
    }
}