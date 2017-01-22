package com.xinnuo.apple.nongda.studentActivity.TeacherEvaluation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherScoreActivity extends BaseActivity {
    private TextView core1;
    private TextView core2;
    private TextView core3;
    private TextView core4;
    private TextView core5;
    private TextView core6,core7;
    private Button coreButton;
    private String core;
    private String teacherId;
    private String id;
    private OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_score);
        initOkHttp();
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        id = intent.getStringExtra("id");
        core1 = (TextView) findViewById(R.id.core1);
        core2 = (TextView) findViewById(R.id.core2);
        core3 = (TextView) findViewById(R.id.core3);
        core4 = (TextView) findViewById(R.id.core4);
        core5 = (TextView) findViewById(R.id.core5);
        core6 = (TextView) findViewById(R.id.core6);
        core7 = (TextView) findViewById(R.id.core7);
        coreButton = (Button) findViewById(R.id.core_button);
        core2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                core = "0分";
                core1.setText(core);
            }
        });
        core3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                core = "2分";
                core1.setText(core);
            }
        });
        core4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                core = "2分";
                core1.setText(core);
            }
        });
        core5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                core = "3分";
                core1.setText(core);
            }
        });
        core6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                core = "4分";
                core1.setText(core);
            }
        });
        core7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                core = "5分";
                core1.setText(core);
            }
        });
        coreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置显示信息
                final AlertDialog.Builder builder = new AlertDialog.Builder(TeacherScoreActivity.this);

                builder.setMessage("确定要提交？").
                        // 设置确定按钮
                                setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 设置TextView文本
                                      //点击是的时候去进行提交
                                       requestWithUserId();
                                    }
                                }).
                        // 设置取消按钮
                                setNegativeButton("否",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {

                                    }
                                });
                // 创建对话框
                AlertDialog ad = builder.create();
                // 显示对话框
                ad.show();

            }
        });
    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(){
        //菊花的开始方法
        mLoading.show();

        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherId",teacherId)
                .add("score",core)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.AddTeacherEvaluation)
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
    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {

        JSONArray jsArr = new JSONArray(jsonStr);
        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length(); i++)
        {
            JSONObject js = jsArr.getJSONObject(i);
            String relTime = js.optString("relTime");
            String meetType;
            if (js.getString("melodyClass").equals("成功"))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherScoreActivity.this);
                builder.setTitle("提示");
                builder.setMessage("评价上传成功！");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            // 单击事件
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // 设置TextView文本
                                //点击是的时候去进行提交
                                if (core.length() == 0){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TeacherScoreActivity.this);
                                    builder.setTitle("提示");
                                    builder.setMessage("请选择分数！");
                                    builder.setPositiveButton("确定", null);
                                    builder.show();
                                }else {
                                    Intent intent = new Intent(TeacherScoreActivity.this,TeacherEvaluationActivity.class);
                                    intent.putExtra("id",id);
                                    startActivity(intent);
                                }

                            }
                        });
                builder.show();
            }else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherScoreActivity.this);
                builder.setTitle("提示");
                builder.setMessage("评价上传失败！");
                builder.setPositiveButton("确定", null);
                builder.show();
            }

            teacherId = js.getString("id");

        }


        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
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
}
