package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.google.firebase.codelab.friendlychat.Day.DatetoString;
import static com.google.firebase.codelab.friendlychat.Day.Stringtodate;

/**
 * Created by JCLIN on 2016/11/28.
 */

public class ScheduleIO {
    private Context c;

    public ScheduleIO(){}

    public ScheduleIO(Context c) {
        this.c = c;
    }

    public void WriteFile(List<WeekContent> data){
        File dir = c.getFilesDir();
        File outFile = new File(dir, "schedule.txt");
        String output = "";


        FileOutputStream osw = null;
        try {
            osw = new FileOutputStream(outFile);

            output = output + data.size() + "\n";

            for(int i=0 ; i<data.size() ;i++){
                output = output +data.get(i).getLevel() + "\n";
            }

            for(int i = 0; i<data.size() ; i++){
                for(int j = 0 ; j<data.get(i).getDays().size() ; j++){
                    String outputDate = DatetoString(data.get(i).getDays().get(j).getDate(),0);
                    output = output
                            + data.get(i).getDays().get(j).getTime() + " "
                            + data.get(i).getDays().get(j).getDist() + " "
                            + data.get(i).getDays().get(j).getChoose() + " "
                            + data.get(i).getDays().get(j).getComplete() + " "
                            + outputDate + " ";
                }
                output += "\n";
            }

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
    public List<WeekContent> ReadFile(){
        File dir = c.getFilesDir();
        File inFile = new File(dir, "schedule.txt");

        List<WeekContent> list_week = new ArrayList<WeekContent>();

        if(!inFile.exists()){
            Toast.makeText(c, "File does not exist.", Toast.LENGTH_SHORT).show();
            return  list_week;
        }

        StringBuilder data = new StringBuilder();
        BufferedReader reader = null;



        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "utf-8"));

            int num = Integer.valueOf(reader.readLine());
            List<Integer> list_level = new ArrayList<Integer>();

            for(int i=0; i<num ; i++){
                list_level.add(Integer.valueOf(reader.readLine()));
            }

            for(int i=0 ; i<num ; i++){
                String line = reader.readLine();

                WeekContent w = new WeekContent();
                List<Day> list_day = new ArrayList<Day>();

                w.setLevel(list_level.get(i));
                w.setWeek(i+1);

                String[] splittedStr = line.split(" ");
                for (int j=0; j<splittedStr.length; j+=5){
                    Day day = new Day();
                    day.setTime(Integer.valueOf(splittedStr[j]));
                    day.setDist(Double.valueOf(splittedStr[j+1]));
                    if(splittedStr[j+2].equals("true")){
                        day.setChoose(true);
                    }
                    else{
                        day.setChoose(false);
                    }
                    if(splittedStr[j+3].equals("true")){
                        day.setComplete(true);
                    }
                    else{
                        day.setComplete(false);
                    }
                    day.setDate(Stringtodate(splittedStr[j+4]));

                    list_day.add(day);
                }
                w.setDays(list_day);
                list_week.add(w);
            }

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
        return list_week;
    }

    public void DeleteFile(){
        c.deleteFile("schedule.txt");
    }

    public interface IsNewPush {
        void SetNewPush(UserSchedule userSchedule,DatabaseReference mFirebaseDatabaseReference);
    }

    public void QueryFirebase(List<WeekContent> data,final IsNewPush callback){

        final UserSchedule userSchedule = new UserSchedule();

        //firebase初始化
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //user 的schedule 初始化
        userSchedule.setGoogleEmail(mFirebaseUser.getEmail());
        userSchedule.setPlan(data);

        //找firebase中的email節點
        mFirebaseDatabaseReference.child("UserSchedule").orderByChild("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                boolean newpush = true;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Object o  = dataSnapshot.getValue(Object.class);
                    String email = ((HashMap<String,String>)o).get("googleEmail");
                    if( email.equals(userSchedule.getGoogleEmail())) {
                        dataSnapshot.getRef().setValue(userSchedule);
                        newpush = false;
                        break;
                    }
                }
                if(newpush){
                    snapshot.getRef().push().setValue(userSchedule);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }


    public void WriteFirebase(List<WeekContent> data){

        QueryFirebase(data,
                new IsNewPush() {
                    @Override
                    public void SetNewPush(UserSchedule userSchedule,DatabaseReference mFirebaseDatabaseReference) {
                        mFirebaseDatabaseReference.push().setValue(userSchedule);

                    }
                });


//        //-------ok method
//        final UserSchedule userSchedule = new UserSchedule();
//
//        //firebase初始化
//        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//
//        //user 的schedule 初始化
//        userSchedule.setGoogleEmail(mFirebaseUser.getEmail());
//        userSchedule.setPlan(data);
//
//        //找firebase中的email節點
//        mFirebaseDatabaseReference.child("UserSchedule").orderByChild("email").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//
//                boolean newpush = true;
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Object o  = dataSnapshot.getValue(Object.class);
//                    String email = ((HashMap<String,String>)o).get("googleEmail");
//                    if( email.equals(userSchedule.getGoogleEmail())) {
//                        dataSnapshot.getRef().setValue(userSchedule);
//                        newpush = false;
//                        break;
//                    }
//                }
//                if(newpush){
//                    snapshot.getRef().push().setValue(userSchedule);
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
//        //-------ok method

    }

    public Day TodayJob(){

        List<WeekContent> list_week = new ArrayList<WeekContent>();
        list_week = ReadFile();

        if(list_week.size()!=0){
            Calendar calendar = Calendar.getInstance();

            for(int i=0; i<list_week.size(); i++){
                for(int j=0; j<list_week.get(i).getDays().size(); j++){

                    String date = DatetoString(list_week.get(i).getDays().get(j).getDate(),0);
                    String now = DatetoString(calendar.getTime(),0);
                    if(date.equals(now)){
                        return list_week.get(i).getDays().get(j);
                    }
                }
            }
        }
        return null;
    }


    public void UpdateComplete(){
        List<WeekContent> list_week = null;
        list_week = ReadFile();

        Calendar calendar = Calendar.getInstance();

        for(int i=0; i<list_week.size(); i++){
            for(int j=0; j<list_week.get(i).getDays().size(); j++){

                String date = DatetoString(list_week.get(i).getDays().get(j).getDate(),0);
                String now = DatetoString(calendar.getTime(),0);
                if(date.compareTo(now)==0){
                    list_week.get(i).getDays().get(j).setComplete(true);
                }
            }
        }
        WriteFile(list_week);
    }

    public boolean IsComplete(double time, double dist){
        Day day = TodayJob();
        if(day.getDist() !=0){
            if( dist >= day.getDist()){
                // finish the today job
                UpdateComplete();
                return true;
            }
            else{
                return false;
            }
        }
        else if(day.getTime() !=0){
            if( time >= day.getTime()){
                UpdateComplete();
                return true;
            }
            else{
                return false;
            }
        }
        else
            return true;
    }

}