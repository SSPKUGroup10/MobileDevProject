package com.example.mobiledevproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mobiledevproject.MyApp;
import com.example.mobiledevproject.R;
import com.example.mobiledevproject.activity.GroupActivity;
import com.example.mobiledevproject.activity.HomeActivity;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;
import com.example.mobiledevproject.model.Group;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.Utility;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ManageFragment extends Fragment implements GetFragmentInfo {
    Unbinder unbinder;
    String title, content;
    Group group;
    TextView checkinCalendar;
    TextView exitGroup;

    public ManageFragment() {
    }

    public static ManageFragment newInstance(String title, Group group) {
        ManageFragment fragment = new ManageFragment();
        fragment.title = title;
        fragment.group = group;
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
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        unbinder = ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkinCalendar = getActivity().findViewById(R.id.tv_manage_calendar);
        exitGroup = getActivity().findViewById(R.id.tv_manage_exit);
        checkinCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupActivity.contentsVp.setCurrentItem(1);
            }
        });

        exitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] flag = {false};
                MyApp app = (MyApp) getActivity().getApplication();
                User user = app.getUser();
                String userId = String.valueOf(user.getUserId());
                String token = Utility.getData(getActivity(), StorageConfig.SP_KEY_TOKEN);
                int groupId = group.getGroupId();
                String url = "http://172.81.215.104/api/v1/circles/"+groupId+"/members/";
                System.out.println(url);

                HttpUtil.deleteRequestWithToken(url, token, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        System.out.println(responseBody);
                        flag[0] = true;

                    }
                });
                while(!flag[0]){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(v.getContext(), "退出圈子成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),HomeActivity.class);
                intent.putExtra("user_info",user);
                startActivity(intent);
                getActivity().finish();
            }

        });
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
