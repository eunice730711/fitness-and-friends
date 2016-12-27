package com.google.firebase.codelab.friendlychat;

/**
 * Created by pei on 2016/12/21.
 */

public class Member {
    private String userid;
    private String name;
    private String photoUrl;


    public Member() {}

    public Member(String userid, String name, String photoUrl) {
        this.userid = userid;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
