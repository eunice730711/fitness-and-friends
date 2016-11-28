package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.firebase.codelab.friendlychat.MainActivity.MESSAGES_CHILD;

/**
 * Created by pei on 2016/11/15.
 */

public class fragment1 extends Fragment {

    private EditText mMessageEditText;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.tab_fragment_1, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mMessageEditText = (EditText)returnView.findViewById(R.id.Post);
        Button button = (Button)returnView.findViewById(R.id.Post_Button);
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

        return returnView;
    }
    private void startNextPage(){
        Intent intent = new Intent();
        intent.setClass(getActivity() , MainActivity.class);
        startActivity(intent);
    }


}
