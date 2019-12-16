package com.example.mobiledevproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.mobiledevproject.MyApp;
import com.example.mobiledevproject.R;
import com.example.mobiledevproject.adapter.BodyVpAdapter;
import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.fragment.ExploreFragment;
import com.example.mobiledevproject.fragment.HomeFragment;
import com.example.mobiledevproject.fragment.MyFragment;
import com.example.mobiledevproject.model.GroupCreate;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.model.UserCreate;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.example.mobiledevproject.util.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    public static final String SP_GROUP_LIST_KEY = "group_list";


    //  当前用户的信息
    public User user;

    //  添加fragment列表
    List<Fragment> fragList;

    @BindView(R.id.vp_home_body)
    ViewPager bodyVp;
    @BindView(R.id.nav_home_bottom)
    BottomNavigationView bottomBnv;

    MenuItem menuItem;

    public MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        app = (MyApp)getApplication();

        //  登录状态检测
//        logon();

        //  用户信息初始化
        userInfoInit();
        //  选项卡初始化
        viewPagerInit();
        //  底部导航栏点击事件初始化
        navigationInit();
        bodyVp.setCurrentItem(0);

    }

    private void logon(){
        SharedPreferences sp = this.getSharedPreferences("init_config", MODE_PRIVATE);
        if(!sp.getBoolean("logon", false)){
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        } else {

        }
    }

    private void userInfoInit(){
        //  从intent中读取数据
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user_info");
        StorageConfig.SP_NAME = user.getUserName();
        String token = intent.getStringExtra("token");

        refreshCircleInfo();

        app.setUser(user);
        app.setToken(token);
        //  配置当前用户专用文件
//          将token本地化存储
        Utility.setData(HomeActivity.this, StorageConfig.SP_KEY_TOKEN, token);
        Utility.setDataList(HomeActivity.this, SP_GROUP_LIST_KEY, user.getJoinedCircles());
    }

    private void refreshCircleInfo(){

        UserCreate info = new UserCreate(user);
        //上传json格式数据
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonInfo = gson.toJson(info);
        HttpUtil.postOkHttpRequest(API.LOGIN, jsonInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i(TAG, "onFailure: 网络请求错误");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.i(TAG, "onResponse: " + responseBody);

                JsonObject jsonObject;
                if ((jsonObject = StatusCodeUtil.isNormalResponse(responseBody)) != null) {
                    int status = jsonObject.get("status").getAsInt();
                    if (StatusCodeUtil.isNormalStatus(status)) {
                        //  正确
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //登录成功后跳转到HomeActivity，通过intent把user和token的信息传过来，user用类传递，token用string
                                JsonObject data = jsonObject.get("data").getAsJsonObject();
                                String token = data.get("accessToken").getAsString();
                                int userID = data.get("userID").getAsInt();

                                info.setUserId(userID);
                                User newUser = new User(info);

                                JsonArray joinedCircles = data.get("joinedCircles").getAsJsonArray();
                                JsonArray otherCircles = data.get("otherCircles").getAsJsonArray();

                                for(JsonElement group : joinedCircles){
                                    JsonObject cur = group.getAsJsonObject();
                                    GroupCreate createdGroup = new GroupCreate();
                                    createdGroup.setGroupName(cur.get("name").getAsString());
                                    createdGroup.setDescription(cur.get("desc").getAsString());
                                    createdGroup.setCheckRule(cur.get("checkRule").getAsString());
                                    createdGroup.setMasterId(cur.get("circleMasterId").getAsInt());
                                    createdGroup.setStartAt(cur.get("startAt").getAsString());
                                    createdGroup.setEndAt(cur.get("endAt").getAsString());
                                    createdGroup.setType(cur.get("type").getAsString());
                                    createdGroup.setGroupId(cur.get("id").getAsInt());
                                    newUser.getJoinedCircles().add(createdGroup);
                                }

                                for(JsonElement group : otherCircles){
                                    JsonObject cur = group.getAsJsonObject();
                                    GroupCreate createdGroup = new GroupCreate();
                                    createdGroup.setGroupName(cur.get("name").getAsString());
                                    createdGroup.setDescription(cur.get("desc").getAsString());
                                    createdGroup.setCheckRule(cur.get("checkRule").getAsString());
                                    createdGroup.setMasterId(cur.get("circleMasterId").getAsInt());
                                    createdGroup.setStartAt(cur.get("startAt").getAsString());
                                    createdGroup.setEndAt(cur.get("endAt").getAsString());
                                    createdGroup.setType(cur.get("type").getAsString());
                                    createdGroup.setGroupId(cur.get("id").getAsInt());
                                    newUser.getOtherCircles().add(createdGroup);
//                                            Utility.setData(HomeActivity.this, StorageConfig.SP_KEY_TOKEN, token);
                                }

                                Log.i(TAG, "run: "+newUser.toString());
                                user = newUser;
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
        //  配置各个选项卡对应的fragment
        fragList = new ArrayList<>();
        //  每个fragment都接收user信息去使用
        fragList.add(HomeFragment.newInstance(user));
        fragList.add(ExploreFragment.newInstance(user));
        fragList.add(MyFragment.newInstance(user));

        bodyVp.setAdapter(new BodyVpAdapter(getSupportFragmentManager(), fragList));
        bodyVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                //  让底部按钮随着页面的滑动一起变化
                if(menuItem!=null){
                    menuItem.setChecked(false);
                } else {
                    bottomBnv.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomBnv.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void navigationInit() {

        //  这里只是设置点击哪个item后，给viewpager在fraglist中选择一个item
        bottomBnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_home_navigation_home:
                        Log.i(TAG, "onNavigationItemSelected: home");
                        bodyVp.setCurrentItem(0);
                        return true;
                    case R.id.item_home_navigation_explore:
                        Log.i(TAG, "onNavigationItemSelected: explore");
                        bodyVp.setCurrentItem(1);
                        return true;
                    case R.id.item_home_navigation_my:
                        Log.i(TAG, "onNavigationItemSelected: my");
                        bodyVp.setCurrentItem(2);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

}
