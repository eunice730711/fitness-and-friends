package com.google.firebase.codelab.friendlychat;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by user on 2016/12/15.
 */

public class SendNotification extends Thread{
    final String key = "AAAA_0pqRw8:APA91bF2ac8XTkE3pA1KdMemwKv-Jv5-9t77x_3HbgbXrl2erDv-rXBqAffb8xGO72lD2r-q2cx-2oDc-iwfCEuUmMSI2kpyhncKTUnayYzCQHVY0eKHkNBfNkMSVztLKtgJCRaiK2AIkq5zpWPUANFZ_9JFU-nbiQ";
    final String address = "https://fcm.googleapis.com/fcm/send";
    private int Case;
    private String userId, friendid;
    public void setupFriend(String userId, String friendId){
        this.Case = 1;
        this.userId = userId;
        this.friendid = friendId;
    }

    public void subscribeFriend(String userid){
        String topic = "friend_";
        FirebaseMessaging.getInstance().subscribeToTopic(topic+userid);
    }

    public String sendFriendNotification()
            throws IOException, JSONException {
        URL url = new URL(address);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true); // 可寫入資料至伺服器

        // HTTP request header

        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization","key="+key);
        con.setRequestMethod("POST");

        con.connect();
        // HTTP request
        JSONObject data = new JSONObject();
        JSONObject nestedData = new JSONObject();
        data.put("to", "/topics/friend_"+this.friendid);
        //data.put("notification","Friend request");
        nestedData.put("message",this.userId+" sends a friend request");
        nestedData.put("type","friend");
        data.put("data",nestedData);

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        int status = con.getResponseCode();
        InputStream is;
        if (status != HttpURLConnection.HTTP_OK)
            is = con.getErrorStream();
        else
            is = con.getInputStream();
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();

        is.close();
        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        con.disconnect();
        return response.getString("message_id");

    }

    public void run(){
        // 運行網路連線的程式
        if (this.Case ==1){
            try {
                String s =sendFriendNotification();
                Log.d("request",s);
            }catch (Exception e){
                Log.e("text", "Http_Client.changeInputStream.IOException="+e.toString());
            }
        }
    }


}
