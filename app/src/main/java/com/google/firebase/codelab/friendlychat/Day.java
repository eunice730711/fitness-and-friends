package com.google.firebase.codelab.friendlychat;

import java.io.Serializable;

/**
 * Created by JCLIN on 2016/11/28.
 */

public class Day implements Serializable {
    private int time;
    private double dist;
    private boolean choose;
    private boolean complete;

    public Day() {

    }
    public Day(int time, double dist, boolean choose, boolean complete) {
        this.time = time;
        this.dist = dist;
        this.choose = choose;
        this.complete = complete;
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
}