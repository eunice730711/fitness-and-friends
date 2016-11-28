package com.google.firebase.codelab.friendlychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.codelab.friendlychat.MainActivity.ANONYMOUS;
import static com.google.firebase.codelab.friendlychat.MainActivity.MESSAGES_CHILD;

/**
 * Created by pei on 2016/11/23.
 */

public class Post_fragment extends Fragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public TextView mdateTextView;
        public TextView mtimeTextView;
        public CircleImageView messengerImageView;
        public Button editbutton;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            mdateTextView = (TextView) itemView.findViewById(R.id.mdateTextView);
            mtimeTextView = (TextView) itemView.findViewById(R.id.mtimeTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            editbutton = (Button) itemView.findViewById(R.id.editbutton);
        }
    }

    private SharedPreferences mSharedPreferences;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<FriendlyMessage,MessageViewHolder>
            mFirebaseAdapter;




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mMessageRecyclerView = (RecyclerView) v.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child("messages")) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage, final int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(friendlyMessage.getText());
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                viewHolder.mdateTextView.setText(friendlyMessage.getDate());
                viewHolder.mtimeTextView.setText(friendlyMessage.getTime());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(getActivity(),
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(getActivity())
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
                if(!viewHolder.messengerTextView.getText().equals(mFirebaseUser.getDisplayName()))
                {
                    viewHolder.editbutton.setVisibility(View.INVISIBLE);
                }
                else
                {
                    viewHolder.editbutton.setVisibility(View.VISIBLE);
                    viewHolder.editbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                            final View v2 = inflater.inflate(R.layout.edit_post, null);
                            final EditText editText = (EditText) (v2.findViewById(R.id.e_Post));
                            editText.setText(viewHolder.messageTextView.getText());
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("編輯貼文")
                                    .setView(v2)
                                    .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Toast.makeText(getContext(),"修改完成" + position, Toast.LENGTH_SHORT).show();
                                            viewHolder.messageTextView.setText(editText.getText().toString());
                                            //mFirebaseDatabaseReference.child("message").getRef().child("text").setValue(editText.getText());
                                            Map<String, Object> nameMap = new HashMap<String, Object>();
                                            nameMap.put("text", editText.getText().toString());
                                            mFirebaseAdapter.getRef(position).updateChildren(nameMap);
                                        }})
                                    .setNeutralButton("取消",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            // TODO Auto-generated method stub
                                            Toast.makeText(getContext(), "取消",Toast.LENGTH_SHORT).show();
                                        }

                                    }).show();
                        }
                    });
                }


            }


        };
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
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.post_fragment, container, false);


        return returnView;
    }


}
