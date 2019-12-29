package com.example.mobiledevproject.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.model.UserCreate;
import com.example.mobiledevproject.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class RetrievePwdActivity extends AppCompatActivity {

    // Log打印的通用Tag
    private final String TAG = "RetrievePwdActivity";

    //声明UI对象
    EditText et_username=null;
    EditText et_password=null;
    EditText et_password2=null;
    Button bt_submit_retrieve=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_pwd);

        viewInit();
        viewSetOnClick();

        super.onCreate(savedInstanceState);
    }

    private void viewInit() {
        et_username = findViewById(R.id.password_old_et);
        et_password = findViewById(R.id.passworld_new1_et);
        et_password2 = findViewById(R.id.et_password2);
        bt_submit_retrieve = findViewById(R.id.bt_submit_retrieve);
    }

    private void viewSetOnClick() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        bt_submit_retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserCreate info = getUserInfo();
                User user = new User(info);
                String url = "https://zxzx.applinzi.com/api/v1/auth/login";

                //上传json格式数据
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String jsonInfo= gson.toJson(user);
                Log.i(TAG, "onClick: "+jsonInfo);


                HttpUtil.postOkHttpRequest(url, jsonInfo, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String responseBody = response.body().string();
                        Log.i(TAG, "onResponse: "+ responseBody);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RetrievePwdActivity.this, "获得响应",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


                finish();
            }
        });
    }


    private UserCreate getUserInfo(){
        if((et_password.getText().toString().equals(et_password2.getText().toString()))) {
            return new UserCreate(et_username.getText().toString(), et_password.getText().toString());
        }
        else {
            Toast.makeText(RetrievePwdActivity.this,"两次密码输入不一致，注册失败",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            String url = "https://zxzx.applinzi.com/api/v1/auth/login";

            UserCreate info = getUserInfo();
            User user = new User(info);

            //上传json格式数据
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String jsonInfo= gson.toJson(user);


            HttpUtil.postOkHttpRequest(url, jsonInfo, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i(TAG, "onResponse: "+response.body().string());
                }
            });
        }
    };

}
