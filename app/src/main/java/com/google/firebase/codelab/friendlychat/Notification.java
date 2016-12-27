package com.google.firebase.codelab.friendlychat;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.value;
import static android.os.Build.VERSION_CODES.M;
import static com.google.firebase.codelab.friendlychat.R.id.webView;


public class Notification extends AppCompatActivity {
    public DatabaseReference mDatabase;
    public UserProfile userProfile;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Object, RequestViewHolder>
            mFirebaseAdapter;
    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_request;
        public Button btn_accept, btn_refect;
        public CircleImageView requestImageView;
        public UserProfile userProfile;
        public String requester;
        public RequestViewHolder( View v) {
            super(v);
            txt_request = (TextView) itemView.findViewById(R.id.txt_request);
            btn_accept = (Button) itemView.findViewById(R.id.btn_accept);
            btn_refect = (Button) itemView.findViewById(R.id.btn_reject);
            requestImageView = (CircleImageView) itemView.findViewById(R.id.requestImageView);

            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    int index = txt_request.getText().toString().indexOf("送")-1;
                    requester = txt_request.getText().subSequence(0,index).toString();
                    HashMap<String, String> friend = new HashMap<String, String>();
                    HashMap<String, String> friend2 = new HashMap<String, String>();

                    friend.put("friendid",requester);
                    friend2.put("friendid",userProfile.getUserid());
                    // 對好友資料庫進行寫入，建立兩人好友關係
                    mDatabase.child("Friend").child(userProfile.getUserid()).getRef().push().setValue(friend);
                    mDatabase.child("Friend").child(requester).getRef().push().setValue(friend2);
                    // 刪除好友邀請
                    deleteRequester();
                    Toast.makeText(v.getContext(), "恭喜你們已成為好友", Toast.LENGTH_LONG).show();

                }
            });

            btn_refect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 刪除好友邀請
                    deleteRequester();
                }
            });
        }
        public void deleteRequester(){
            int index = txt_request.getText().toString().indexOf("送")-1;
            requester = txt_request.getText().subSequence(0,index).toString();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            // 列出所有對該用戶所發出的好友通知
            mDatabase.child("RequestFriend").child(userProfile.getUserid()).getRef().
                    orderByChild("requester").equalTo(requester).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Object o = dataSnapshot.getValue(Object.class);
                        String id = ((HashMap<String, String>) o).get("requester");
                        // 找到viewholder對應的 requester
                        dataSnapshot.getRef().removeValue();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO(Notification.this);
        userProfile = profileIO.ReadFile();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = (RecyclerView) findViewById(R.id.notificationRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        // 顯示最新資訊，由上而下
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        showNotification();

    }


    private void showNotification(){
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // 存取通知資料庫，並且顯示通知出來

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Object,
                RequestViewHolder>(
                Object.class,
                R.layout.item_request_friend,
                RequestViewHolder.class,
                mFirebaseDatabaseReference.child("RequestFriend").child(userProfile.getUserid()).orderByChild("requester")) {

            @Override
            protected void populateViewHolder(RequestViewHolder viewHolder,
                                              Object o , int position) {
                String requester = ((HashMap)o).get("requester").toString();
                viewHolder.txt_request.setText(requester + " 送出好友邀請");
                viewHolder.requester = requester;
                viewHolder.userProfile = userProfile;
            }
        };

/*
        WebView mWebView = (WebView)findViewById(R.id.webView);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://140.113.193.71/FitnessFriend/test.html");
*/
/*

        ArrayList l = new ArrayList();
        Map m1 = new HashMap();
        Map m2 = new HashMap();
        m1.put("requester","r1");
        m2.put("requester","r2");
        l.add(m1);
        l.add(m2);

        JsAdapter adapter = new JsAdapter(l){
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // 設定viewHolder 所使用的 layout
                View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_friend, parent, false);
               RequestViewHolder vh = new RequestViewHolder(mView);
                return vh;
            }
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
               RequestViewHolder viewHolder = (RequestViewHolder) holder;

                String requester = ((HashMap)iList.get(position)).get("requester").toString();
                viewHolder.txt_request.setText(requester + " 送出好友邀請");
                viewHolder.requester = requester;
                viewHolder.userProfile = userProfile;
            }
        };
*/
        //adapter.setOnRecyclerViewListener(this);
        //mRecyclerView.setAdapter(adapter);
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }



}


