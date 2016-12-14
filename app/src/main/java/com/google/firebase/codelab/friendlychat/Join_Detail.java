package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class Join_Detail extends AppCompatActivity {

    private JoinMessage join;
    private TextView Name;
    private TextView Date;
    private TextView Time;
    private TextView Text;
    private TextView Title;
    private TextView J_Time;
    private TextView J_Date;
    private TextView J_Pos;
    private CircleImageView Photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_detail);
        Intent intent = this.getIntent();
        join = (JoinMessage) intent.getSerializableExtra("join");

        Name=(TextView)findViewById(R.id.Detail_Name);
        Date=(TextView)findViewById(R.id.Detail_Date);
        Time=(TextView)findViewById(R.id.Detail_Time);
        Text=(TextView)findViewById(R.id.Detail_Text);
        Photo =(CircleImageView)findViewById(R.id.Detail_photo);
        Title=(TextView)findViewById(R.id.Detail_Title);
        J_Date=(TextView)findViewById(R.id.Detail_JDate);
        J_Time=(TextView)findViewById(R.id.Detail_JTime);
        J_Pos=(TextView)findViewById(R.id.Detail_JPos);

        Name.setText(join.getName());
        Date.setText(join.getDate());
        Time.setText(join.getTime());
        Text.setText(join.getText());
        Title.setText(join.getTitle());
        J_Date.setText(join.getJdate());
        J_Time.setText(join.getJtime());
        J_Pos.setText(join.getJpos());
        Glide.with(Join_Detail.this)
                .load(join.getPhotoUrl())
                .into(Photo);
    }
}
