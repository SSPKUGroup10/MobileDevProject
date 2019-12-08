package com.example.mobiledevproject.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.adapter.PhotoAdapter;
import com.example.mobiledevproject.adapter.DynamicAdapter;
import com.example.mobiledevproject.interfaces.GetFragmentInfo;
import com.example.mobiledevproject.model.MessageBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_checkin, container, false);
        unbinder = ButterKnife.bind(this, view);
        initDynamic(view);
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

    private void initDynamic(View view) {
        String path = "0011";
        try {

            String AbsolutePath= getContext().getFilesDir().toString();
            File file = new File(AbsolutePath +"/" + path);
            System.out.println(file.exists());
            if (file.exists()) {
                System.out.println("=================aaaaaaa============");
                List<MessageBean> dynamicData;
                FileInputStream fileInputStream = getContext().openFileInput(path);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                dynamicData = (ArrayList)objectInputStream.readObject();
                System.out.println(dynamicData.size());
                List<String> data = null;
                System.out.println("=================1============");
                fileInputStream.close();
                objectInputStream.close();

                DynamicAdapter dynamicAdapter = new DynamicAdapter(getContext(), dynamicData);
                RecyclerView recyclerView = view.findViewById(R.id.group_message_rv);
                System.out.println("=================2============");
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                System.out.println("=================3============");
                recyclerView.setAdapter(dynamicAdapter);
                System.out.println("=================4============");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
