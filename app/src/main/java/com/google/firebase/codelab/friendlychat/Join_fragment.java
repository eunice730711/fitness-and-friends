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

public class Join_fragment extends Fragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static class JoinViewHolder extends RecyclerView.ViewHolder {
        public TextView typeTextView;
        public TextView posTextView;
        public TextView NameTexView;
        public TextView dateTextView;
        public TextView timeTextView;
        public CircleImageView messengerImageView;
        public static View J_view;

        public JoinViewHolder(View v) {
            super(v);
            J_view = v;
            typeTextView = (TextView) itemView.findViewById(R.id.Type);
            posTextView = (TextView) itemView.findViewById(R.id.Position);
            NameTexView = (TextView) itemView.findViewById(R.id.Name);
            dateTextView = (TextView) itemView.findViewById(R.id.Date);
            timeTextView = (TextView) itemView.findViewById(R.id.Time);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<JoinMessage,JoinViewHolder> mFirebaseAdapter;
    public JsAdapter adapter;
    private List<String> FriendIdList;
    private List<DatabaseReference>RefList;
    private ArrayList<JoinMessage> FriendJoin_List;
    private final int limit_join_number = 30;
    private UserProfile userProfile;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mMessageRecyclerView = (RecyclerView) v.findViewById(R.id.joinRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setAutoMeasureEnabled(false);
        mMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        // New child entries
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO( getActivity());
        userProfile = profileIO.ReadFile();


        mFirebaseDatabaseReference.child("Friend").child(userProfile.getUserid()).addValueEventListener(new ValueEventListener() {
            @Override
            // 取得好友列表
            public void onDataChange(DataSnapshot snapshot) {
                FriendIdList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HashMap map = (HashMap<String, String>) dataSnapshot.getValue(Object.class);
                    FriendIdList.add(map.get("friendid").toString());
                }
                // 讓自己的Join 也可以在塗鴉牆顯示
                FriendIdList.add(userProfile.getUserid());
                //  搜尋好友的文章
                mFirebaseDatabaseReference.child("Join").orderByChild("id").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        FriendJoin_List = new ArrayList<>();
                        RefList = new ArrayList<>();
                        // 限定只顯示15筆資料
                        int limit_number = limit_join_number;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if( limit_number ==0)
                                break;
                            JoinMessage joinMessage =  dataSnapshot.getValue(JoinMessage.class);
                            String id = joinMessage.getId();
                            if( FriendIdList.indexOf(id)!=-1){
                                limit_number --;
                                FriendJoin_List.add(joinMessage);
                                RefList.add(dataSnapshot.getRef());
                            }
                        }
                        // 完成搜尋好友文章
                        adapter = new JsAdapter(FriendJoin_List){
                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                // 設定viewHolder 所使用的 layout
                                View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_joinmessage, parent, false);
                                JoinViewHolder vh = new JoinViewHolder(mView);
                                return vh;
                            }
                            @Override
                            public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {
                                JoinViewHolder viewHolder = (JoinViewHolder) holder;

                                JoinMessage joinMessage = (JoinMessage)iList.get(position);

                                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                viewHolder.typeTextView.setText(joinMessage.getTitle());
                                viewHolder.posTextView.setText(joinMessage.getJpos());
                                viewHolder.NameTexView.setText(joinMessage.getName());
                                viewHolder.dateTextView.setText(joinMessage.getJdate());
                                viewHolder.timeTextView.setText(joinMessage.getJtime());
                                if (joinMessage.getPhotoUrl() == null) {
                                    viewHolder.messengerImageView
                                            .setImageDrawable(ContextCompat
                                                    .getDrawable(getActivity(),
                                                            R.drawable.ic_account_circle_black_36dp));
                                } else {
                                    Glide.with(getActivity())
                                            .load(joinMessage.getPhotoUrl())
                                            .into(viewHolder.messengerImageView);
                                }
                            }
                        };
                        mMessageRecyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        /*
        mFirebaseAdapter = new FirebaseRecyclerAdapter<JoinMessage, JoinViewHolder>(
                JoinMessage.class,
                R.layout.item_joinmessage,
                JoinViewHolder.class,
                mFirebaseDatabaseReference.child("Join")) {
            @Override
            protected void populateViewHolder(final JoinViewHolder viewHolder,
                                              final JoinMessage JoinMessage,final int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.typeTextView.setText(JoinMessage.getTitle());
                viewHolder.posTextView.setText(JoinMessage.getJpos());
                viewHolder.NameTexView.setText(JoinMessage.getName());
                viewHolder.dateTextView.setText(JoinMessage.getJdate());
                viewHolder.timeTextView.setText(JoinMessage.getJtime());
                if (JoinMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(getActivity(),
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(getActivity())
                            .load(JoinMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }

            }
        };
        */
        mMessageRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //j = mFirebaseAdapter.getItem(position);
                        String ref = RefList.get(position).toString();
                        // 原先版本 沒有過濾朋友機制
                        //String ref = mFirebaseAdapter.getRef(position).toString();
                        Intent intent = new Intent();
                        intent.setClass(getActivity() , Join_Detail.class);
                        //Bundle bundle = new Bundle();
                        //bundle.putSerializable("join",j);
                        //intent.putExtras(bundle);
                        intent.putExtra("ref",ref);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, final int position) {
                        JoinMessage join = (JoinMessage) adapter.getItem(position);
                        // 原先版本 沒有過濾朋友機制
                        //JoinMessage join = mFirebaseAdapter.getItem(position);
                        if (mFirebaseUser.getDisplayName().equals(join.getName())) {
                            //彈跳視窗(編輯和刪除)
                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                            final View v = inflater.inflate(R.layout.edit_join, null);
                            final EditText editText = (EditText)(v.findViewById(R.id.e_JText));
                            final EditText editTitle = (EditText)(v.findViewById(R.id.e_JTitle));
                            final EditText editDate = (EditText)(v.findViewById(R.id.e_JDate));
                            final EditText editTime = (EditText)(v.findViewById(R.id.e_JTime));
                            final EditText editPos = (EditText)(v.findViewById(R.id.e_JPos));
                            editText.setText(join.getText());
                            editTitle.setText(join.getTitle());
                            editDate.setText(join.getJdate());
                            editTime.setText(join.getJtime());
                            editPos.setText(join.getJpos());
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Edit Your Join")
                                    .setView(v)
                                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Toast.makeText(getContext(), "successfully changed", Toast.LENGTH_SHORT).show();
                                            //viewHolder.messageTextView.setText(editText.getText().toString());
                                            Map<String, Object> nameMap = new HashMap<String, Object>();
                                            nameMap.put("text", editText.getText().toString());
                                            nameMap.put("title", editTitle.getText().toString());
                                            nameMap.put("jtime", editTime.getText().toString());
                                            nameMap.put("jdate", editDate.getText().toString());
                                            nameMap.put("jpos", editPos.getText().toString());
                                            // 原先版本 沒有過濾朋友機制
                                            //mFirebaseAdapter.getRef(position).updateChildren(nameMap);
                                            RefList.get(position).updateChildren(nameMap);
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
                                            adapter.notifyItemRangeChanged(position,adapter.getItemCount());
                                            // 原先版本 沒有過濾朋友機制
                                            //mFirebaseAdapter.getRef(position).removeValue();
                                            //mFirebaseAdapter.notifyItemRemoved(position);
                                            //mFirebaseAdapter.notifyDataSetChanged();
                                            //mFirebaseAdapter.notifyItemRangeChanged(position,mFirebaseAdapter.getItemCount());
                                            Toast.makeText(getContext(), "successfully removed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        }
                    }
                }));
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
        });
        */

        mMessageRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        // 原先版本 沒有過濾朋友機制
        //mMessageRecyclerView.setAdapter(mFirebaseAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.join_fragment, container, false);


        return returnView;
    }




}
