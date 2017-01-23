package com.xinnuo.apple.nongda.TeacherActivity.AccountSettings;
/**
 * 修改密码
 * */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyPasswordActivity extends BaseActivity implements View.OnClickListener{
    private OkHttpClient client;
    private EditText old_pswd;
    private EditText new_pswd;
    private EditText confirm_pswd;
    private Button button_pswd;
    private String cardNo;
    private String phone;
    private String teacherId;
    private String newPswd;
    private String oldPswd;
    private String pswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        setTitle("农大 => 修改密码");
        binding();
        initOkHttp();
        Intent intent = getIntent();
        cardNo = intent.getStringExtra("cardNo");
        Log.d("cardNo = ", cardNo);
        phone = intent.getStringExtra("phone");
        teacherId = intent.getStringExtra("teacherId");
        pswd = intent.getStringExtra("pswd");

    }
    //绑定控件
    private void binding()
    {
        old_pswd = (EditText) findViewById(R.id.old_pswd);
        new_pswd = (EditText) findViewById(R.id.new_pswd);
        confirm_pswd = (EditText) findViewById(R.id.confirm_pswd);
        button_pswd = (Button) findViewById(R.id.button_pswd);
        button_pswd.setOnClickListener(this);
    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(String cardNos, String phones ,String newPswds){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("cardNo",cardNo)
                .add("phone",phone)
                .add("password",newPswd)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherModifyPassword)
                .post(requestBodyPost)
                .build();
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            //接收方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //接收httpresponse返回的json数据
                final String retStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("网络请求返回值",retStr);
                        //菊花结束方法
                        mLoading.dismiss();
                        try {
                            //解析json数据
                            jsonParseWithJsonStr(retStr);
                        } catch (JSONException e) {
                            mLoading.dismiss();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 解析数据方法
     * */
    //json解析方法

    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {
        JSONObject js = new JSONObject(jsonStr);
        if (js.getString("melodyClass").equals("操作成功"))
        {
            AlertDialog.Builder builder  = new AlertDialog.Builder(ModifyPasswordActivity.this);
            builder.setTitle("提示" ) ;
            builder.setMessage("修改成功！" ) ;
            builder.setPositiveButton("确定" , new DialogInterface.OnClickListener() {
                // 单击事件
                public void onClick(DialogInterface dialog, int which)
                {
                    // 设置TextView文本
                    //进行跳转
                    Intent intent = new Intent(ModifyPasswordActivity.this,AccountSettingsActivity.class);
                    intent.putExtra("cardNo",cardNo);
                    intent.putExtra("phone",phone);
                    intent.putExtra("teacherId",teacherId);
                    startActivity(intent);
                    finish();
                }
            }) ;
            builder.show();
        }else
        {
            AlertDialog.Builder builder  = new AlertDialog.Builder(ModifyPasswordActivity.this);
            builder.setTitle("提示" ) ;
            builder.setMessage("修改失败，请重新提交！" ) ;
            builder.setPositiveButton("确定" ,  null );
            builder.show();
        }

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

    @Override
    public void onClick(View view) {
        oldPswd = old_pswd.getText().toString();
        newPswd = new_pswd.getText().toString();
        if (oldPswd.equals(pswd)){
        if (newPswd.length() > 6) {
            String confirmPswd = confirm_pswd.getText().toString();
            if (newPswd.equals(confirmPswd)) {
                // 设置显示信息
                final AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPasswordActivity.this);

                builder.setMessage("确定要提交？").
                        // 设置确定按钮
                                setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        requestWithUserId(cardNo, phone, newPswd);
                                    }
                                }).
                        // 设置取消按钮
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


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPasswordActivity.this);
                builder.setTitle("提示");
                builder.setMessage("两次输入的密码不一致，请重新输入！");
                builder.setPositiveButton("确定", null);
                builder.show();
            }
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPasswordActivity.this);
            builder.setTitle("提示");
            builder.setMessage("密码长度必须大于6位，请重新输入！");
            builder.setPositiveButton("确定", null);
            builder.show();
        }
    }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPasswordActivity.this);
            builder.setTitle("提示");
            builder.setMessage("旧密码输入不正确，请重新输入！");
            builder.setPositiveButton("确定", null);
            builder.show();
        }
    }
}
