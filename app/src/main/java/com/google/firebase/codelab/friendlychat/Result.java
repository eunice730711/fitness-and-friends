package com.google.firebase.codelab.friendlychat;

import java.io.Serializable;

/**
 * Created by Andersen on 2016/11/17.
 */

public class Result implements Serializable {
    private int type;
    private int level;
    private int distance;
    private String days;

    public Result(){
        this.type = 0;
        this.level = 0;
        this.distance = 0;
        this.days = "";
    };

    public void setType(int type){
        this.type = type;
    };

    public void setLevel(int level){
        this.level = level;
    };

    public  void setDistance(int distance){
        this.distance = distance;
    };

    public void setDays(String days){
        this.days = days;
    };

    public int getType() {return this.type;};

    public int getLevel() {return this.level;};

    public int getDistance() {return this.distance;};

    public String getDays() {return this.days;};

    public int countDays(){
        int count=0;
        if(days == "") return 0;
        for(int i=0; i<days.length(); i++){
            if(days.charAt(i)=='1') count+=1;
        }
        return count;
    };
}