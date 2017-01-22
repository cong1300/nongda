package com.xinnuo.apple.nongda.studentActivity;
/**
 * 学生端主界面 点击相应的功能进行跳转
 * */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.studentActivity.AccountSettings.StuAccountSettingsActivity;
import com.xinnuo.apple.nongda.studentActivity.CampusDynamics.StuCampusDynamics;
import com.xinnuo.apple.nongda.studentActivity.ClassAttendance.StuClassAttendanceActivity;
import com.xinnuo.apple.nongda.studentActivity.News.StuNews;
import com.xinnuo.apple.nongda.studentActivity.OutDoorSports.OutDoorSportsActivity;
import com.xinnuo.apple.nongda.studentActivity.StuClub.StuClubActivity;
import com.xinnuo.apple.nongda.studentActivity.StudentResultQuery.StudentResultQueryActivity;
import com.xinnuo.apple.nongda.studentActivity.TeacherEvaluation.TeacherEvaluationActivity;
import com.xinnuo.apple.nongda.studentActivity.TeacherShow.TeacherShowActivity;

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

public class StudentActivity extends AppCompatActivity {
    //定义控件
    private TextView student_name;
    private TextView account_number;
    private TextView student_club;
    private TextView student_CampusDynamics;
    private TextView student_teachers;
    private TextView student_OnlineTest;
    private TextView student_ClassAttendance;
    private TextView student_TeacherEvaluation;
    private TextView student_ResultQuery;
    private TextView student_setup;
    private TextView student_outdoors;
    private TextView stu_spase;
    private TextView stu_spase1;

    private String studentNo;
    private String name;
    private String id;
    private String password;
    private OkHttpClient client;
    private String spDate;
    private final static String FILE_NAME = "xth.txt"; // 设置文件的名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        binding();
        Intent intent = getIntent();
        name = intent.getStringExtra("studentName");
        studentNo = intent.getStringExtra("studentNo");
        id = intent.getStringExtra("studentId");
        password = intent.getStringExtra("password");
        student_name.setText(name);
        account_number.setText(studentNo);
        clickJump();


    }

    @Override
    protected void onResume() {
        super.onResume();
        findNewMsg();
    }

    /**
     * 绑定控件
     * */
    private void binding()
    {
        student_name = (TextView) findViewById(R.id.student_name);
        account_number = (TextView) findViewById(R.id.account_number);
        student_club = (TextView) findViewById(R.id.student_club);
        student_CampusDynamics = (TextView) findViewById(R.id.student_CampusDynamics);
        student_teachers = (TextView) findViewById(R.id.student_teachers);
        student_OnlineTest = (TextView) findViewById(R.id.student_OnlineTest);
        student_ClassAttendance = (TextView) findViewById(R.id.student_ClassAttendance);
        student_TeacherEvaluation = (TextView) findViewById(R.id.student_TeacherEvaluation);
        student_ResultQuery = (TextView) findViewById(R.id.student_ResultQuery);
        student_setup = (TextView) findViewById(R.id.student_setup);
        student_outdoors = (TextView) findViewById(R.id.student_outdoors);
        stu_spase = (TextView) findViewById(R.id.stu_spase);
        stu_spase1 = (TextView) findViewById(R.id.stu_spase1);
    }
    /**
     * 点击事件进行相应的跳转
     * */
    private void clickJump()
    {
        //俱乐部
        student_club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this,StuClubActivity.class);
                intent.putExtra("studentId",id);
                intent.putExtra("studentNo",studentNo);
                intent.putExtra("pswd",password);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
        //校园动态
        student_CampusDynamics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this,StuCampusDynamics.class);
                startActivity(intent);
            }
        });
        //教师风采
        student_teachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this,TeacherShowActivity.class);
                startActivity(intent);
            }
        });
        //在线考试
        student_OnlineTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //上课签到
        student_ClassAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this,StuClassAttendanceActivity.class);
                intent.putExtra("studentId",id);
                intent.putExtra("studentNo",studentNo);
                startActivity(intent);
            }
        });
        //教师评价
        student_TeacherEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this,TeacherEvaluationActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        //成绩查询
        student_ResultQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this,StudentResultQueryActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("studentNo",studentNo);
                startActivity(intent);
            }
        });
        //账号设置
        student_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this,StuAccountSettingsActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("studentNo",studentNo);
                intent.putExtra("password",password);
                startActivity(intent);
            }
        });
        //户外考勤
        student_outdoors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this,OutDoorSportsActivity.class);
                intent.putExtra("studentId",id);
                startActivity(intent);

            }
        });
        //消息
        stu_spase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences preferences=getSharedPreferences("userinfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();

                editor.putString("msgDate", spDate);
                editor.commit();

                Intent intent = new Intent(StudentActivity.this,StuNews.class);
                 startActivity(intent);
            }
        });
    }
    //查询是否是最新消息
    private void findNewMsg(){
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("status",1+"")
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
                    public static final String TAG = "消息 = ";

                    @Override
                    public void run() {

                        Log.d("网络请求返回值",retStr);

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
                                stu_spase1.setVisibility(View.INVISIBLE);
                            }else
                            {
                                Log.d(TAG, "run: 不相同");

                                stu_spase1.setVisibility(View.VISIBLE);
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
