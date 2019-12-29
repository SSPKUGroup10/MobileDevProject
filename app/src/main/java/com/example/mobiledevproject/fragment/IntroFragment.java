package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;
import com.example.mobiledevproject.model.Group;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroFragment extends Fragment implements GetFragmentInfo {


    String masterName;
    Group group;
    @BindView(R.id.tv_intro_master)
    TextView tvIntroMaster;
    @BindView(R.id.tv_intro_desc)
    TextView tvIntroDesc;
    @BindView(R.id.tv_intro_time)
    TextView tvIntroTime;
    @BindView(R.id.tv_intro_rule)
    TextView tvIntroRule;

    private static final String TAG = "IntroFragment";

    public IntroFragment() {
    }

    //  单例模式
    public static IntroFragment newInstance(String masterName, Group info) {
        IntroFragment fragment = new IntroFragment();
        fragment.masterName = masterName;
        fragment.group = info;
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
        View view = inflater.inflate(R.layout.fragment_intro, container, false);
        ButterKnife.bind(this, view);
        viewInit();

        return view;
    }

    private void viewInit(){
        Log.i(TAG, "viewInit: "+group.toString());

        tvIntroMaster.setText(masterName);
        tvIntroDesc.setText(group.getDescription());
        String a = group.getStartAt();
        String b = group.getEndAt();
        a = a.split(" ")[1];
        b = b.split(" ")[1];

        String seTime = a+"~"+b;
        tvIntroTime.setText(seTime);
        tvIntroRule.setText(group.getCheckRule());

    }

    @Override
    public String getTitle() {
        return "简介";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unbinder.unbind();
    }
}
