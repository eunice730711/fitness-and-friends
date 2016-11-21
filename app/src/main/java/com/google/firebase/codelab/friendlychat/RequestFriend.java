package com.google.firebase.codelab.friendlychat;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/21.
 */

public class RequestFriend {

    public String user;
    //public ArrayList<String> requester;
    public String requester;

    public RequestFriend(){
        //requester = new ArrayList<String>();
    }
    public RequestFriend(String user, String requester){
        this.user = user;
        this.requester = requester;
    }
    public String getUser(){ return this.user;}

    public String getRequester(){ return  this.requester;}

}
