package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;


/**
 * Created by JCLIN on 2016/12/8.
 */

public class ProfileIO {

    private Context c;

    public ProfileIO(){}

    public ProfileIO(Context c) {
        this.c = c;
    }

    public void WriteFile(UserProfile data){
        File dir = c.getFilesDir();
        File outFile = new File(dir, "userprofile.txt");
        String output = "";

        FileOutputStream osw = null;
        try {
            osw = new FileOutputStream(outFile);

            output = output
                    + data.getTotaldistance() + "\n"
                    + data.getUsername() + "\n"
                    + data.getUserid() + "\n"
                    + data.getInstanceid() + "\n"
                    + data.getUsercity() + "\n"
                    + data.getUsergender() + "\n"
                    + data.getUserbirthday() + "\n"
                    + data.getSelfintroduction() + "\n"
                    + data.getUseremail() + "\n"
                    + data.getUserphoto() + "\n";

            osw.write(output.getBytes());
            osw.flush();
            //Toast.makeText(c, "successful", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(c, "failed"+e.toString(), Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                osw.close();
                //Toast.makeText(c, "finish", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                Toast.makeText(c, "finish error" + e.toString(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    public UserProfile ReadFile(){
        File dir = c.getFilesDir();
        File inFile = new File(dir, "userprofile.txt");

        BufferedReader reader = null;

        UserProfile u = new UserProfile();

        try {

            if(!inFile.exists()){
                Toast.makeText(c, "File does not exist.", Toast.LENGTH_SHORT).show();
                return  null;
            }
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "utf-8"));

            double dist = Double.valueOf(reader.readLine());
            String name = reader.readLine();
            String id = reader.readLine();
            String instanceid = reader.readLine();
            String city = reader.readLine();
            String gender = reader.readLine();
            String birthday = reader.readLine();
            String intro = reader.readLine();
            String email = reader.readLine();
            String photo = reader.readLine();

            UserProfile userProfile = new UserProfile(name,id,instanceid,city,birthday,gender,intro,email,photo);
            userProfile.updateDistance(dist);

            u = userProfile;
        }
        catch (Exception e) {

        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {

            }
        }

        return u;
    }

    public void DeleteFile(){
        c.deleteFile("userprofile.txt");
    }


}
