package com.example.mobiledevproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mobiledevproject.MyApp;
import com.example.mobiledevproject.R;
import com.example.mobiledevproject.adapter.ContentsVpAdapter;
import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.fragment.CircleFragment;
import com.example.mobiledevproject.fragment.GroupCheckinFragment;
import com.example.mobiledevproject.fragment.IntroFragment;
import com.example.mobiledevproject.fragment.ManageFragment;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;
import com.example.mobiledevproject.model.Group;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


import java.text.SimpleDateFormat;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GroupActivity extends AppCompatActivity {
    final static String TAG = "GroupActivity";
    @BindView(R.id.tl_checkin_funcs)
    TabLayout funcsTl;
    @BindView(R.id.vp_checkin_contents)
    ViewPager contentsVp;

    List<GetFragmentInfo> fragmentList;

    @BindView(R.id.iv_checkin_groupicon)
    ImageView groupIconIv;
    @BindView(R.id.tv_checkin_name)
    TextView nameTv;
    @BindView(R.id.tv_checkin_membernum)
    TextView memberNumTv;
    @BindView(R.id.btn_joinin)
    Button btnJoinin;
    @BindView(R.id.btn_checkin)
    FloatingActionButton checkinBtn;

    static final String STATE_GROUP = "cur_group";
    static final String STATE_USER = "cur_user";

    //    @AutoRestore
    public Group group;
    //    @AutoRestore
    public User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        MyApp app = (MyApp) getApplication();
        user = app.getUser();

//        if(savedInstanceState!=null){
//            Log.i(TAG, "onCreate: 已经重新加载");
//            group = (Group)savedInstanceState.getSerializable(STATE_GROUP);
////            user = (User)savedInstanceState.getSerializable(STATE_USER);
//        } else {
//            Log.i(TAG, "onCreate: 需要重新初始化");
        groupInit();
//        }

        viewPagerInit();
        tabInit();

        //  判断是否加入
        joinState();

        Log.i(TAG, "onCreate: " + user.toString());
    }

    private void joinState(){
        if(group.containsUser(user)){
            hasJoined();
        } else {
            hasNotJoined();
        }
    }

    private void hasJoined(){
        Log.i(TAG, "hasJoined: 已加入");
        btnJoinin.setText("已加入");
        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   if(canCheckin()) {

                    Intent intent = new Intent(GroupActivity.this, CheckinActivity.class);
                    intent.putExtra("group",group);
                    startActivity(intent);
//                }else{
//                    Toast.makeText(v.getContext(),"不在该圈子打卡时间内",Toast.LENGTH_SHORT).show();
//                }


            }
        });
    }

    private void hasNotJoined(){
        Log.i(TAG, "hasNotJoined: 未加入");
        btnJoinin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = Utility.getData(GroupActivity.this, StorageConfig.SP_KEY_TOKEN);
                int groupId = group.getGroupId();
                String url = API.CIRCLE+groupId+"/members/";

                HttpUtil.postRequestWithToken(url, "", token, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hasJoined();
                            }
                        });
                    }
                });
            }
        });
    }


    public boolean canCheckin() {
        String beginTime = "8:00";
        String endTime = "8:20";
        //current time
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");
        String currentTime = dateFormat.format(date);

        System.out.println(beginTime);
        System.out.println(endTime);
        System.out.println(currentTime);

        int beginTimeHour = Integer.parseInt(beginTime.split(":")[0]);
        int beginTimeMinute = Integer.parseInt(beginTime.split(":")[1]);
        int endTimeHour = Integer.parseInt(endTime.split(":")[0]);
        int endTimeMinute = Integer.parseInt(endTime.split(":")[1]);
        int currentTimeHour = Integer.parseInt(currentTime.split(":")[0]);
        int currentTimeMinute = Integer.parseInt(currentTime.split(":")[1]);

        if (beginTimeHour <= currentTimeHour && currentTimeHour <= endTimeHour) {
            if (currentTimeHour == endTimeHour) {
                if (currentTimeMinute <= endTimeMinute)
                    return true;
                else
                    return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void groupInit() {
        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra("group_info");
        viewSetInfo(group);
    }


    private void viewPagerInit() {
        fragmentList = new ArrayList<>();
        fragmentList.add(IntroFragment.newInstance("简介", group));
        fragmentList.add(GroupCheckinFragment.newInstance("动态", "内容"));
        fragmentList.add(CircleFragment.newInstance("圈子", "内容"));
        fragmentList.add(ManageFragment.newInstance("管理", "内容"));
        contentsVp.setAdapter(new ContentsVpAdapter(getSupportFragmentManager(), fragmentList));
    }

    private void tabInit() {
        funcsTl.setupWithViewPager(contentsVp);
    }
//    private void intentReceived() {
//
//    }

    private void viewSetInfo(Group group) {

        nameTv.setText(group.getGroupName());

//        nameTv.setText("aa");
        //  此处成员数据要通过数据库读取
        memberNumTv.setText("成员" + group.getMemberList().size() + "人");

        //  此处小组头像要通过数据库读取
//        groupIconIv.setImageResource();

    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//
//        Log.i(TAG, "onSaveInstanceState: 已经保存");
//
//        savedInstanceState.putSerializable(STATE_GROUP, group);
//        savedInstanceState.putSerializable(STATE_USER, user);
//    }
}
