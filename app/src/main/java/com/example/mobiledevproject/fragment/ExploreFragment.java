package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.adapter.ListRcvAdapter;
import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.model.GroupCreate;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.model.UserCreate;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.example.mobiledevproject.util.Utility;
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


public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";


    Unbinder unbinder;
    @BindView(R.id.et_explore_search)
    EditText searchEt;
    @BindView(R.id.rcv_explore_list)
    RecyclerView listRcv;
    @BindView(R.id.btn_explore_search)
    Button searchBtn;
    @BindView(R.id.srl_explore_fragment_refresh)
    SwipeRefreshLayout srlExploreFragmentRefresh;

    ListRcvAdapter adapter;
    List<GroupCreate> infoList;


    public User user;

    public ExploreFragment() {
    }

    public static ExploreFragment newInstance(User user) {
        ExploreFragment fragment = new ExploreFragment();
        fragment.user = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        unbinder = ButterKnife.bind(this, view);
        viewSetOnClick();
        dataInit();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onActivityCreated: 进行界面初始化");
        srlExploreFragmentRefresh.setRefreshing(true);
        refreshCircleInfo();
    }


    private void dataInit() {
        if(infoList!=null){
            infoList.clear();
        }
        infoList = Utility.getDataList(getContext(), StorageConfig.SP_KEY_USER_OTHER_CIRCLES, GroupCreate.class);
        initRecycleView();

    }

    //  暂时用不到
    private void viewSetOnClick() {

//        searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dataInit();
//            }
//        });

        srlExploreFragmentRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                srlExploreFragmentRefresh.setRefreshing(false);
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

                        Utility.setDataList(getContext(), StorageConfig.SP_KEY_USER_OTHER_CIRCLES, newUser.getOtherCircles());

                        Log.i(TAG, "run: "+newUser.toString());
                        Log.i(TAG, "onResponse: 刷新完成");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataInit();
                                adapter.notifyDataSetChanged();
                                srlExploreFragmentRefresh.setRefreshing(false);
                            }
                        });
                    } else {
                        Log.i(TAG, "onResponse: " + status);
                        srlExploreFragmentRefresh.setRefreshing(false);
                    }
                } else {
                    Log.i(TAG, "onResponse: 响应内容错误");
                    srlExploreFragmentRefresh.setRefreshing(false);
                }
            }
        });

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
}
