package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public class GroupCheckinFragment extends Fragment implements GetFragmentInfo {

    Unbinder unbinder;
    String title, content;


    public GroupCheckinFragment() {
        // Required empty public constructor
    }

    //  单例模式
    public static GroupCheckinFragment newInstance(String title, String content) {
        GroupCheckinFragment fragment = new GroupCheckinFragment();
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
        View view = inflater.inflate(R.layout.fragment_group_checkin, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
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
