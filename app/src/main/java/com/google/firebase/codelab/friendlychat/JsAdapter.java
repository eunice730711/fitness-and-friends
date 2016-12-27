package com.google.firebase.codelab.friendlychat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alan Chen on 2016/12/21.
 */

public class JsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList iList; // 放在 ArrayList 裡面
    public UserProfile userProfile;
    public String requester;

    JsAdapter(ArrayList list) {
        iList = list ; //new ArrayList<>();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // ViewHolder 可以放更多複雜的 View，不過我們這邊直接用自己的 Dummy
        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return iList.size();
    }
}
