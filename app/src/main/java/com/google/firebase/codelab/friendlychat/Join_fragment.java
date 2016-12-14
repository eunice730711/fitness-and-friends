package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.codelab.friendlychat.Join_fragment.JoinViewHolder.J_view;
import static com.google.firebase.codelab.friendlychat.MainActivity.ANONYMOUS;

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
        public Button editbutton;
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
            editbutton = (Button) itemView.findViewById(R.id.edit);
            editbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

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
    private FirebaseRecyclerAdapter<JoinMessage,JoinViewHolder> mFirebaseAdapter;

    public JoinMessage j ;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessageRecyclerView = (RecyclerView) v.findViewById(R.id.joinRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
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
                J_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Log.w(TAG, "You clicked on "+position);
                        //mRecycleViewAdapter.getRef(position).removeValue();
                        j = mFirebaseAdapter.getItem(viewHolder.getAdapterPosition());
                        Intent intent = new Intent();
                        intent.setClass(getActivity() , Join_Detail.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("join",j);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
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
        View returnView = inflater.inflate(R.layout.join_fragment, container, false);


        return returnView;
    }




}
