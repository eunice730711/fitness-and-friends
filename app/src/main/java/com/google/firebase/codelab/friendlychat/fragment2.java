package com.google.firebase.codelab.friendlychat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class fragment2 extends Fragment {

    private DatePickerDialog date_pick;
    private TimePickerDialog time_pick;

    private EditText mMessageEditText;
    private TextView J_date;
    private TextView J_time;
    private EditText J_pos;
    private EditText J_type;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private DatabaseReference mFirebaseDatabaseReference;
    private UserProfile userProfile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.tab_fragment_2, container, false);
        GregorianCalendar calendar = new GregorianCalendar();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = mFirebaseUser.getDisplayName();
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO( getActivity());
        userProfile = profileIO.ReadFile();

        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        J_date = (TextView)returnView.findViewById(R.id.Edit_Date);
        date_pick = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                J_date.setText(year + "/");
                if((monthOfYear+1) < 10) J_date.append("0"+(monthOfYear+1)+"/");
                else J_date.append((monthOfYear+1)+"/");
                if(dayOfMonth < 10) J_date.append("0"+dayOfMonth);
                else J_date.append(dayOfMonth+"");
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        J_time = (TextView) returnView.findViewById(R.id.Edit_Time);
        time_pick = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(hourOfDay < 10) J_time.setText("0"+hourOfDay+":");
                else J_time.setText(hourOfDay+":");
                if(minute < 10) J_time.append("0"+minute);
                else J_time.append(minute+"");
                // J_time.setText((hourOfDay > 12 ? hourOfDay- 12 :hourOfDay) + ":" +minute + " "+(hourOfDay > 12 ? "PM": "AM"));
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),
                false);


        mMessageEditText = (EditText)returnView.findViewById(R.id.Join);

        J_pos = (EditText)returnView.findViewById(R.id.Edit_Pos);
        J_type = (EditText)returnView.findViewById(R.id.Edit_Type);
        Button b_date = (Button)returnView.findViewById(R.id.set_date);
        b_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_pick.show();
            }
        });
        Button b_time = (Button)returnView.findViewById(R.id.set_time);
        b_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_pick.show();
            }
        });
        Button button = (Button)returnView.findViewById(R.id.Join_Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nowDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

                JoinMessage joinMessage = new
                        JoinMessage(userProfile.getUserid(), mMessageEditText.getText().toString(),
                        mUsername, mPhotoUrl,nowTime,nowDate,J_date.getText().toString(),
                        J_time.getText().toString(),J_pos.getText().toString(),J_type.getText().toString());
                mFirebaseDatabaseReference.child("Join")
                        .push().setValue(joinMessage);
                mMessageEditText.setText("");
                mFirebaseDatabaseReference.push().setValue(mMessageEditText);
                mMessageEditText.getText().clear();
                startNextPage();

            }
        });

        return returnView;
    }
    private void startNextPage(){
        Intent intent = new Intent();
        intent.setClass(getActivity() , MainActivity.class);
        startActivity(intent);
    }
}