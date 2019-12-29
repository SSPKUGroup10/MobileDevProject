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
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.example.mobiledevproject.util.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


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

    public Group group;
    public User user;
    public String masterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        MyApp app = (MyApp) getApplication();
        user = app.getUser();
        groupInit();
        viewPagerInit();
        tabInit();

        //  判断是否加入
        Log.i(TAG, "onCreate: " + user.toString());
    }

    private boolean joinState(){

        if(group.containsUser(user)){
            return true;
        } else {
            return false;
        }
    }

    private void hasJoined(){
        Log.i(TAG, "hasJoined: 已加入");
        btnJoinin.setText("已加入");
//        contentFresh();
        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(canCheckin()) {
                    Intent intent = new Intent(GroupActivity.this, CheckinActivity.class);
                    intent.putExtra("group",group);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(v.getContext(),"不在该圈子打卡时间内",Toast.LENGTH_SHORT).show();
                }

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
                Log.i(TAG, "onClick: "+url);

                HttpUtil.postRequestWithToken2(url, token, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();

                        Log.i(TAG, "onResponse: 点击加入之后"+responseBody);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                hasJoined();
                                contentFresh();
                            }
                        });
                    }
                });
            }
        });
    }

    public boolean canCheckin() {

        String time1 = "2019-12-12 00:10:10";
        String time2 = "2019-12-12 23:10:10";
//        String beginTime = group.getStartAt().split(" ")[1];
        String beginTime = time1.split(" ")[1];
        String []str1 = beginTime.split(":");
        if(str1[0].charAt(0) == '0')
            beginTime = str1[0].charAt(1)+":"+str1[1];
        else
            beginTime = str1[0]+":"+str1[1];

//        String endTime = group.getStartAt().split(" ")[1];
        String endTime = time2.split(" ")[1];
        String [] str2 = endTime.split(":");

        if(str2[0].charAt(0) == '0')
            endTime = str2[0].charAt(1)+":"+str2[1];
        else
            endTime = str2[0]+":"+str2[1];
        System.out.println(beginTime);
        System.out.println(endTime);

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

    //  取出Intent中的信息
    private void groupInit() {

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra("group_info");
        masterName = intent.getStringExtra("master_name");
        nameTv.setText(group.getGroupName());


        Log.i(TAG, "groupInit: groupid="+group.getGroupId());
        contentFresh();

    }

    private void contentFresh(){
        int groupId = group.getGroupId();
//        int userId = user.getUserId();

        if(joinState()){
            viewSetInfo();
            hasJoined();
            return;
        }

        String token = Utility.getData(GroupActivity.this, StorageConfig.SP_KEY_TOKEN);
        String url = API.CIRCLE+groupId+"/members/";

        Log.i(TAG, "contentFresh: "+url);
        HttpUtil.getRequestWithToken(url, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.i(TAG, "onResponse: "+responseBody);
                JsonObject jsonObject;
                if ((jsonObject = StatusCodeUtil.isNormalResponse(responseBody)) != null) {
                    int status = jsonObject.get("status").getAsInt();
                    if (StatusCodeUtil.isNormalStatus(status)) {
                        //  正确
                        List<User> members = new ArrayList<>();
                        JsonArray data = jsonObject.get("data").getAsJsonArray();
                        for(JsonElement member:data){
                            JsonObject cur = member.getAsJsonObject();
                            User user = new User();
                            user.setUserId(cur.get("UserID").getAsInt());
                            user.setUserName(cur.get("Username").getAsString());
                            members.add(user);
                        }
                        Log.i(TAG, "onResponse: 小组成员更新");
                        Log.i(TAG, "onResponse: 成员人数"+members.size());
                        group.setMemberList(members);

                        //  修改当前用户的

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewSetInfo();
                                if(joinState()){
                                    hasJoined();
                                } else {
                                    hasNotJoined();
                                }
                            }
                        });
                    } else {
                        Log.i(TAG, "onResponse: " + status);
                    }
                } else {
                    Log.i(TAG, "onResponse: 响应内容错误");
                }
            }
        });
    }


    private void viewPagerInit() {
        fragmentList = new ArrayList<>();
        fragmentList.add(IntroFragment.newInstance(masterName, group));
        fragmentList.add(GroupCheckinFragment.newInstance("动态", group));
        fragmentList.add(CircleFragment.newInstance("圈子", group));
        fragmentList.add(ManageFragment.newInstance("管理", "内容"));
        contentsVp.setAdapter(new ContentsVpAdapter(getSupportFragmentManager(), fragmentList));
    }

    private void tabInit() {
        funcsTl.setupWithViewPager(contentsVp);
    }

    private void viewSetInfo() {

        Log.i(TAG, "viewSetInfo: 成员" + group.getMemberList().size() + "人");
        memberNumTv.setText("成员" + group.getMemberList().size() + "人");
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
