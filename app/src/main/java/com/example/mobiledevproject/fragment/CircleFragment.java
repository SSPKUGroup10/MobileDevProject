package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.mobiledevproject.MyApp;
import com.example.mobiledevproject.R;
import com.example.mobiledevproject.adapter.CircleAdapter;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.model.UserBean;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
    MyApp myApp;


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


        myApp = (MyApp) getActivity().getApplication();


        return view;
    }

    public void initView(View view) {

        UserBean userBean = new UserBean("001", "德玛西亚", "data");
        List<UserBean> checkinMembers = new ArrayList<>();
        List<UserBean> notCheckinMembers = new ArrayList<>();
        if (!getCheckInformation("1", checkinMembers, notCheckinMembers)) {
            return;
        }
        System.out.println(checkinMembers.size());
        System.out.println(notCheckinMembers.size());
        CircleAdapter adapterCheckin = new CircleAdapter(getContext(), checkinMembers);
        CircleAdapter adapterNotCheckin = new CircleAdapter(getContext(), notCheckinMembers);
        GridView checkin = view.findViewById(R.id.circle_has_checkin_gv);
        GridView notCheckin = view.findViewById(R.id.circle_hasnot_checkin_gv);
        checkin.setAdapter(adapterCheckin);
        notCheckin.setAdapter(adapterNotCheckin);

    }

    public boolean getCheckInformation(String groupId, List<UserBean> checkinMembers, List<UserBean> notCheckinMembers) {

        String circle_id = groupId;
        String address = "http://172.81.215.104/api/v1/circles/" + circle_id + "/clockinMembers/";
        final boolean[] flag = {false};
        //String token = Utility.getData(this, StorageConfig.SP_KEY_TOKEN);

        String token = Utility.getData(this.getContext(), SP_KEY_TOKEN);

                HttpUtil.getRequestWithToken(address, token, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        flag[0] = false;
                        System.out.println("获取打卡信息失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        flag[0] = true;

                        JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
                        JsonObject data = jsonObject.getAsJsonObject("data");

                        JsonArray checkin = data.getAsJsonArray("clockinsMembers");
                        for (int i = 0; i < checkin.size(); ++i) {
                            JsonObject item = checkin.get(i).getAsJsonObject();
                            //   System.out.println(item);
                            String userId;
                            if (item.get("userId") == null) {
                                userId = null;
                            } else {
                                userId = item.getAsJsonPrimitive("userId").toString();
                            }
                            String username;
                            if (item.get("username") == null) {
                                username = null;
                            } else {
                                username = item.getAsJsonPrimitive("username").toString();
                            }

                            String userAvatar;
                            //  System.out.println(item.get("userAvatar"));
                            if (item.get("userAvatar").isJsonNull()) {
                                userAvatar = null;
                            } else {
                                userAvatar = item.getAsJsonPrimitive("userAvatar").toString();
                            }

                            checkinMembers.add(new UserBean(userId, username, userAvatar));
                        }

                        JsonArray notCheckin = data.getAsJsonArray("notClockinsMembers");
                        for (int i = 0; i < notCheckin.size(); ++i) {
                            JsonObject item = notCheckin.get(i).getAsJsonObject();
                            //   System.out.println(item);
                            String userId;
                            if (item.get("userId") == null) {
                                userId = null;
                            } else {
                                userId = item.getAsJsonPrimitive("userId").toString();
                            }
                            String username;
                            if (item.get("username") == null) {
                                username = null;
                            } else {
                                username = item.getAsJsonPrimitive("username").toString();
                            }

                            String userAvatar;
                            //    System.out.println(item.get("userAvatar"));
                            if (item.get("userAvatar").isJsonNull()) {
                                userAvatar = null;
                            } else {
                                userAvatar = item.getAsJsonPrimitive("userAvatar").toString();
                            }
                            notCheckinMembers.add(new UserBean(userId, username, userAvatar));
                        }
                    }
                });


        try {
            Thread.sleep(500);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag[0];
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
