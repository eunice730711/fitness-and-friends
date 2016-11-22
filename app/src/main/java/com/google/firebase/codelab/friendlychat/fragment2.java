package com.google.firebase.codelab.friendlychat;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

public class fragment2 extends Fragment {

    private EditText mMessageEditText;
    private EditText J_time;
    private EditText J_pos;
    private EditText J_type;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.tab_fragment_2, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mMessageEditText = (EditText)returnView.findViewById(R.id.Join);
        J_time = (EditText)returnView.findViewById(R.id.Edit_Time);
        J_pos = (EditText)returnView.findViewById(R.id.Edit_Pos);
        J_type = (EditText)returnView.findViewById(R.id.Edit_Type);
        Button button = (Button)returnView.findViewById(R.id.Join_Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nowDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

                JoinMessage joinMessage = new
                        JoinMessage(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl,nowTime,nowDate,J_time.getText().toString(),J_pos.getText().toString(),J_type.getText().toString());
                mFirebaseDatabaseReference.child("Join")
                        .push().setValue(joinMessage);
                mMessageEditText.setText("");
                mFirebaseDatabaseReference.push().setValue(mMessageEditText);
                mMessageEditText.getText().clear();
                //startNextPage();

            }
        });

        return returnView;
    }
}