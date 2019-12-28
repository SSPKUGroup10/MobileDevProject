package com.example.mobiledevproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.activity.GroupActivity;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ManageFragment extends Fragment implements GetFragmentInfo {
    Unbinder unbinder;
    String title, content;
    TextView checkinCalendar;
    TextView exitGroup;
    public ManageFragment() {
    }

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
