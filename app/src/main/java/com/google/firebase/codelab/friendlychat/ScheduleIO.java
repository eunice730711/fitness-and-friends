package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JCLIN on 2016/11/28.
 */

public class ScheduleIO {
    private Context c;

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
                    output = output + data.get(i).getDays().get(j).getTime() + " "
                            + data.get(i).getDays().get(j).getDist() + " "
                            + data.get(i).getDays().get(j).getChoose() + " "
                            + data.get(i).getDays().get(j).getComplete() + " ";
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

        StringBuilder data = new StringBuilder();
        BufferedReader reader = null;

        List<WeekContent> list_week = new ArrayList<WeekContent>();



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
                for (int j=0; j<splittedStr.length; j+=4){
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



}