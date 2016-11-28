package com.google.firebase.codelab.friendlychat;

/**
 * Created by pei on 2016/11/15.
 */

public class JoinMessage {
    private String text;
    private String name;
    private String photoUrl;
    private String time;
    private String date;
    private String Jdate;
    private String Jtime;
    private String Jpos;
    private String Jtype;

    public JoinMessage(){
    }

    public JoinMessage(String text, String name, String photoUrl, String time, String date, String j_date, String j_time, String j_pos, String j_type) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.time = time;
        this.date = date;
        this.Jdate = j_date;
        this.Jtime = j_time;
        this.Jpos = j_pos;
        this.Jtype = j_type;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {return date;}

    public void setDate(String date) {
        this.date = date;
    }

    public String getJdate() { return Jdate; }

    public void setJdate(String J_date) {
        this.Jdate = J_date;
    }

    public String getJtime() { return Jtime; }

    public void setJtime(String J_time) {
        this.Jtime = J_time;
    }

    public String getJpos() {
        return Jpos;
    }

    public void setJpos(String J_pos) { this.Jpos = J_pos; }

    public String getJtype() {
        return Jtype;
    }

    public void setJtype(String J_type) { this.Jtype = J_type; }



}
