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
}