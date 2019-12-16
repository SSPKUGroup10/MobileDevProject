package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.adapter.CircleAdapter;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;
import com.example.mobiledevproject.model.UserBean;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.mobiledevproject.config.StorageConfig.SP_KEY_TOKEN;

public class CircleFragment extends Fragment implements GetFragmentInfo {
    Unbinder unbinder;
    String title, content;


    public CircleFragment() {
        // Required empty public constructor
    }

    public static CircleFragment newInstance(String title, String content) {
        CircleFragment fragment = new CircleFragment();
        fragment.title = title;
        fragment.content = content;
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
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        unbinder = ButterKnife.bind(this, view);


        initView(view);


        return view;
    }

    public void initView(View view) {
        UserBean userBean = new UserBean("001","德玛西亚","data");
        List<UserBean> data = new ArrayList<>();
        data.add(userBean);
        CircleAdapter adapterCheckin = new CircleAdapter(getContext(),data);
        CircleAdapter adapterNotCheckin = new CircleAdapter(getContext(),data);
        GridView checkin = view.findViewById(R.id.circle_has_checkin_gv);
        GridView notCheckin = view.findViewById(R.id.circle_hasnot_checkin_gv);
        checkin.setAdapter(adapterCheckin);
        notCheckin.setAdapter(adapterNotCheckin);
    }

    public List<UserBean> getCheckInformation(String groupId) {
        String token = Utility.getData(this.getContext(),SP_KEY_TOKEN );
        String circle_id = "1";
        String address = "http://172.81.215.104/api/v1/circles/"+circle_id+"/clockin/";
        final boolean[] flag = {false};
        HttpUtil.getOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                flag[0] = false;
                System.out.println("获取打卡信息失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                flag[0] = true;

            }
        });
        return new ArrayList<>();
    }

//    public List<UserBean> getCheckInformation() {
//
//
//    }





    @Override
    public String getTitle(){
        return title;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
