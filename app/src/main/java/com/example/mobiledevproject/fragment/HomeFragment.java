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

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.activity.CreateGroupActivity;
import com.example.mobiledevproject.adapter.ListRcvAdapter;
import com.example.mobiledevproject.model.GroupCreate;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.util.Utility;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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

    public HomeFragment() {
    }

    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataInit();
        initRecycleView();
        viewSetOnClick();

    }

    private void dataInit() {

        if(Utility.hasSpItem(getContext(), SP_GROUP_LIST_KEY)){
            infoList = Utility.getDataList(getContext(), SP_GROUP_LIST_KEY, GroupCreate.class);
        } else {
            //  从User中读取信息
            infoList = user.getJoinedCircles();
            Utility.setDataList(getContext(), SP_GROUP_LIST_KEY, infoList);
        }
    }

    private void viewSetOnClick() {
        groupsMcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 创建一个圈子");
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void addGroupItem(GroupCreate createdGroup) {
        adapter.addData(createdGroup, infoList.size());
        //  每次创建后都更新一下本地存储的内容
        Utility.setDataList(getContext(), SP_GROUP_LIST_KEY, infoList);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == getActivity().RESULT_OK) {
                    GroupCreate createdGroup = (GroupCreate) data.getSerializableExtra("group_info");
                    Log.i(TAG, "onActivityResult: "+createdGroup.toString());
                    //  新圈子信息添加到列表
                    addGroupItem(createdGroup);
                    //  刷新界面
                    //  刷新的方法写在adapter中了
                }
                break;
            default:
        }
    }

}
