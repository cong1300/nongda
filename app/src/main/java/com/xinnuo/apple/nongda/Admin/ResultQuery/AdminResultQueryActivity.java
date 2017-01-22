package com.xinnuo.apple.nongda.Admin.ResultQuery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.TeacherActivity.Resultquery.TeacherClassQueryActivity;

public class AdminResultQueryActivity extends AppCompatActivity {
    private TextView attendanceRecord;
    private TextView constitutionQuery;
    private TextView onlineAnswer;
    private TextView sportsPerformance;
    private String teacherId;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_result_query);
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("id");
        itemId = intent.getStringExtra("itemId");
        BoundControl();
        PageJump();
    }
    /**
     *绑定控件方法
     * */
    private void BoundControl()
    {
        attendanceRecord = (TextView) findViewById(R.id.admin_query1);
        constitutionQuery = (TextView) findViewById(R.id.admin_query2);
        onlineAnswer = (TextView) findViewById(R.id.admin_query3);
        sportsPerformance = (TextView) findViewById(R.id.admin_query4);
    }
    /**
     * 点击事件
     * 点击相应的TextView进行页面跳转
     * */
    private void PageJump()
    {   //签到记录查询
        attendanceRecord.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String state = "1";
                Intent intent = new Intent(AdminResultQueryActivity.this,TeacherClassQueryActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("itemId",itemId);
                intent.putExtra("state",state);
                startActivity(intent);
            }
        });
        //体质测试查询
        constitutionQuery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String state = "2";
                Intent intent = new Intent(AdminResultQueryActivity.this,TeacherClassQueryActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("itemId",itemId);
                intent.putExtra("state",state);
                startActivity(intent);
            }
        });
        //在线答题成绩查询
        onlineAnswer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String state = "3";
                Intent intent = new Intent(AdminResultQueryActivity.this,TeacherClassQueryActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("itemId",itemId);
                intent.putExtra("state",state);
                startActivity(intent);
            }
        });
        //体育成绩查询
        sportsPerformance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String state = "4";
                Intent intent = new Intent(AdminResultQueryActivity.this,TeacherClassQueryActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("itemId",itemId);
                intent.putExtra("state",state);
                startActivity(intent);
            }
        });
    }
}
