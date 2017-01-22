package com.xinnuo.apple.nongda.TeacherActivity.AccountSettings;
/**
 * 账号设置主界面
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

import com.xinnuo.apple.nongda.Binding.TeacherBindingActivity;
import com.xinnuo.apple.nongda.MainActivity;
import com.xinnuo.apple.nongda.R;

public class AccountSettingsActivity extends AppCompatActivity {
    private TextView account_setting1;
    private TextView account_setting2;
    private TextView account_setting3;
    private TextView sign_out;
    private String cardNo;
    private String teacherId;
    private String phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Intent intent = getIntent();
        cardNo = intent.getStringExtra("cardNo");
        Log.d("cardNo = ", cardNo);
        teacherId = intent.getStringExtra("teacherId");
        phone = intent.getStringExtra("phone");
        binding();
        jump();
    }

    //绑定控件
    private void binding()
    {
        account_setting1 = (TextView) findViewById(R.id.account_setting1);
        account_setting2 = (TextView) findViewById(R.id.account_setting2);
        account_setting3 = (TextView) findViewById(R.id.account_setting3);
        sign_out = (TextView) findViewById(R.id.sign_out);
    }

    //点击事件跳转方法
    private void jump()
    {
        //绑定手机号
        account_setting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountSettingsActivity.this, TeacherBindingActivity.class);
                intent.putExtra("phone",phone);
                intent.putExtra("cardNo",cardNo);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
            }
        });
        //修改密码
        account_setting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountSettingsActivity.this, ModifyPasswordActivity.class);
                intent.putExtra("phone",phone);
                intent.putExtra("cardNo",cardNo);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
            }
        });
        //关于我们
        account_setting3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //退出登录
        sign_out.setOnClickListener(new View.OnClickListener() {
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
                                editor.commit();

                                //进行页面跳转，跳回登录界面
                                Intent intent = new Intent(AccountSettingsActivity.this, MainActivity.class);
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
