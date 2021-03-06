package com.google.firebase.codelab.friendlychat;

/**
 * Created by user on 2016/11/18.
 */

public class UserProfile {
    public String username;
    public String userid;
    public String instanceid;
    public double totaldistance;
    public String usercity;
    public String usergender;
    public String userbirthday;
    public String selfintroduction;
    public String useremail;
    public String userphoto;
    public String userref;
    public String userheight;
    public String userweight;


    public UserProfile(){}

    public UserProfile(String username, String userid, String instanceid, String usercity, String userbirthday, String usergender, String selfintroduction, String useremail, String photo, String userref, String userheight, String userweight){
        this.username = username;
        this.userid = userid;
        this.instanceid = instanceid;
        this.totaldistance = 0;
        this.usercity = usercity;
        this.userbirthday = userbirthday;
        this.usergender = usergender;
        this.selfintroduction = selfintroduction;
        this.useremail = useremail;
        this.userphoto = photo;
        this.userref = userref;
        this.userheight = userheight;
        this.userweight = userweight;
    }

    public String getUsername(){return username;}

    public void setUsername(String username){this.username = username;}

    public String getUserid(){return userid;}

    public void setUserid(String userid){this.userid = userid;}

    public  String getInstanceid(){return  instanceid;}

    public void setInstanceid(String instanceid){this.instanceid = instanceid;}

    public void setUserref(String userref){this.userref = userref;}

    public double updateDistance(double oneDistance){ this.totaldistance += oneDistance; return this.totaldistance;}

    public double getTotaldistance(){return totaldistance;}

    public String getUsergender(){return usergender;}

    public String getUsercity(){return usercity;}

    public String getUserbirthday(){return userbirthday;}

    public String getSelfintroduction(){return selfintroduction;}

    public String getUseremail(){return useremail;}

    public String getUserphoto(){return userphoto;}

    public String getUserref(){return userref;}

    public String getUserheight(){return userheight;}

    public String getUserweight(){return userweight;}


}
