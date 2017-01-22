package com.xinnuo.apple.nongda.studentActivity.AccountSettings;
/**
 * 账号设置界面 里面包含5个功能
 * */
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

import com.xinnuo.apple.nongda.MainActivity;
import com.xinnuo.apple.nongda.R;

public class StuAccountSettingsActivity extends AppCompatActivity  {
    //定义控件
    private TextView stu_account_setting1;  //绑定手机号
    private TextView stu_account_setting2;  //修改密码
    private TextView stu_account_setting3;  //意见反馈
    private TextView stu_account_setting4;  //关于我们
    private TextView stu_sign_out;           //退出登录

    private String studentNo;
    private String id;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_account_settings);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        studentNo = intent.getStringExtra("studentNo");
        Log.d("stuNo =",studentNo);
        password = intent.getStringExtra("password");
        binding();
        clickJump();
    }

    //绑定控件
    private void binding()
    {
       stu_account_setting1 = (TextView) findViewById(R.id.stu_account_setting1);
       stu_account_setting2 = (TextView) findViewById(R.id.stu_account_setting2);
       stu_account_setting3 = (TextView) findViewById(R.id.stu_account_setting3);
       stu_account_setting4 = (TextView) findViewById(R.id.stu_account_setting4);
       stu_sign_out = (TextView) findViewById(R.id.sign_out);
    }

    //点击相应控件进行相应的跳转
    private void clickJump()
    {
        //绑定手机号
        stu_account_setting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //修改密码
        stu_account_setting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StuAccountSettingsActivity.this,StuModifyPasswordActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("studentNo",studentNo);
                intent.putExtra("password",password);
                startActivity(intent);
            }
        });
        //意见反馈
        stu_account_setting3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //关于我们
        stu_account_setting4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //退出登录
        stu_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginOut();
            }
        });
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
                                editor.putString("msgDate", null);

                                editor.commit();

                                //进行页面跳转，跳回登录界面
                                Intent intent = new Intent(StuAccountSettingsActivity.this, MainActivity.class);
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
