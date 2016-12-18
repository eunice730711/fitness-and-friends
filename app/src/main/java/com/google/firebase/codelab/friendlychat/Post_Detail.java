package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class Post_Detail extends AppCompatActivity {

    private PostMessage post;
    private TextView Name;
    private TextView Date;
    private TextView Time;
    private TextView Text;
    private CircleImageView Photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent intent = this.getIntent();
        post = (PostMessage) intent.getSerializableExtra("post");

        Name=(TextView)findViewById(R.id.Detail_Name);
        Date=(TextView)findViewById(R.id.Detail_Date);
        Time=(TextView)findViewById(R.id.Detail_Time);
        Text=(TextView)findViewById(R.id.Detail_Text);
        Photo =(CircleImageView)findViewById(R.id.Detail_photo);



        Name.setText(post.getName());
        Date.setText(post.getDate());
        Time.setText(post.getTime());
        Text.setText(post.getText());
        Glide.with(Post_Detail.this)
                .load(post.getPhotoUrl())
                .into(Photo);
    }
}
