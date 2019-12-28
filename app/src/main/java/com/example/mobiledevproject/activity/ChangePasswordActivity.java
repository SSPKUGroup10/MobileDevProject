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
import com.example.mobiledevproject.model.User;

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
                        MyApp app = (MyApp) getApplication();
                        User user = app.getUser();



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
