package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mobiledevproject.MyApp;
import com.example.mobiledevproject.R;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;
import com.example.mobiledevproject.model.Group;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CircleFragment extends Fragment implements GetFragmentInfo {
    Unbinder unbinder;
    String title;
    Group group;
    MyApp myApp;


    public CircleFragment() {
        // Required empty public constructor
    }

    public static CircleFragment newInstance(String title, Group group) {
        CircleFragment fragment = new CircleFragment();
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
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        unbinder = ButterKnife.bind(this, view);

        myApp = (MyApp)getActivity().getApplication();

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
