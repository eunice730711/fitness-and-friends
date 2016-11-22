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
    private String J_time;
    private String J_pos;
    private String J_type;

    public JoinMessage(){
    }

    public JoinMessage(String text, String name, String photoUrl, String time, String date, String j_time, String j_pos, String j_type) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.time = time;
        this.date = date;
        this.J_time = j_time;
        this.J_pos = j_pos;
        this.J_type = j_type;
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

    public void setPTime(String time) {
        this.time = time;
    }

    public String getDate() {return date;}

    public void setDate(String date) {
        this.date = date;
    }

    public String getJ_time() {
        return J_time;
    }

    public void setJ_time(String J_time) {
        this.J_time = J_time;
    }

    public String getJ_pos() {
        return J_pos;
    }

    public void setJ_pos(String J_pos) {
        this.J_pos = J_pos;
    }

    public String getJ_type() {
        return J_type;
    }

    public void setJ_type(String J_type) {
        this.J_pos = J_type;
    }



}
