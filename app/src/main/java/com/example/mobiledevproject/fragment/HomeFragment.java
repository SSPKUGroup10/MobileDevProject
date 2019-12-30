package com.example.mobiledevproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.activity.CreateGroupActivity;
import com.example.mobiledevproject.adapter.ListRcvAdapter;
import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.model.GroupCreate;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.model.UserCreate;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.example.mobiledevproject.util.Utility;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    Unbinder unbinder;
    public static final String SP_GROUP_LIST_KEY = "group_list";

    public User user;

    ListRcvAdapter adapter;
    List<GroupCreate> infoList;

    @BindView(R.id.tv_home_create_group)
    TextView createGroupTv;
    @BindView(R.id.mcv_home_groups)
    MaterialCardView groupsMcv;
    @BindView(R.id.rcv_home_list)
    RecyclerView listRcv;
    @BindView(R.id.srl_home_fragment_refresh)
    SwipeRefreshLayout srlHomeFragmentRefresh;

    public HomeFragment() {
    }

    //  好像用不到这个参数了
    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        fragment.user = user;
        return fragment;
    }

    //  纯净版创建
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        viewSetOnClick();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: 进行界面初始化");
//        dataInit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onActivityCreated: 进行界面初始化");
        srlHomeFragmentRefresh.setRefreshing(true);
        refreshCircleInfo();
    }

    //  刷新的逻辑，其他地方更新存储在文件中的圈子列表，这里总是读取文件中的圈子列表
    private void dataInit() {
        if(infoList!=null){
            infoList.clear();
        }
        infoList = Utility.getDataList(getContext(), StorageConfig.SP_KEY_USER_JOINED_CIRCLES, GroupCreate.class);
        initRecycleView();
    }

    private void viewSetOnClick() {
        groupsMcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 创建一个圈子");
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                //  不需要传递user数据了
//                intent.putExtra("user", user);
                startActivityForResult(intent, 1);
            }
        });

        srlHomeFragmentRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCircleInfo();
            }
        });
    }

    //  访问网络加载新的圈子信息并存储下来，然后执行datainit方法。
    private void refreshCircleInfo(){

        Log.i(TAG, "refreshCircleInfo: 开始刷新列表");

        UserCreate userCreate = new UserCreate();
        userCreate.setUserName(Utility.getData(getContext(), StorageConfig.SP_KEY_USER_NAME));
        userCreate.setPassword(Utility.getData(getContext(), StorageConfig.SP_KEY_PASSWORD));

        //上传json格式数据
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonInfo = gson.toJson(userCreate);
        
        HttpUtil.postOkHttpRequest(API.LOGIN, jsonInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i(TAG, "onFailure: 网络请求错误");
                srlHomeFragmentRefresh.setRefreshing(false);
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
                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                        String token = data.get("accessToken").getAsString();
                        int userID = data.get("userID").getAsInt();

                        userCreate.setUserId(userID);
                        User newUser = new User(userCreate);

                        JsonArray joinedCircles = data.get("joinedCircles").getAsJsonArray();
                        JsonArray otherCircles = data.get("otherCircles").getAsJsonArray();

                        //  加载圈子列表
                        for (JsonElement group : joinedCircles) {
                            JsonObject cur = group.getAsJsonObject();
                            newUser.getJoinedCircles().add(Utility.setGroupInfo(cur));
                        }
                        for (JsonElement group : otherCircles) {
                            JsonObject cur = group.getAsJsonObject();
                            newUser.getOtherCircles().add(Utility.setGroupInfo(cur));
                        }

                        Log.i(TAG, "onResponse: 圈子列表加载完毕，加入的圈子个数为"+newUser.getJoinedCircles().size());

                        Utility.setDataList(getContext(), StorageConfig.SP_KEY_USER_JOINED_CIRCLES, newUser.getJoinedCircles());

                        Log.i(TAG, "run: "+newUser.toString());
//                        user = newUser;
                        Log.i(TAG, "onResponse: 刷新完成");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataInit();
                                adapter.notifyDataSetChanged();
                                srlHomeFragmentRefresh.setRefreshing(false);
                            }
                        });
                    } else {
                        Log.i(TAG, "onResponse: " + status);
                        srlHomeFragmentRefresh.setRefreshing(false);
                    }
                } else {
                    Log.i(TAG, "onResponse: 响应内容错误");
                    srlHomeFragmentRefresh.setRefreshing(false);
                }
            }
        });

    }

    private void addGroupItem(GroupCreate createdGroup) {
        adapter.addData(createdGroup, infoList.size());
        //  每次创建后都更新一下本地存储的内容
//        Utility.setDataList(getContext(), SP_GROUP_LIST_KEY, infoList);

        Log.i(TAG, "addGroupItem: add an item");
        Log.i(TAG, "addGroupItem: list size " + infoList.size());
    }

    private void initRecycleView() {
        //  定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        //  将管理器配置给recyclerView
        listRcv.setLayoutManager(manager);
        //  设置adapter
        adapter = new ListRcvAdapter(getContext(), infoList);
        //  添加adapter
        listRcv.setAdapter(adapter);
        //  添加动画
        listRcv.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == getActivity().RESULT_OK) {
                    GroupCreate groupCreate = (GroupCreate) data.getSerializableExtra("group_info");
                    Log.i(TAG, "onActivityResult: " + groupCreate.toString());
                    //  新圈子信息添加到列表
                    addGroupItem(groupCreate);
                    //  存储到本地文件中
                    List<GroupCreate> addedList = Utility.getDataList(getContext(), StorageConfig.SP_KEY_USER_JOINED_CIRCLES, GroupCreate.class);
                    addedList.add(groupCreate);
                    Utility.setDataList(getContext(), StorageConfig.SP_KEY_USER_JOINED_CIRCLES, addedList);

                    //  刷新的方法写在adapter中了
                }
                break;
            default:
        }
    }

}
