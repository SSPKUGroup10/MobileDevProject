package com.example.mobiledevproject.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledevproject.R;
import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.model.GroupCreate;
import com.example.mobiledevproject.model.User;
import com.example.mobiledevproject.model.UserCreate;
import com.example.mobiledevproject.util.HttpUtil;
import com.example.mobiledevproject.util.StatusCodeUtil;
import com.example.mobiledevproject.util.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateGroupActivity extends AppCompatActivity {

    @BindView(R.id.et_cg_name)
    EditText etCgName;
    @BindView(R.id.et_cg_description)
    EditText etCgDescription;
    @BindView(R.id.btn_cg_startat)
    Button btnCgStartat;
    @BindView(R.id.tv_cg_startat)
    TextView tvCgStartat;
    @BindView(R.id.btn_cg_endat)
    Button btnCgEndat;
    @BindView(R.id.tv_cg_endat)
    TextView tvCgEndat;
    @BindView(R.id.btn_cg_commit)
    Button btnCgCommit;
    @BindView(R.id.et_cg_rule)
    EditText etCgRule;
    @BindView(R.id.spin_cg_type)
    Spinner spinCgType;

    private Calendar calendar;

    private static final String TAG = "CreateGroupActivity";

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        calendar = Calendar.getInstance();
        viewSetOnClick();
    }

    private void viewSetOnClick() {
        //  选择框
        spinCgType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //  选择开始时间
        btnCgStartat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(CreateGroupActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.i(TAG, "onClick: " + hourOfDay);
                        Log.i(TAG, "onClick: " + minute);
                        tvCgStartat.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, true).show();
            }
        });
        //  选择结束时间
        btnCgEndat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(CreateGroupActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.i(TAG, "onClick: " + hourOfDay);
                        Log.i(TAG, "onClick: " + minute);
                        tvCgEndat.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, true).show();
            }
        });

        btnCgCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gsonEx = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                Intent intentFromHome = getIntent();
                GroupCreate groupSrc = getCreateInfo();
                //  user信息用于给group添加masterId，并且在需要token的时候用user信息来重新申请
                User userSrc = (User) intentFromHome.getSerializableExtra("user");
                UserCreate user = new UserCreate(userSrc);

                groupSrc.setMasterId(user.getUserId());
                String groupInfo = gsonEx.toJson(groupSrc);
                String userInfo = gsonEx.toJson(user);
                String token = Utility.getData(CreateGroupActivity.this, StorageConfig.SP_KEY_TOKEN);

                Log.i(TAG, "onClick: " + groupInfo);
                Log.i(TAG, "onClick: " + userInfo);
                Log.i(TAG, "onClick: " + token);

                HttpUtil.postRequestWithToken(API.CIRCLE, token, groupInfo, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String responseBody = response.body().string();
                        Log.i(TAG, "onResponse: " + responseBody);
                        System.out.println("=====================");
                        System.out.println(responseBody);
                        JsonObject jsonObject;
                        if ((jsonObject = StatusCodeUtil.isNormalResponse(responseBody)) != null) {
                            int status = jsonObject.get("status").getAsInt();
                            if (StatusCodeUtil.isNormalStatus(status)) {
                                JsonObject data = jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject();
                                int groupId = data.get("id").getAsInt();
                                groupSrc.setGroupId(groupId);

                                Log.i(TAG, "onResponse: 圈子已创建");
                                Log.i(TAG, "onResponse: " + groupId);


                                Intent intentBackHome = new Intent();
                                intentBackHome.putExtra("group_info", groupSrc);
                                setResult(RESULT_OK, intentBackHome);
                                finish();
                            } else if (StatusCodeUtil.isTokenError(status)) {
                                //  重新申请一下token
                                Log.i(TAG, "onResponse: " + "token失效");
                                HttpUtil.getToken(user, handler);
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:
                    Log.i(TAG, "handleMessage: " + (String) msg.obj);
                    break;
                case 1:
                    String token = (String) msg.obj;
                    Log.i(TAG, "handleMessage: " + token);
                    Log.i(TAG, "handleMessage: token已更新");
                    updateToken(token);
                    break;
            }
        }
    };

    public void updateToken(String token) {
        Utility.setData(CreateGroupActivity.this, StorageConfig.SP_KEY_TOKEN, token);
    }

    private GroupCreate getCreateInfo() {
        GroupCreate groupCreate = new GroupCreate();
        groupCreate.setGroupName(etCgName.getText().toString());
        groupCreate.setType(type);
        groupCreate.setDescription(etCgDescription.getText().toString());
        groupCreate.setCheckRule(etCgRule.getText().toString());
        //  静态数据
        groupCreate.setStartAt("2020-10-24 11:11:11");
        groupCreate.setEndAt("2020-10-24 11:11:11");

//        groupCreate.setStartAt(tvCgStartat.getText().toString());
//        groupCreate.setEndAt(tvCgEndat.getText().toString());
        return groupCreate;
    }

}
