package com.example.mobiledevproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.model.UserCreate;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.example.mobiledevproject.util.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    // Log打印的通用Tag
    private final String TAG = "LoginActivity";

    SharedPreferences sp;

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.tv_to_register)
    TextView tvToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

//        sp = this.getSharedPreferences("init_config", MODE_PRIVATE);
        viewSetOnClick();
    }


    private void viewSetOnClick() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        //跳转到注册页面
        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoginToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intentLoginToRegister, 2);
            }
        });

        //跳转到找回密码界面
        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoginToRetrievePassword = new Intent(LoginActivity.this, RetrievePwdActivity.class);
                startActivity(intentLoginToRetrievePassword);
            }
        });


        //登录按钮事件响应
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

                UserCreate info = getUserInfo();
                String jsonInfo = gson.toJson(info);

                HttpUtil.postOkHttpRequest(API.LOGIN, jsonInfo, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "onFailure: 网络请求错误");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string();
                        Log.i(TAG, "onResponse: " + responseBody);

                        JsonObject jsonObject;
                        if ((jsonObject = StatusCodeUtil.isNormalResponse(responseBody)) != null) {
                            int status = jsonObject.get("status").getAsInt();
                            if (StatusCodeUtil.isNormalStatus(status)) {
                                //  正确
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "登录成功",
                                                Toast.LENGTH_SHORT).show();
                                        //登录成功后跳转到HomeActivity，通过intent把user和token的信息传过来，user用类传递，token用string
                                        Intent intentLoginToHome = new Intent(LoginActivity.this, HomeActivity.class);

                                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                                        String token = data.get("accessToken").getAsString();
                                        int userID = data.get("userID").getAsInt();
                                        info.setUserId(userID);
                                        //  创建User类对象来存储圈子列表
                                        User user = new User(info);

                                        JsonArray joinedCircles = data.get("joinedCircles").getAsJsonArray();
                                        JsonArray otherCircles = data.get("otherCircles").getAsJsonArray();

                                        //  加载圈子列表
                                        for (JsonElement group : joinedCircles) {
                                            JsonObject cur = group.getAsJsonObject();
                                            user.getJoinedCircles().add(Utility.setGroupInfo(cur));
                                        }
                                        for (JsonElement group : otherCircles) {
                                            JsonObject cur = group.getAsJsonObject();
                                            user.getOtherCircles().add(Utility.setGroupInfo(cur));
                                        }

                                        //  信息存储，这样就不用传输了
                                        storageUserInfo(user, token);
                                        intentLoginToHome.putExtra("user_info", user);
                                        startActivity(intentLoginToHome);
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

    private void storageUserInfo(User user, String token){
        StorageConfig.SP_NAME = user.getUserName();

        Utility.setData(LoginActivity.this, StorageConfig.SP_KEY_USER_NAME, user.getUserName());
        Utility.setData(LoginActivity.this, StorageConfig.SP_KEY_USER_ID, user.getUserId());
        Utility.setData(LoginActivity.this, StorageConfig.SP_KEY_PASSWORD, user.getPassword());
        Utility.setDataList(LoginActivity.this, StorageConfig.SP_KEY_USER_JOINED_CIRCLES, user.getJoinedCircles());
        Utility.setDataList(LoginActivity.this, StorageConfig.SP_KEY_USER_OTHER_CIRCLES, user.getOtherCircles());

        Utility.setData(LoginActivity.this, StorageConfig.SP_KEY_TOKEN, token);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    String userName = data.getStringExtra("user_name");
                    String password = data.getStringExtra("password");
                    etUsername.setText(userName);
                    etPassword.setText(password);
                }
                break;
            default:
        }
    }




    private UserCreate getUserInfo() {
        UserCreate userCreate = new UserCreate();
        userCreate.setUserName(etUsername.getText().toString());
        userCreate.setPassword(etPassword.getText().toString());
        return userCreate;
    }

}
