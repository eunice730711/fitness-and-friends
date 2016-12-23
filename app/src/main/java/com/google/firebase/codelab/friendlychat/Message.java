package com.google.firebase.codelab.friendlychat;

/**
 * Created by pei on 2016/12/19.
 */

public class Message {
    private String name;
    public String userid;
    private String photoUrl;
    private String content;
    private String mtime;
    private String mdate;


    public Message() {
    }
    public Message(String name, String photoUrl, String userid, String content, String mtime, String mdate) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.content = content;
        this.mtime = mtime;
        this.mdate = mdate;
        this.userid = userid;
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

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

}
