package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ManageFragment extends Fragment implements GetFragmentInfo {


    @BindView(R.id.tv_manage_content)
    TextView contentTv;

    Unbinder unbinder;
    String title, content;


    public ManageFragment() {
        // Required empty public constructor
    }

    //  单例模式
    public static ManageFragment newInstance(String title, String content) {
        ManageFragment fragment = new ManageFragment();
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
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        unbinder = ButterKnife.bind(this, view);
        contentTv.setText(content);
        return view;
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
