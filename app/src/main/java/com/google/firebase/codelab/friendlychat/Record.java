package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

public class Record extends AppCompatActivity {
    public DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    public UserProfile userProfile;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Object, RecordViewHolder>
            mFirebaseAdapter;
    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_dist, txt_time, txt_date, txt_rate;

        public RecordViewHolder(View v) {
            super(v);
            txt_dist = (TextView)itemView.findViewById(R.id.txt_recordDist);
            txt_time = (TextView)itemView.findViewById(R.id.txt_recordTime);
            txt_date = (TextView)itemView.findViewById(R.id.txt_recordDate);
            txt_rate = (TextView)itemView.findViewById(R.id.txt_recordRate);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO(Record.this);
        userProfile = profileIO.ReadFile();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = (RecyclerView) findViewById(R.id.recordRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        // 顯示最新資訊，由上而下
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        showRecord();
    }

    private void showRecord(){
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Object,
                RecordViewHolder>(
                Object.class,
                R.layout.item_record,
                RecordViewHolder.class,
                mDatabase.child("Record").child(userProfile.getUserid()).orderByChild("date")) {

            @Override
            protected void populateViewHolder(RecordViewHolder viewHolder,
                                              Object o , int position) {
                DecimalFormat formatter = new DecimalFormat("###.00");//小數後兩位

                String date = ((HashMap)o).get("date").toString();
                String time = ((HashMap)o).get("time").toString();
                String dist = ((HashMap)o).get("distance").toString();
                String rate = null;
                viewHolder.txt_date.setText(date);
                viewHolder.txt_dist.setText((formatter.format(Double.valueOf(dist))).toString());
                viewHolder.txt_time.setText(time+"minutes");

                if(dist.compareTo("0.0")!=0)
                    rate = String.valueOf(formatter.format(Double.valueOf(time)/Double.valueOf(dist))+" mins/km");
                else
                    rate = "0.00"+" mins/km";
                viewHolder.txt_rate.setText(rate);
            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

}