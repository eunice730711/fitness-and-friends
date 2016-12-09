package com.google.firebase.codelab.friendlychat;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JCLIN on 2016/11/28.
 */

public class Day implements Serializable {
    private int time;
    private double dist;
    private boolean choose;
    private boolean complete;
    private Date date;

    public Day() {

    }
    public Day(int time, double dist, boolean choose, boolean complete, Date date) {
        this.time = time;
        this.dist = dist;
        this.choose = choose;
        this.complete = complete;
        this.date = date;
    }
    public void setTime(int time){
        this.time = time;
    }
    public void setDist(double dist){
        this.dist = dist;
    }
    public void setChoose(boolean choose){
        this.choose = choose;
    }
    public void setComplete(boolean complete) { this.complete = complete; }
    public void setDate(Date date){ this.date = date; }

    public int getTime(){
        return this.time;
    }
    public double getDist(){
        return this.dist;
    }
    public boolean getChoose(){
        return this.choose;
    }
    public boolean getComplete() { return  this.complete; }
    public Date getDate() { return  this.date; } //直接拿到date這個變數的值

    //提供一個 把Date轉成String 的函式 (多種格式)
    static public String DatetoString(Date date,int i){
        SimpleDateFormat sdf = new SimpleDateFormat();

        if(i==0){
            sdf.applyPattern("yyyy/MM/dd");
        }
        else if(i==1){
            sdf.applyPattern("MM/dd");
        }

        String s = sdf.format(date);
        return s;
    }

    //提供一個 把傳入String轉成Date 的函式 (固定格式)
    static public Date Stringtodate(String input){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date d = new Date();

        try{
            d = sdf.parse(input);
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        return d;
    }


}