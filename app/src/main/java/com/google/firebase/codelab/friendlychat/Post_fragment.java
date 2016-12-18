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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;


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

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<PostMessage,PostViewHolder> mFirebaseAdapter;
    public PostMessage p;
    private List<PostMessage> Post_List = new ArrayList<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = mFirebaseUser.getDisplayName();
        mMessageRecyclerView = (RecyclerView) v.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<PostMessage, PostViewHolder>(
                PostMessage.class,
                R.layout.item_message,
                PostViewHolder.class,
                mFirebaseDatabaseReference.child("messages")) {

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
        mMessageRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    //按一下進入detail
                    @Override
                    public void onItemClick(View view, int position) {
                        p = mFirebaseAdapter.getItem(position);
                        Intent intent = new Intent();
                        intent.setClass(getActivity() , Post_Detail.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("post",p);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    //長按來編輯貼文
                    @Override
                    public void onLongClick(View view, final int position) {
                        PostMessage post = mFirebaseAdapter.getItem(position);
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
                                            Toast.makeText(getContext(), "successfully changed" + position, Toast.LENGTH_SHORT).show();
                                            //viewHolder.messageTextView.setText(editText.getText().toString());
                                            Map<String, Object> nameMap = new HashMap<String, Object>();
                                            nameMap.put("text", editText.getText().toString());
                                            nameMap.put("title", editTitle.getText().toString());
                                            mFirebaseAdapter.getRef(position).updateChildren(nameMap);
                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            // TODO Auto-generated method stub
                                            Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        }
                    }
                }));
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
        mMessageRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.post_fragment, container, false);
        return returnView;
    }
}
