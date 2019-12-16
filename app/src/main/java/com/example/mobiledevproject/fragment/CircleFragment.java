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

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

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
