package com.xinnuo.apple.nongda.studentActivity.StudentResultQuery;
/**
 * 学生成绩查询
 * */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.xinnuo.apple.nongda.R;

public class StudentResultQueryActivity extends AppCompatActivity {
    private TextView student_query1;
    private TextView student_query2;
    private TextView student_query3;
    private TextView student_query4;
    private String id;          //学生Id
    private String studentNo;   //学号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_result_query);
        binding();
        clickJump();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        studentNo = intent.getStringExtra("studentNo");
    }
    /**
    * 绑定控件
    * */
    protected void binding()
    {
        student_query1 = (TextView) findViewById(R.id.student_query1);
        student_query2 = (TextView) findViewById(R.id.student_query2);
        student_query3 = (TextView) findViewById(R.id.student_query3);
        student_query4 = (TextView) findViewById(R.id.student_query4);
    }
    /**
     * 点击进行相应的跳转
     * */
    protected void clickJump()
    {
        //签到记录查询
        student_query1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentResultQueryActivity.this,StuAttendanceRecordActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        //体质成绩查询
        student_query2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentResultQueryActivity.this,PhysicalPerformanceActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        //在线考试成绩查询
        student_query3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //体育成绩查询
        student_query4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentResultQueryActivity.this,SportsPerformanceQueryActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }
}
