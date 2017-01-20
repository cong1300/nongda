package com.xinnuo.apple.nongda.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xinnuo.apple.nongda.Admin.Club.AdminClub;
import com.xinnuo.apple.nongda.Admin.Meeting.AdminMeetingInfoActivity;
import com.xinnuo.apple.nongda.Admin.News.AdminNewsActivity;
import com.xinnuo.apple.nongda.Admin.Substitute.AdminSubstituteActivity;
import com.xinnuo.apple.nongda.MainActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

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

public class AdminActivity extends AppCompatActivity {
    //定义控件
    private TextView admin_meeting;
    private TextView admin_spase;
    private TextView admin_achievement;
    private TextView admin_setup;
    private TextView admin_Replace;
    private TextView admin_check;
    private TextView admin_club;
    private TextView admin_name;
    private OkHttpClient client;
    private String adminId;
    private final static String FILE_NAME = "xth.txt"; // 设置文件的名称

    private String adminName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initOkHttp();
        boundControl();
        Intent intent = getIntent();
        adminName = intent.getStringExtra("adminName");
        adminId = intent.getStringExtra("adminId");
        admin_name.setText(adminName);
        assignBtnAction();
    }

    /**
     * 绑定控件
     * */
    private void boundControl()
    {
        admin_name = (TextView) findViewById(R.id.admin_name);
        admin_club = (TextView) findViewById(R.id.admin_club);
        admin_check = (TextView) findViewById(R.id.admin_check);
        admin_achievement = (TextView) findViewById(R.id.admin_achievement);
        admin_Replace = (TextView) findViewById(R.id.admin_Replace);
        admin_setup = (TextView) findViewById(R.id.admin_setup);
        admin_meeting = (TextView) findViewById(R.id.admin_meeting);
        admin_spase = (TextView) findViewById(R.id.admin_spase);
    }

    /**
     * 点击事件方法 点击相应的功能进行相应的跳转并进行传值
     * */
    private void assignBtnAction()
    {
        //俱乐部
        admin_club.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminActivity.this, AdminClub.class);
                String statu = "0";
                intent.putExtra("statu",statu);
                startActivity(intent);
            }
        });
        //考勤数据统计
        admin_check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminActivity.this, AdminClub.class);
                String statu = "1";
                intent.putExtra("statu",statu);
                startActivity(intent);
            }
        });
        //成绩查询
        admin_achievement.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminActivity.this, AdminClub.class);
                String statu = "2";
                intent.putExtra("statu",statu);
                startActivity(intent);
            }
        });
        //会议及集体活动
        admin_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminMeetingInfoActivity.class);
                intent.putExtra("teacherId",adminId);
                startActivity(intent);
            }
        });
        //账号设置
        admin_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginOut();
            }
        });
        //代课
        admin_Replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminSubstituteActivity.class);
                intent.putExtra("teacherId",adminId);
                startActivity(intent);
            }
        });
        //消息
        admin_spase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this,AdminNewsActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * 初始化网络请求
     * */
    public void initOkHttp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    //查询是否是最新消息
    private void scanCodeSign(){
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
                    @Override
                    public void run() {

                        Log.d("网络请求返回值",retStr);

                        try {
                            JSONObject jsObj = new JSONObject(retStr);
                            String retStr = jsObj.getString("msgDate");
                            StringBuffer sb = read();
                            String date = sb.toString();
                            if (date.equals("null") || date == "")
                            {
                                SimpleDateFormat sDateFormat = new    SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                date  = sDateFormat.format(new java.util.Date());
                            }


                            if (retStr.equals(date))
                            {
                                admin_spase.setVisibility(View.INVISIBLE);
                            }else
                            {
                                admin_spase.setVisibility(View.VISIBLE);
                                save(retStr);
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
    private StringBuffer read() {
        FileInputStream in = null;
        Scanner s = null;
        StringBuffer sb = new StringBuffer();
        try {
            in = super.openFileInput(FILE_NAME);
            s = new Scanner(in);
            while (s.hasNext()) {
                sb.append(s.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    private void save(String data) {
        FileOutputStream out = null;
        PrintStream ps = null;
        try {
            out = super.openFileOutput(FILE_NAME, Activity.MODE_APPEND);
            ps = new PrintStream(out);
            ps.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    ps.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //退出方法 点击注销时给出提示信息
    private void loginOut(){
        // 设置显示信息
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("确定要退出？").
                // 设置确定按钮
                        setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            // 单击事件
                            public void onClick(DialogInterface dialog, int which) {
                                // 设置TextView文本
                                //点击是的时候去进行提交
                                SharedPreferences sp = getSharedPreferences("userinfo",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();

                                editor.putBoolean("pswd_checkBox", false);
                                editor.commit();

                                //进行页面跳转，跳回登录界面
                                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).
                // 取消跳转
                        setNegativeButton("否",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        });
        // 创建对话框
        AlertDialog ad = builder.create();
        // 显示对话框
        ad.show();
    }

}
