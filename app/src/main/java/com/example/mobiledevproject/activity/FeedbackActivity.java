package com.example.mobiledevproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobiledevproject.R;

public class FeedbackActivity extends AppCompatActivity {
    EditText feedbackET;
    Button feedbackBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        feedbackET = findViewById(R.id.feedback_et);
        feedbackBTN = findViewById(R.id.feedbcak_commit_btn);
        feedbackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = feedbackET.getText().toString();
                System.out.println(text);
                if (text == null || text == "") {
                    Toast.makeText(v.getContext(), "反馈内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    //后续完善网络部分
                    Toast.makeText(v.getContext(), "感谢反馈", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }
}
