package com.google.firebase.codelab.friendlychat;

/**
 * Created by user on 2016/11/18.
 */

public class UserProfile {
    public String username;
    public String userid;
    public String instanceid;
    public UserProfile(){}

    public UserProfile(String username, String userid, String instanceid){
        this.username = username;
        this.userid = userid;
        this.instanceid = instanceid;
    }

    public String getUsername(){return username;}

    public void setUsername(String username){this.username = username;}

    public String getUserid(){return userid;}

    public void setId(String userid){this.userid = userid;}

    public  String getInstanceid(){return  instanceid;}

    public void setInstanceid(String instanceid){this.instanceid = instanceid;}


}