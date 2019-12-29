package com.example.mobiledevproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobiledevproject.MyApp;
import com.example.mobiledevproject.R;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    TextView passwordOldTV;
    TextView passwordNew1TV;
    TextView passwordNew2TV;
    EditText passwordOldET;
    EditText passwordNew1ET;
    EditText passwordNew2ET;
    Button passwordCommit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
    }

    public void initView() {
        passwordOldTV = findViewById(R.id.password_old_tv);
        passwordOldET = findViewById(R.id.password_old_et);
        passwordNew1TV = findViewById(R.id.password_new1_tv);
        passwordNew1ET = findViewById(R.id.password_new1_et);
        passwordNew2TV = findViewById(R.id.password_new2_tv);
        passwordNew2ET = findViewById(R.id.password_new2_et);
        passwordCommit = findViewById(R.id.password_commit_btn);
        passwordCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = passwordOldET.getText().toString();
                String newPassword1 = passwordNew1ET.getText().toString();
                String newPassword2 = passwordNew2ET.getText().toString();
                if (oldPassword == null || oldPassword == "" || newPassword1 == null || newPassword1 == "" || newPassword2 == null || newPassword2 == "") {
                    Toast.makeText(v.getContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (newPassword1.equals(newPassword2)) {
                    if (checkOldPassword()) {
                        final boolean []flag ={false};
                        MyApp app = (MyApp) getApplication();
                        User user = app.getUser();
                        String userId = String.valueOf(user.getUserId());
                        String token = Utility.getData(ChangePasswordActivity.this, StorageConfig.SP_KEY_TOKEN);

                        String url = "http://172.81.215.104/api/v1/users/i";
                        System.out.println(url);
                        String data = "{\"password\":\""+newPassword1+"\"}";
                        System.out.println(data);
                        HttpUtil.putRequestWithToken(url, token,data, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseBody = response.body().string();
                                System.out.println(responseBody);
                                flag[0]=true;

                            }
                        });
                        while(!flag[0]){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(v.getContext(), "修改密码成功", Toast.LENGTH_SHORT).show();
                        finish();

                    } else{
                        Toast.makeText(v.getContext(), "旧密码错误", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(v.getContext(), "两次密码输入不一样", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    public boolean checkOldPassword(){
        String oldPassword = passwordOldET.getText().toString();

        return true;
    }
}
