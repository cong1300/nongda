package com.xinnuo.apple.nongda.studentActivity.AccountSettings;
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

public class StuModifyPasswordActivity extends BaseActivity implements View.OnClickListener{
    private OkHttpClient client;
    private EditText stu_old_pswd;
    private EditText stu_new_pswd;
    private EditText stu_confirm_pswd;
    private Button stu_button_pswd;
    private String id;
    private String studentNo;
    private String password;
    private String new_paswd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_modify_password);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        studentNo = intent.getStringExtra("studentNo");
        Log.d("stuNo = ",studentNo);
        password = intent.getStringExtra("password");
        initOkHttp();
        binding();
    }
    //绑定控件
    private void binding()
    {
        stu_old_pswd = (EditText) findViewById(R.id.stu_old_pswd);
        stu_new_pswd = (EditText) findViewById(R.id.stu_new_pswd);
        stu_confirm_pswd = (EditText) findViewById(R.id.stu_confirm_pswd);
        stu_button_pswd = (Button) findViewById(R.id.stu_button_pswd);
        stu_button_pswd.setOnClickListener(this);
    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(String id, String studentNos ,String oldpswd,String newPswds){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",id)
                .add("studentNo",studentNo)
                .add("topas",oldpswd)
                .add("pwdByStu",newPswds)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.StudentModifyPassword)
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
        if (js.getString("melodyClass").equals("yes"))
        {
            AlertDialog.Builder builder  = new AlertDialog.Builder(StuModifyPasswordActivity.this);
            builder.setTitle("提示" ) ;
            builder.setMessage("修改成功！" ) ;
            builder.setPositiveButton("确定" , new DialogInterface.OnClickListener() {
                // 单击事件
                public void onClick(DialogInterface dialog, int which)
                {
                    // 设置TextView文本
                    //进行跳转
                    Intent intent = new Intent(StuModifyPasswordActivity.this,StuAccountSettingsActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("studentNo",studentNo);
                    intent.putExtra("password",password);
                    startActivity(intent);
                }
            }) ;
            builder.show();
        }else
        {
            AlertDialog.Builder builder  = new AlertDialog.Builder(StuModifyPasswordActivity.this);
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
        new_paswd = stu_new_pswd.getText().toString();
        if (new_paswd.length() >= 6) {
            String confirmPswd = stu_confirm_pswd.getText().toString();
            if (new_paswd.equals(confirmPswd)) {
                // 设置显示信息
                final AlertDialog.Builder builder = new AlertDialog.Builder(StuModifyPasswordActivity.this);

                builder.setMessage("确定要提交？").
                        // 设置确定按钮
                                setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        requestWithUserId(id, studentNo, password,new_paswd);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(StuModifyPasswordActivity.this);
                builder.setTitle("提示");
                builder.setMessage("两次输入的密码不一致，请重新输入！");
                builder.setPositiveButton("确定", null);
                builder.show();
            }
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(StuModifyPasswordActivity.this);
            builder.setTitle("提示");
            builder.setMessage("密码长度必须大于6位，请重新输入！");
            builder.setPositiveButton("确定", null);
            builder.show();
        }
    }
}
