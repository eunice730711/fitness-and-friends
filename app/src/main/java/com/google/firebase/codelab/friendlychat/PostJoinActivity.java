package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.firebase.codelab.friendlychat.MainActivity.MESSAGES_CHILD;

public class PostJoinActivity extends AppCompatActivity {
    private EditText mMessageEditText;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private DatabaseReference mFirebaseDatabaseReference;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Post"));
        tabLayout.addTab(tabLayout.newTab().setText("Join"));
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mMessageEditText = (EditText)findViewById(R.id.Post_Input_content);

        Button button = (Button)findViewById(R.id.Post_Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nowDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl,nowTime,nowDate);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                mMessageEditText.setText("");
                mFirebaseDatabaseReference.push().setValue(mMessageEditText);
                mMessageEditText.getText().clear();
                startNextPage();

            }
        });

    }
    class MyPagerAdapter extends PagerAdapter {
        private int pageCount = 2;
        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return obj == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "MyPage " + (position + 1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.my_viewpager, container, false);
            container.addView(view);

            if(position==0)
            {
                TextView T_Time=(TextView)view.findViewById(R.id.textView_1);
                T_Time.setVisibility(View.INVISIBLE);
                TextView T_Pos=(TextView)view.findViewById(R.id.textView_2);
                T_Pos.setVisibility(View.INVISIBLE);
                TextView T_Type=(TextView)view.findViewById(R.id.textView_3);
                T_Type.setVisibility(View.INVISIBLE);
                EditText E_Time=(EditText)view.findViewById(R.id.Edit_Time);
                E_Time.setVisibility(View.INVISIBLE);
                EditText E_Pos=(EditText)view.findViewById(R.id.Edit_Pos);
                E_Pos.setVisibility(View.INVISIBLE);
                EditText E_Type=(EditText)view.findViewById(R.id.Edit_Type);
                E_Type.setVisibility(View.INVISIBLE);
            }
            else
            {
                TextView T_Time=(TextView)view.findViewById(R.id.textView_1);
                T_Time.setVisibility(View.VISIBLE);
                TextView T_Pos=(TextView)view.findViewById(R.id.textView_2);
                T_Pos.setVisibility(View.VISIBLE);
                TextView T_Type=(TextView)view.findViewById(R.id.textView_3);
                T_Type.setVisibility(View.VISIBLE);
                EditText E_Time=(EditText)view.findViewById(R.id.Edit_Time);
                E_Time.setVisibility(View.VISIBLE);
                EditText E_Pos=(EditText)view.findViewById(R.id.Edit_Pos);
                E_Pos.setVisibility(View.VISIBLE);
                EditText E_Type=(EditText)view.findViewById(R.id.Edit_Type);
                E_Type.setVisibility(View.VISIBLE);
            }
            //TextView title = (TextView) view.findViewById(R.id.textView_item_title);
            //title.setText("" + (position + 1));
            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }




    private void startNextPage(){
        Intent intent = new Intent();
        intent.setClass(this , MainActivity.class);
        startActivity(intent);
    }

}
