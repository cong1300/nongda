package com.xinnuo.apple.nongda.TeacherActivity.SportsStandards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.xinnuo.apple.nongda.R;

public class RunActivity extends AppCompatActivity {
    private String id;
    private String sportkindSex;
    private String sportkindName;
    private String status,state;
    private Button Stopwatch,Manual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        sportkindSex = intent.getStringExtra("sportkindSex");
        sportkindName = intent.getStringExtra("sportkindName");
        status = intent.getStringExtra("status");
        state = intent.getStringExtra("state");
        Stopwatch = (Button) findViewById(R.id.Stopwatch);
        Manual = (Button) findViewById(R.id.Manual);
        //秒表录分
        Stopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(RunActivity.this,SportSecondActivity.class);
                intent1.putExtra("id",id);
                intent1.putExtra("sportkindSex",sportkindSex);
                intent1.putExtra("sportkindName",sportkindName);
                intent1.putExtra("state","1");
                intent1.putExtra("status",status);
                startActivity(intent1);
            }
        });
        Manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //传值 班级id 所教班级id
                Intent intent2 = new Intent(RunActivity.this,Sport_Class.class);
                intent2.putExtra("id",id);
                intent2.putExtra("sportkindSex",sportkindSex);
                intent2.putExtra("sportkindName",sportkindName);
                intent2.putExtra("state","2");
                intent2.putExtra("status",status);
                startActivity(intent2);
            }
        });
    }
}
