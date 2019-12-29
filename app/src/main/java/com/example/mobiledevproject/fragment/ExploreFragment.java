package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.adapter.ListRcvAdapter;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.model.GroupCreate;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.util.Utility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ExploreFragment extends Fragment {

    private static final String TAG = "ExploreFragment";


    Unbinder unbinder;
    @BindView(R.id.et_explore_search)
    EditText searchEt;
    @BindView(R.id.rcv_explore_list)
    RecyclerView listRcv;
    @BindView(R.id.btn_explore_search)
    Button searchBtn;

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
//        viewSetOnClick();
        dataInit();

        return view;
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
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataInit();
            }
        });
    }



//    private Handler handler = new Handler(){
//        public void handleMessage(Message msg){
//            super.handleMessage(msg);
//            switch (msg.what){
//                case -1:
//                    Log.i(TAG, "handleMessage: "+(String)msg.obj);
//                    break;
//                case 1:
//                    String token = (String)msg.obj;
//                    updateToken(token);
//                    break;
//            }
//        }
//    };


//    public void updateToken(String token){
//        Utility.setData(getContext(), StorageConfig.SP_KEY_TOKEN, token);
//    }


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
