package com.xinnuo.apple.nongda.TeacherActivity.SportsStandards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

import org.json.JSONArray;
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

public class SportsScore extends BaseActivity {
    private OkHttpClient client;
    private String id;
    private String sex;
    private String studentId;
    private String sportkindName;
    private String name;
    private String studentNo;
    private String sportkindSex;
    private String status;
    private ListView listView;
    private JSONArray dataArr;
    private String score;
    private TextView sport_event;
    private TextView sport_stu_name;
    private TextView sport_stu_number;
    private EditText sport_stu_score;
    private Button sport_submission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_score);
        initOkHttp();
        sport_event = (TextView) findViewById(R.id.sport_event);
        sport_stu_name = (TextView) findViewById(R.id.sport_stu_name);
        sport_stu_number = (TextView) findViewById(R.id.sport_stu_number);
        sport_stu_score = (EditText) findViewById(R.id.sport_stu_score);
        sport_submission = (Button) findViewById(R.id.sport_submission);
        sport_submission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String scores = sport_stu_score.getText().toString();
                // 设置显示信息
                final AlertDialog.Builder builder = new AlertDialog.Builder(SportsScore.this);
                builder.setMessage("是否提交").
                        // 设置确定按钮
                                setPositiveButton("提交",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        // 单击事件
                                        requestWithUserId(scores);


                                    }
                                }).
                        // 设置取消按钮
                                setNegativeButton("取消",
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


                builder.show();

            }
        });
    }
    protected void onStart()
    {
        super.onStart();
        //取出上个界面传过来的id
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        sportkindName = intent.getStringExtra("sportkindName");
        studentId = intent.getStringExtra("studentId");
        studentNo = intent.getStringExtra("studentNo");
        sportkindSex = intent.getStringExtra("sportkindSex");
        name = intent.getStringExtra("name");
        status = intent.getStringExtra("status");
        sport_event.setText(sportkindName);
        sport_stu_name.setText(name);
        sport_stu_number.setText(studentNo);
    }
    //请求方法
    private  void requestWithUserId(String score){
        mLoading.show();
        Log.d("StudentList ==== ", id);
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("gradeId",id)   //id
                .add("studentId",studentId)
                .add("status",status)
                .add("score",score)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.SportsScore)    //请求地址
                .post(requestBodyPost)
                .build();
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            //接收后台返回值方法
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
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }

    //json解析

    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {

        JSONObject jsArr = new JSONObject(jsonStr);
        if (jsArr.getString("melodyClass").equals("yes"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(SportsScore.this);
            builder.setTitle("提示");
            builder.setMessage("提交成功！");
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        // 单击事件
                        public void onClick(DialogInterface dialog, int which) {
                            // 设置TextView文本
                            //点击是的时候去进行提交
                            Intent intent = new Intent(SportsScore.this,Sport_Class.class);
                            intent.putExtra("id",id);
                            intent.putExtra("sportkindSex",sportkindSex);
                            intent.putExtra("sportkindName",sportkindName);
                            intent.putExtra("status",status);
                            startActivity(intent);
                            finish();
                        }
                    });
            builder.show();
        }else if (jsArr.getString("melodyClass").equals("Error")){
            AlertDialog.Builder builder = new AlertDialog.Builder(SportsScore.this);
            builder.setTitle("提示");
            builder.setMessage("提交失败！");
            builder.setPositiveButton("确定", null);
            builder.show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SportsScore.this);
            builder.setTitle("提示");
            builder.setMessage("提交异常！");
            builder.setPositiveButton("确定", null);
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
