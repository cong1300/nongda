package com.xinnuo.apple.nongda.studentActivity.OutDoorSports;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xinnuo.apple.nongda.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OutDoorSportsActivity extends AppCompatActivity {
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_door_sports);
        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");

    }
    //运动开始
    public void onClickSports(View view){
        Intent intent = new Intent(this, SportActivity.class);
        intent.putExtra("studentId",studentId);
        startActivity(intent);
    }
    //运动量
    public void onClickSportsRecords(View view){
        Intent intent = new Intent(this, SportRecordsActivity.class);
        intent.putExtra("studentId",studentId);
        startActivity(intent);
    }

}
