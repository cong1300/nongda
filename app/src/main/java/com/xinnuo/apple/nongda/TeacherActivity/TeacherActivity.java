package com.xinnuo.apple.nongda.TeacherActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.TeacherActivity.AccountSettings.AccountSettingsActivity;
import com.xinnuo.apple.nongda.TeacherActivity.AttendanceDataStatistics.AttendanceDataStatisticsActivity;
import com.xinnuo.apple.nongda.TeacherActivity.CourseAttendance.Class_ListActivity;
import com.xinnuo.apple.nongda.TeacherActivity.Meeting.MeetingInfoActivity;
import com.xinnuo.apple.nongda.TeacherActivity.Resultquery.ResultQueryActivity;
import com.xinnuo.apple.nongda.TeacherActivity.SportsStandards.SportsStandards;
import com.xinnuo.apple.nongda.TeacherActivity.Substitute.SubstituteActivity;
import com.xinnuo.apple.nongda.TeacherActivity.WorkPlan.TeacherWorkPlan;
import com.xinnuo.apple.nongda.TeacherActivity.club.TeacherClubActivity;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.studentActivity.News.StuNews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherActivity extends BaseActivity {
    //定义控件
    private TextView teacher_name;
    private TextView teacher_club;
    private TextView teacher_courseAttendance;
    private TextView teacher_check;
    private TextView teacher_workplan;
    private TextView teacher_meeting;
    private TextView teacher_achievement;
    private TextView teacher_Replace;
    private TextView teacher_setup;
    private TextView teacher_outdoors;
    private TextView teacher_spase;
    private TextView teacher_spase1;

    private OkHttpClient client;
    private String cardNo;
    private String phone;
    private String teacherId;
    private String teacherName;
    private String itemId;
    private final static String FILE_NAME = "xth.txt"; // 设置文件的名称
    private String spDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        setTitle("SCHOOLAPP（教师版）");
         //接收登录界面传过来的教师id 教师姓名 教师所教课程
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        teacherName = intent.getStringExtra("teacherName");
        itemId = intent.getStringExtra("itemId");
        phone = intent.getStringExtra("phone");
        cardNo = intent.getStringExtra("cardNo");
        boundControl();
        assignBtnAction();
        teacher_name.setText(teacherName);
     }
    @Override
    protected void onResume() {
        super.onResume();
        findNewMsg();
    }
    /**
     * 绑定控件
     * */
    private void boundControl()
    {
        teacher_name = (TextView) findViewById(R.id.teacher_name);
        teacher_club = (TextView) findViewById(R.id.teacher_club);
        teacher_courseAttendance = (TextView) findViewById(R.id.teacher_courseAttendance);
        teacher_check = (TextView) findViewById(R.id.teacher_check);
        teacher_workplan = (TextView) findViewById(R.id.teacher_workplan);
        teacher_meeting = (TextView) findViewById(R.id.teacher_meeting);
        teacher_achievement = (TextView) findViewById(R.id.teacher_achievement);
        teacher_Replace = (TextView) findViewById(R.id.teacher_Replace);
        teacher_setup = (TextView) findViewById(R.id.teacher_setup);
        teacher_outdoors = (TextView) findViewById(R.id.teacher_outdoors);
        teacher_spase = (TextView) findViewById(R.id.teacher_spase);
        teacher_spase1 = (TextView) findViewById(R.id.teacher_spase1);
    }
    /**
     * 点击事件方法 点击相应的功能进行相应的跳转并进行传值
     * */
    private void assignBtnAction()
    {
        //俱乐部
        teacher_club.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(TeacherActivity.this, TeacherClubActivity.class);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
            }
        });
        //课程签到
        teacher_courseAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherActivity.this, Class_ListActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("itemId",itemId);
                startActivity(intent);
            }
        });
        //考勤数据统计
        teacher_check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(TeacherActivity.this, AttendanceDataStatisticsActivity.class);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
            }
        });
        //工作计划
        teacher_workplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherActivity.this, TeacherWorkPlan.class);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
            }
        });
        //成绩查询
        teacher_achievement.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(TeacherActivity.this, ResultQueryActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("itemId",itemId);
                startActivity(intent);
            }
        });
        //会议及集体活动
        teacher_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherActivity.this, MeetingInfoActivity.class);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
            }
        });
        //账号设置
        teacher_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherActivity.this, AccountSettingsActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("cardNo",cardNo);
                //Log.d("cardNo = ", cardNo);
                intent.putExtra("phone",phone);
                startActivity(intent);
            }
        });
        //代课
        teacher_Replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherActivity.this, SubstituteActivity.class);
                intent.putExtra("teacherId",teacherId);
                Log.d("teacherId = ",teacherId+"***************");
                startActivity(intent);
            }
        });
        //体育达标测试
        teacher_outdoors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherActivity.this, SportsStandards.class);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
            }
        });
        //消息
        teacher_spase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences=getSharedPreferences("userinfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();

                editor.putString("msgDate", spDate);
                editor.commit();
                Intent intent = new Intent(TeacherActivity.this,StuNews.class);
                teacher_spase1.setVisibility(View.INVISIBLE);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化网络请求
     * */

    //查询是否是最新消息
    private void findNewMsg(){
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("status", 1 + "")
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.StuNews)
                .post(requestBodyPost)
                .build();
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                //signSleepTextView.setText("网络异常！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String retStr = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("网络请求返回值", retStr);

                        try {
                            //{"msgDate":"2017-01-18 15:27:40","msgContent":"张海里荣升一只鸡\n"}
                            JSONObject jsObj = new JSONObject(retStr);
                            String msgDate = jsObj.getString("msgDate");
                            spDate = msgDate;
                            SharedPreferences preferences=getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                            String saveDate=preferences.getString("msgDate", null);

                            Log.d(TAG, "run: msgDate"+msgDate);
                            Log.d(TAG, "run: savaDate"+saveDate);
                            if (msgDate.equals(saveDate))
                            {
                                Log.d(TAG, "run: 相同");
                                teacher_spase1.setVisibility(View.INVISIBLE);
                            }else
                            {
                                Log.d(TAG, "run: 不相同");

                                teacher_spase1.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //statuTV.setText("签到异常");
                        }
                    }
                });
            }
        });
    }


}
