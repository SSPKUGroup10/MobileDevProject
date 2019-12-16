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
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.fragment.ExploreFragment;
import com.example.mobiledevproject.fragment.HomeFragment;
import com.example.mobiledevproject.fragment.MyFragment;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.util.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


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

        app.setUser(user);
        app.setToken(token);
        //  配置当前用户专用文件
//          将token本地化存储
        Utility.setData(HomeActivity.this, StorageConfig.SP_KEY_TOKEN, token);
        Utility.setDataList(HomeActivity.this, SP_GROUP_LIST_KEY, user.getJoinedCircles());
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
