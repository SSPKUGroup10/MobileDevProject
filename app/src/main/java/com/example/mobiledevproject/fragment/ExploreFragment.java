package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.activity.HomeActivity;
import com.example.mobiledevproject.adapter.ListRcvAdapter;
import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.model.GroupCreate;
import com.example.mobiledevproject.model.UserCreate;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.example.mobiledevproject.util.Utility;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";
    public static final String SP_GROUP_LIST_KEY = "group_list";


    Unbinder unbinder;
    @BindView(R.id.et_explore_search)
    EditText searchEt;
    @BindView(R.id.rcv_explore_list)
    RecyclerView listRcv;

    ListRcvAdapter adapter;
    List<GroupCreate> infoList;
    @BindView(R.id.btn_explore_search)
    Button serarchBtn;

    public ExploreFragment() {
        // Required empty public constructor
    }

    public static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewSetOnClick();
        dataInit();
        initRecycleView();
    }

    private void viewSetOnClick() {
        serarchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataInit();
            }
        });
    }

    private void dataInit() {
        if(infoList!=null){
            listRcv.removeAllViews();
            adapter.notifyDataSetChanged();
        }
        infoList = new ArrayList<>();

        //  从数据库中读取groups
        String address = API.CIRCLE;
        String responsestr;

        //  user信息
        final UserCreate currentUser = ((HomeActivity) getActivity()).user;
        //  本地存储的token信息
        String token = Utility.getData(getContext(), StorageConfig.SP_KEY_TOKEN);
        HttpUtil.getRequestWithToken(address, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.i(TAG, "onResponse: " + responseBody);
                JsonObject jsonObject;

                //  判断是否得到正确的请求
                if((jsonObject=StatusCodeUtil.isNormalResponse(responseBody))!=null){
                    int status = jsonObject.get("status").getAsInt();
                    if(StatusCodeUtil.isNormalStatus(status)){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseGroupList(responseBody);
                            }
                        });
                    } else {
                        //  如果token失效，重新申请一个
                        if(StatusCodeUtil.isTokenError(status)){
                            Log.i(TAG, "onResponse: "+"token失效，已经重新生成");
                            HttpUtil.getToken(currentUser, handler);
                        }
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: "+"网络请求错误");
                        }
                    });
                }

            }
        });
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case -1:
                    Log.i(TAG, "handleMessage: "+(String)msg.obj);
                    break;
                case 1:
                    String token = (String)msg.obj;
                    updateToken(token);
                    break;
            }
        }
    };


    public void updateToken(String token){
        Utility.setData(getContext(), StorageConfig.SP_KEY_TOKEN, token);
    }

    public void parseGroupList(String responseBody){
        JsonObject jsonObject = (JsonObject)new JsonParser().parse(responseBody);
        JsonArray data = jsonObject.get("data").getAsJsonArray();
        for(JsonElement group : data){
            JsonObject cur = group.getAsJsonObject();

            GroupCreate createdGroup = new GroupCreate();
            createdGroup.setGroupName(cur.get("name").getAsString());
            createdGroup.setDescription(cur.get("desc").getAsString());
            addGroupItem(createdGroup);
        }
    }


    private void addGroupItem(GroupCreate createdGroup) {
        adapter.addData(createdGroup, infoList.size());
        Utility.setDataList(getContext(), "group_list", infoList);

        Log.i(TAG, "addGroupItem: add an item");
        Log.i(TAG, "addGroupItem: list size " + infoList.size());
    }


    private void initRecycleView() {
        //  定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
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
