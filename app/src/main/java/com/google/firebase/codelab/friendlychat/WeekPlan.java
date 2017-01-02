package com.google.firebase.codelab.friendlychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.google.firebase.codelab.friendlychat.Day.DatetoString;

public class WeekPlan extends AppCompatActivity implements RecyclerTouchListener.RecyclerTouchListenerHelper{

    private List<WeekContent> myplan = new ArrayList<WeekContent>();
    private List <WeekContent> receive = new ArrayList<WeekContent>();

    private RecyclerView planRecyclerView;
    private LinearLayoutManager layoutManager;
    private PlanAdapter adapter;
    private ItemTouchHelper helper;

    private RecyclerTouchListener onTouchListener;
    private OnActivityTouchListener touchListener;

    private TextView wtitle;
    private TextView editTitle;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_plan);

        //初始化RecyclerView用的layoutManager
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        Intent intent = this.getIntent();
        receive = (List<WeekContent>) intent.getSerializableExtra("plan");

        for(int i=0; i<receive.size()-1; i++){
            myplan.add(receive.get(i));
        }
        editMode = false;

        final int w_position = receive.get(receive.size()-1).getWeek();

        wtitle = (TextView) findViewById(R.id.textView);
        wtitle.setText("Week"+ myplan.get(w_position).getWeek());

        editTitle = (TextView) findViewById(R.id.editTitle);

        planRecyclerView = (RecyclerView) findViewById(R.id.EveryDay);
        adapter = new PlanAdapter(WeekPlan.this,myplan,w_position);
        planRecyclerView.setLayoutManager(layoutManager);
        planRecyclerView.setAdapter(adapter);

        //拖動要用的Helper
        helper = new ItemTouchHelper(new PlanTouchHelper(receive,WeekPlan.this,editMode));
        helper.attachToRecyclerView(planRecyclerView);


        planRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), planRecyclerView, new RecyclerViewTouchListener.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
            @Override
            public void onLongClick(View view, int position) {
                //處理長按事件
                if(editMode){
                    helper.startDrag(planRecyclerView.findContainingViewHolder(view));
                }

            }
        }));

        //側滑
        onTouchListener = new RecyclerTouchListener(this, planRecyclerView);
        onTouchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {

                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.edit, R.id.delete)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, final int position) {
                        //按下側滑菜單中的按鈕
                        if (viewID == R.id.edit) {
                            LayoutInflater inflater = LayoutInflater.from(WeekPlan.this);
                            final View v = inflater.inflate(R.layout.edit_plan, null);
                            final TextView editTitle = (TextView)(v.findViewById(R.id.plan_edit_title));
                            final EditText editContent = (EditText)(v.findViewById(R.id.plan_edit_content));
                            final int level = myplan.get(w_position).getLevel();

                            if(level == 1){
                                editTitle.setText("時間 (mins) :");
                            }
                            else{
                                editTitle.setText("距離 (km) :");
                            }

                            new AlertDialog.Builder(WeekPlan.this)
                                    .setTitle("Edit Your Plan")
                                    .setView(v)
                                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (level){
                                                case 1:
                                                    myplan.get(w_position).getDays().get(position).setTime((int)Double.parseDouble(editContent.getText().toString()));
                                                    break;
                                                case 2:
                                                    myplan.get(w_position).getDays().get(position).setDist(Double.parseDouble(editContent.getText().toString()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                            //Toast.makeText(WeekPlan.this, "Change" ,Toast.LENGTH_SHORT).show();
                                            myplan.get(w_position).getDays().get(position).setChoose(true);
                                            adapter.notifyItemChanged(position);
                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            // TODO Auto-generated method stub
                                            //Toast.makeText(WeekPlan.this, "Cancel" ,Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();


                        }
                        else if (viewID == R.id.delete) {
                            myplan.get(w_position).getDays().get(position).setDist(0.0);
                            myplan.get(w_position).getDays().get(position).setTime(0);
                            myplan.get(w_position).getDays().get(position).setChoose(false);
                            myplan.get(w_position).getDays().get(position).setComplete(false);
                            adapter.notifyItemChanged(position);
                        }
                    }
                })
                .setSwipeable(false);


        //上一頁
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode){
                    AlertDialog.Builder builder = new AlertDialog.Builder(WeekPlan.this);

                    builder.setMessage("是否儲存修改再離開?")
                            .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ScheduleIO fileIO = new ScheduleIO(WeekPlan.this);
                                    fileIO.DeleteFile();
                                    fileIO.WriteFile(myplan);

                                    Intent intent1 = new Intent();
                                    intent1.setClass(WeekPlan.this, schedule.class);
                                    startActivity(intent1);
                                    WeekPlan.this.finish();
                                }
                            })
                            .setPositiveButton("否", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent2 = new Intent();
                                    intent2.setClass(WeekPlan.this, schedule.class);
                                    startActivity(intent2);
                                    WeekPlan.this.finish();
                                }
                            });
                    AlertDialog about_dialog = builder.create();
                    about_dialog.show();
                }
                else{
                    Intent intent3 = new Intent();
                    intent3.setClass(WeekPlan.this, schedule.class);
                    startActivity(intent3);
                    WeekPlan.this.finish();
                }



            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editmode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.plan_editDone){
            //Toast.makeText(WeekPlan.this , "Save" , Toast.LENGTH_SHORT).show();
            editTitle.setText("");

            ScheduleIO fileIO = new ScheduleIO(WeekPlan.this);
            fileIO.DeleteFile();
            fileIO.WriteFile(myplan);
            //fileIO.WriteFirebase(myplan);

            invalidateOptionsMenu();
            editMode = false;
            onTouchListener.setSwipeable(false);

        }
        else if(id == R.id.plan_edit){
            //Toast.makeText(WeekPlan.this , "Edit" , Toast.LENGTH_SHORT).show();
            editTitle.setText("Edit Mode");
            invalidateOptionsMenu();

            editMode = true;
            onTouchListener.setSwipeable(true);
            onTouchListener.setUnSwipeableRows(unSwipeRows().toArray(new Integer[0]));

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.plan_editDone).setVisible(editMode);
        menu.findItem(R.id.plan_edit).setVisible(!editMode);

        return super.onPrepareOptionsMenu(menu);
    }

    //側滑菜單要複寫的

    @Override
    protected void onResume() {
        super.onResume();
        planRecyclerView.addOnItemTouchListener(onTouchListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        planRecyclerView.removeOnItemTouchListener(onTouchListener);
    }

    //Activity的implement 按到別的地方就關掉菜單
    @Override
    public void setOnActivityTouchListener(OnActivityTouchListener listener) {
        this.touchListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (touchListener != null) touchListener.getTouchCoordinates(ev);
        return super.dispatchTouchEvent(ev);
    }


    // 丟出不可以滑動的rows
    // 非編輯模式: 全部
    // 編輯模式: 小於今天皆不可以滑
    public List<Integer> unSwipeRows(){
        List<Integer> rows = new ArrayList<Integer>();
        if(!editMode){
            for(int i=0; i<7; i++){
                rows.add(i);
            }
        }
        else{
            int week = receive.get(receive.size()-1).getWeek();
            int todayweek = 0;
            int todayposition = 0;

            Calendar calendar = Calendar.getInstance();

            for(int i=0; i<myplan.size(); i++){
                for(int j=0; j<myplan.get(i).getDays().size(); j++){

                    String date = DatetoString(myplan.get(i).getDays().get(j).getDate(),0);
                    String now = DatetoString(calendar.getTime(),0);
                    if(date.equals(now)){
                        todayweek = i;
                        todayposition = j;
                    }
                }
            }

            if(week < todayweek){
                for(int i=0; i<7; i++){
                    rows.add(i);
                }
            }
            else if(week == todayweek){
                int limit = todayposition;
                if(myplan.get(todayweek).getDays().get(todayposition).getChoose() && myplan.get(todayweek).getDays().get(todayposition).getComplete()){
                    limit+=1;
                }
                for(int i=0; i<limit; i++){
                    rows.add(i);
                }
            }
        }
        return rows;
    }


}
