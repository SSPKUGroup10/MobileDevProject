package com.example.mobiledevproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.model.GroupCreate;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.model.UserCreate;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    // Log打印的通用Tag
    private final String TAG = "LoginActivity";

    //声明UI对象
    EditText et_username = null;
    EditText et_password = null;
    Button bt_login = null;
    TextView tv_forget_password = null;
    TextView tv_to_register = null;
    ImageView iv_third_method1 = null;
    ImageView iv_third_method2 = null;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sp = this.getSharedPreferences("init_config", MODE_PRIVATE);
        viewInit();
        viewSetOnClick();
    }

    private void viewInit() {
        et_username = findViewById(R.id.password_old_et);
        et_password = findViewById(R.id.passworld_new1_et);
        bt_login = findViewById(R.id.bt_login);
        tv_to_register = findViewById(R.id.tv_to_register);
        tv_forget_password = findViewById(R.id.tv_forget_password);
    }

    private void viewSetOnClick() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        //跳转到注册页面
        tv_to_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent it_login_to_register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(it_login_to_register);
            }
        });

        //跳转到找回密码界面
        tv_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it_login_to_retrievepassword = new Intent(LoginActivity.this, RetrievePwdActivity.class);
                startActivity(it_login_to_retrievepassword);
            }

        });


        //登录按钮事件响应
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCreate info = getUserInfo();
                //上传json格式数据
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
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
                                        Intent it_login_to_home = new Intent(LoginActivity.this, HomeActivity.class);
                                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                                        String token = data.get("accessToken").getAsString();
                                        int userID = data.get("userID").getAsInt();

                                        info.setUserId(userID);
                                        User user = new User(info);


                                        JsonArray joinedCircles = data.get("joinedCircles").getAsJsonArray();
                                        JsonArray otherCircles = data.get("otherCircles").getAsJsonArray();

                                        for(JsonElement group : joinedCircles){
                                            JsonObject cur = group.getAsJsonObject();

                                            GroupCreate createdGroup = new GroupCreate();
                                            createdGroup.setGroupName(cur.get("name").getAsString());
                                            createdGroup.setDescription(cur.get("desc").getAsString());
                                            createdGroup.setCheckRule(cur.get("checkRule").getAsString());
                                            createdGroup.setMasterId(cur.get("circleMasterId").getAsInt());
                                            createdGroup.setStartAt(cur.get("startAt").getAsString());
                                            createdGroup.setEndAt(cur.get("endAt").getAsString());
                                            createdGroup.setType(cur.get("type").getAsString());
                                            createdGroup.setGroupId(cur.get("id").getAsInt());
                                            user.getJoinedCircles().add(createdGroup);

                                        }

                                        for(JsonElement group : otherCircles){
                                            JsonObject cur = group.getAsJsonObject();

                                            GroupCreate createdGroup = new GroupCreate();
                                            createdGroup.setGroupName(cur.get("name").getAsString());
                                            createdGroup.setDescription(cur.get("desc").getAsString());
                                            createdGroup.setCheckRule(cur.get("checkRule").getAsString());
                                            createdGroup.setMasterId(cur.get("circleMasterId").getAsInt());
                                            createdGroup.setStartAt(cur.get("startAt").getAsString());
                                            createdGroup.setEndAt(cur.get("endAt").getAsString());
                                            createdGroup.setType(cur.get("type").getAsString());
                                            createdGroup.setGroupId(cur.get("id").getAsInt());
                                            user.getOtherCircles().add(createdGroup);
                                        }

                                        it_login_to_home.putExtra("token", token);
                                        it_login_to_home.putExtra("user_info", user);
                                        Log.i(TAG, "run: "+user.toString());

//                                        SharedPreferences.Editor editor = sp.edit();
//                                        editor.putBoolean("logon", true);
//                                        editor.commit();


                                        startActivity(it_login_to_home);
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



    private UserCreate getUserInfo() {
        UserCreate userCreate = new UserCreate();
        userCreate.setUserName(et_username.getText().toString());
        userCreate.setPassword(et_password.getText().toString());
        return userCreate;
    }

}
