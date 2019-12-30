package com.example.mobiledevproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.activity.ChangePasswordActivity;
import com.example.mobiledevproject.activity.FeedbackActivity;
import com.example.mobiledevproject.activity.HomeActivity;
import com.example.mobiledevproject.activity.LoginActivity;
import com.example.mobiledevproject.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyFragment extends Fragment {

    User user;
    @BindView(R.id.tv_my_username)
    TextView tvMyUsername;
    @BindView(R.id.tv_my_userdescription)
    TextView tvMyUserdescription;
    @BindView(R.id.tv_my_usergroup)
    TextView myGroup;
    @BindView(R.id.tv_my_password_change)
    TextView changePassword;
    @BindView(R.id.tv_my_seedback)
    TextView feedbackTV;
    @BindView(R.id.tv_my_settings)
    TextView exitTv;

    public MyFragment() {
        // Required empty public constructor
    }


    public static MyFragment newInstance(User user) {
        MyFragment fragment = new MyFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this,view);
        setUserInfo();

        myGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.bodyVp.setCurrentItem(0);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        feedbackTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);
            }
        });
        exitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });



        return view;
    }

    private void setUserInfo(){
        tvMyUsername.setText(this.user.getUserName());
    }

}
