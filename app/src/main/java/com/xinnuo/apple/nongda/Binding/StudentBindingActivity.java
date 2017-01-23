package com.xinnuo.apple.nongda.Binding;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xinnuo.apple.nongda.MainActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.studentActivity.AccountSettings.StuAccountSettingsActivity;

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

public class StudentBindingActivity extends AppCompatActivity implements View.OnClickListener {

    //定义控件
    private EditText account;
    private TextView tvshow;
    private EditText pwd;
    private Button login;

    private static OkHttpClient client;
    private static String melodyClass;
    private static String userNo;
    private String id;
    private String studentNo;
    private String password;
    private String new_paswd;
    private String state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_binding);
        initOkHttp();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        studentNo = intent.getStringExtra("studentNo");
        Log.d("stuNo = ",studentNo);
        password = intent.getStringExtra("password");
        if (intent.getStringExtra("state") != null){
            state = intent.getStringExtra("state");
        }

        initOkHttp();

        //绑定控件
        this.account = (EditText) findViewById(R.id.nameEditTV);
        this.pwd = (EditText) findViewById(R.id.passwordEditTV);
        this.login = (Button) findViewById(R.id.loginBtn);
        this.tvshow = (TextView) findViewById(R.id.tv_show);
        login.setOnClickListener(this);
    }

    // 实例化AlertDailog.Builder对象
    @Override
    public void onClick(View v) {
        //接收页面传过来来的手机号和身份证号
        final String userIdStr = account.getText().toString().trim();
        final String passwordStr = pwd.getText().toString().trim();

        Log.d("userIdStr",userIdStr);
        Log.d("passwordStr",passwordStr);

        // 设置显示信息
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("确定要绑定？").
                // 设置确定按钮
                        setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            // 单击事件
                            public void onClick(DialogInterface dialog, int which) {
                                // 设置TextView文本
                                //点击是的时候去进行提交
                                if (userIdStr.length() != 18 || passwordStr.length() != 11){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentBindingActivity.this);
                                    builder.setTitle("提示");
                                    builder.setMessage("手机号是11位，身份证号是18位！");
                                    builder.setPositiveButton("确定", null);
                                    builder.show();
                                } else {
                                    requestWithUserId(userIdStr,passwordStr,studentNo);
                                }

                            }
                        }).
                // 设置取消按钮
                        setNegativeButton("否",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                tvshow.setText("取消绑定！");
                            }
                        });
        // 创建对话框
        AlertDialog ad = builder.create();
        // 显示对话框
        ad.show();
    }
    private void initOkHttp()
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

    }
    /**
     * 请求方法 参数（身份证号、电话号、学生编号）
     * */
    private  void requestWithUserId(String IdNumber , String Phone , String StudentNo){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("IdNumber",IdNumber)
                .add("Phone",Phone)
                .add("stuNo",StudentNo)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.studentBindingUrl)
                .post(requestBodyPost)
                .build();
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                tvshow.setText("网络异常！");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //接收httpresponse请求的返回值
                final String retStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("网络请求返回值",retStr);
                        try {
                            //调用解析方法 解析数据
                            jsonParseWithJsonStr(retStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    //json数据解析
    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {
        JSONObject js = new JSONObject(jsonStr);
        melodyClass = js.getString("melodyClass");
        Log.d("*******************",melodyClass);
        //判断返回值如果是yes就跳转。如果是no就给提示信息
        if (melodyClass.equals("yes")){
            tvshow.setText("绑定成功！");
            //绑定成功后跳转登录界面
            if (state.equals("1")){
                Intent intent = new Intent(StudentBindingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(StudentBindingActivity.this,StuAccountSettingsActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("studentNo",studentNo);
                intent.putExtra("password",password);
                startActivity(intent);
                //清理缓存
                finish();
            }

        }else{
            //绑定失败留在本界面
            tvshow.setText("绑定失败");
        }



    }
}
