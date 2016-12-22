package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.firebase.codelab.friendlychat.Day.DatetoString;

/**
 * Created by JCLIN on 2016/12/23.
 */

public class PlanTouchHelper extends ItemTouchHelper.Callback {

    //處理Item拖曳 + 整條滑動

    //移動標誌
    private int dragFlags;
    private int swipeFlags;
    private List<WeekContent> content;
    private Context c;
    private boolean edit;
    private int todayWeek;
    private int todayposition;

    public PlanTouchHelper(List<WeekContent> content, Context c, boolean edit){
        this.content = content;
        this.c = c;
        this.edit = edit;

        Calendar calendar = Calendar.getInstance();

        for(int i=0; i<content.size()-1; i++){
            for(int j=0; j<content.get(i).getDays().size(); j++){

                String date = DatetoString(content.get(i).getDays().get(j).getDate(),0);
                String now = DatetoString(calendar.getTime(),0);
                if(date.equals(now)){
                    todayWeek = i;
                    todayposition = j;
                }
            }
        }

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        int week = content.get(content.size()-1).getWeek();
        if(isActionAllowed(viewHolder.getAdapterPosition())){
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        else{
            dragFlags = 0;
            swipeFlags = 0;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        int week = content.get(content.size()-1).getWeek();

        if(isActionAllowed(toPosition) && isActionAllowed(fromPosition)){
            if (fromPosition < toPosition) {
                //向下拖動
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(content.get(week).getDays(), i, i + 1);
                    Date temp = content.get(week).getDays().get(i).getDate();
                    content.get(week).getDays().get(i).setDate(content.get(week).getDays().get(i+1).getDate());
                    content.get(week).getDays().get(i+1).setDate(temp);
                }
            }
            else {
                //向上拖動
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(content.get(week).getDays(), i, i - 1);
                    Date temp = content.get(week).getDays().get(i).getDate();
                    content.get(week).getDays().get(i).setDate(content.get(week).getDays().get(i-1).getDate());
                    content.get(week).getDays().get(i-1).setDate(temp);
                }
            }


            List<WeekContent> newplan = new ArrayList<WeekContent>();
            for(int i=0; i<content.size()-1; i++){
                newplan.add(content.get(i));
            }

            //及時修改顯示
            if(fromPosition < toPosition){
                recyclerView.getAdapter().notifyItemRangeChanged(fromPosition,toPosition-fromPosition+1,content.get(week));
            }
            else{
                recyclerView.getAdapter().notifyItemRangeChanged(toPosition,fromPosition-toPosition+1,content.get(week));
            }

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

        }

        return true;

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


    }

    @Override
    public boolean isLongPressDragEnabled() {
        return edit;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setPressed(true);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setPressed(false);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }



    public boolean isActionAllowed(int position){

        boolean isAllow = false;

        int week = content.get(content.size()-1).getWeek();

        if(week > todayWeek) isAllow = true;
        else if(week == todayWeek){
            if(position > todayposition) isAllow = true;
            else if(position == todayposition){
                if(!content.get(todayWeek).getDays().get(todayposition).getChoose()){
                    isAllow = true;
                }
                else{
                    if(!content.get(todayWeek).getDays().get(todayposition).getComplete()){
                        isAllow = true;
                    }
                }
            }
        }
        return isAllow;

    }

}