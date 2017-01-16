package com.xinnuo.apple.nongda.studentActivity;
/**
 * 学生端主界面 点击相应的功能进行跳转
 * */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.studentActivity.AccountSettings.StuAccountSettingsActivity;
import com.xinnuo.apple.nongda.studentActivity.CampusDynamics.StuCampusDynamics;
import com.xinnuo.apple.nongda.studentActivity.ClassAttendance.StuClassAttendanceActivity;
import com.xinnuo.apple.nongda.studentActivity.OutDoorSports.OutDoorSportsActivity;
import com.xinnuo.apple.nongda.studentActivity.StuClub.StuClubActivity;
import com.xinnuo.apple.nongda.studentActivity.StudentResultQuery.StudentResultQueryActivity;
import com.xinnuo.apple.nongda.studentActivity.TeacherShow.TeacherShowActivity;

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

    private String studentNo;
    private String name;
    private String id;
    private String password;

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
    }
}
