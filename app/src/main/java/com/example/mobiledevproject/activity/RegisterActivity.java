package com.example.mobiledevproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.model.UserCreate;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    // Log打印的通用Tag
    private final String TAG = "RegisterActivity";

    //声明UI对象
    EditText et_username=null;
    EditText et_password=null;
    EditText et_password2=null;
    Button bt_submit_register=null;
    TextView tv_serviceagreement =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewInit();
        viewSetOnClick();
    }
    private void viewInit() {
        et_username = findViewById(R.id.password_old_et);
        et_password = findViewById(R.id.passworld_new1_et);
        et_password2 = findViewById(R.id.et_password2);
        bt_submit_register = findViewById(R.id.bt_submit_register);
        tv_serviceagreement = findViewById(R.id.tv_service_agreement);
    }

    private void viewSetOnClick(){
        String username=et_username.getText().toString();
        String password=et_password.getText().toString();
        tv_serviceagreement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent it_register_to_serviceagreement = new Intent(RegisterActivity.this, ServiceAgreementActivity.class);
                startActivity(it_register_to_serviceagreement);
            }

        });

        //注册按钮事件响应
        bt_submit_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCreate info = getUserInfo();
                if(info==null){
                    Toast.makeText(RegisterActivity.this,"两次密码输入不一致，请重新输入",Toast.LENGTH_SHORT).show();
                    return;
                }

                //上传json格式数据
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String jsonInfo= gson.toJson(info);
                Log.i(TAG, "onClick: "+jsonInfo);

                HttpUtil.postOkHttpRequest(API.REGISTER, jsonInfo, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "onFailure: 网络无响应");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String responseBody = response.body().string();
                        Log.i(TAG, "onResponse: "+ responseBody);

                        JsonObject jsonObject;
                        if ((jsonObject = StatusCodeUtil.isNormalResponse(responseBody)) != null) {
                            int status = jsonObject.get("status").getAsInt();
                            if (StatusCodeUtil.isNormalStatus(status)) {
                                //  正确
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i(TAG, "run: 注册成功");
                                        Intent intentRegToLogin = new Intent();
                                        intentRegToLogin.putExtra("user_name", info.getUserName());
                                        intentRegToLogin.putExtra("password", info.getPassword());
                                        setResult(RESULT_OK, intentRegToLogin);
                                        finish();
                                    }
                                });
                            } else {
                                Log.i(TAG, "onResponse: " + status);
                            }
                        } else {
                            Log.i(TAG, "onResponse: 响应内容错误");
                        }
                    }
                });
            }
        });
    }

    private UserCreate getUserInfo(){
        if((et_password.getText().toString().equals(et_password2.getText().toString()))) {
            return new UserCreate(et_username.getText().toString(), et_password.getText().toString());
        }
        else {
            return null;
        }
    }
}
