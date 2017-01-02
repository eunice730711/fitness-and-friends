package com.google.firebase.codelab.friendlychat;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;



/**
 * Created by pei on 2016/11/23.
 */

public class Post_fragment extends Fragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public TextView mdateTextView;
        public TextView mtimeTextView;
        public CircleImageView messengerImageView;
        public static View P_view;

        public PostViewHolder(View v) {
            super(v);
            P_view = v;
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            mdateTextView = (TextView) itemView.findViewById(R.id.mdateTextView);
            mtimeTextView = (TextView) itemView.findViewById(R.id.mtimeTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }

    }
    private UserProfile userProfile;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<PostMessage,PostViewHolder> mFirebaseAdapter;
    private List<String> FriendIdList;
    private List<DatabaseReference>RefList;
    private ArrayList<PostMessage> FriendPost_List;
    private final int limit_post_number = 30;
    public JsAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = mFirebaseUser.getDisplayName();

        mMessageRecyclerView = (RecyclerView) v.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO( getActivity());
        userProfile = profileIO.ReadFile();

        mMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseDatabaseReference.child("Friend").child(userProfile.getUserid()).addValueEventListener(new ValueEventListener() {
            @Override
            // 取得好友列表
            public void onDataChange(DataSnapshot snapshot) {
                FriendIdList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HashMap map = (HashMap<String, String>) dataSnapshot.getValue(Object.class);
                    FriendIdList.add(map.get("friendid").toString());
                }
                // 讓自己的po文也可以在塗鴉牆顯示
                FriendIdList.add(userProfile.getUserid());
                //  搜尋好友的文章
                mFirebaseDatabaseReference.child("messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        FriendPost_List = new ArrayList<>();
                        RefList = new ArrayList<>();
                        // 限定只顯示15筆資料
                        int limit_number = limit_post_number;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if( limit_number ==0)
                                break;
                            PostMessage postMessage =  dataSnapshot.getValue(PostMessage.class);
                            String id = postMessage.getId();
                            if( FriendIdList.indexOf(id)!=-1){
                                limit_number --;
                                FriendPost_List.add(postMessage);
                                RefList.add(dataSnapshot.getRef());
                            }
                        }
                    // 完成搜尋好友文章
                        adapter = new JsAdapter(FriendPost_List){
                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                // 設定viewHolder 所使用的 layout
                                View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_postmessage, parent, false);
                                PostViewHolder vh = new PostViewHolder(mView);
                                return vh;
                            }
                            @Override
                            public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {
                                PostViewHolder viewHolder = (PostViewHolder) holder;

                                PostMessage postMessage = (PostMessage)iList.get(position);
                                viewHolder.messageTextView.setText(postMessage.getText());
                                viewHolder.messengerTextView.setText(postMessage.getName());
                                viewHolder.mdateTextView.setText(postMessage.getDate());
                                viewHolder.mtimeTextView.setText(postMessage.getTime());
                                if (postMessage.getPhotoUrl() == null) {
                                    viewHolder.messengerImageView
                                            .setImageDrawable(ContextCompat
                                                    .getDrawable(getActivity(),
                                                            R.drawable.ic_account_circle_black_36dp));
                                } else {
                                    Glide.with(getActivity())
                                            .load(postMessage.getPhotoUrl())
                                            .into(viewHolder.messengerImageView);
                                }
                            }
                        };
                        mMessageRecyclerView.setAdapter(adapter);
                        mLinearLayoutManager.setStackFromEnd(true);
                        mLinearLayoutManager.setReverseLayout(true);
                        mLinearLayoutManager.setAutoMeasureEnabled(false);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        // 原先版本 沒有過濾朋友機制
/*
        mFirebaseAdapter = new FirebaseRecyclerAdapter<PostMessage, PostViewHolder>(
                PostMessage.class,
                R.layout.item_postmessage,
                PostViewHolder.class,
                mFirebaseDatabaseReference.child("messages").limitToLast(15)) {

            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder,
                                              final PostMessage postMessage, final int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(postMessage.getText());
                viewHolder.messengerTextView.setText(postMessage.getName());
                viewHolder.mdateTextView.setText(postMessage.getDate());
                viewHolder.mtimeTextView.setText(postMessage.getTime());
                if (postMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(getActivity(),
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(getActivity())
                            .load(postMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
//                viewHolder.P_view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {}
//                });
            }
        };
*/

        mMessageRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    //按一下進入detail
                    @Override
                    public void onItemClick(View view, int position) {
                        //p = mFirebaseAdapter.getItem(position);
                        String ref = RefList.get(position).toString();
                        // 原先版本 沒有過濾朋友機制
                        ///String ref= mFirebaseAdapter.getRef(position).getRef().toString();
                        Intent intent = new Intent();
                        intent.setClass(getActivity() , Post_Detail.class);
                        intent.putExtra("ref",ref);
                        //Bundle bundle = new Bundle();
                        //bundle.putSerializable("post",p);
                        //intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    //長按來編輯貼文
                    @Override
                    public void onLongClick(View view, final int position) {
                        PostMessage post = (PostMessage) adapter.getItem(position);
                        // 原先版本 沒有過濾朋友機制
                        //PostMessage post = mFirebaseAdapter.getItem(position);
                        if (mUsername.equals(post.getName())) {
                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                            final View v = inflater.inflate(R.layout.edit_post, null);
                            final EditText editText = (EditText)(v.findViewById(R.id.e_PText));
                            final EditText editTitle = (EditText)(v.findViewById(R.id.e_PTitle));
                            editText.setText(post.getText());
                            editTitle.setText(post.getTitle());
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Edit Your Post")
                                    .setView(v)
                                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getContext(), "successfully changed", Toast.LENGTH_SHORT).show();
                                            //viewHolder.messageTextView.setText(editText.getText().toString());
                                            Map<String, Object> updateMap = new HashMap<String, Object>();
                                            updateMap.put("text", editText.getText().toString());
                                            updateMap.put("title", editTitle.getText().toString());
                                            // 原先版本 沒有過濾朋友機制
                                            //mFirebaseAdapter.getRef(position).updateChildren(updateMap);
                                            RefList.get(position).updateChildren(updateMap);
                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            // TODO Auto-generated method stub
                                            Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("Remove", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            RefList.get(position).removeValue();

                                            adapter.notifyItemRemoved(position);
                                            adapter.notifyDataSetChanged();
                                            Log.e("number",String.valueOf(adapter.getItemCount()));
                                            adapter.notifyItemRangeChanged(position,adapter.getItemCount());

                                            // 原先版本 沒有過濾朋友機制
                                            //mFirebaseAdapter.getRef(position).removeValue();
                                            /*
                                            mFirebaseAdapter.notifyItemRemoved(position);
                                            mFirebaseAdapter.notifyDataSetChanged();
                                            mFirebaseAdapter.notifyItemRangeChanged(position,mFirebaseAdapter.getItemCount());*/
                                            Toast.makeText(getContext(), "successfully removed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        }
                    }
                }));
        // 原先版本 沒有過濾朋友機制
/*
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mLinearLayoutManager.scrollToPosition(positionStart);
                }
            }
        });*/
        mMessageRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        // 原先版本 沒有過濾朋友機制
        //mMessageRecyclerView.setAdapter(mFirebaseAdapter);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.post_fragment, container, false);
        return returnView;
    }
}
