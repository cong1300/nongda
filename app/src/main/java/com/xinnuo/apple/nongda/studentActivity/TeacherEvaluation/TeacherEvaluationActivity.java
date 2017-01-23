package com.xinnuo.apple.nongda.studentActivity.TeacherEvaluation;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

public class TeacherEvaluationActivity extends BaseActivity {
    private String id;
    private TextView teacherName;
    private TextView teacherState;
    private LinearLayout teacherLayout;
    private OkHttpClient client;
    private String type = null,teacherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_evaluation);
        initOkHttp();
        final Intent intent = getIntent();
        id = intent.getStringExtra("id");
        teacherName = (TextView) findViewById(R.id.teacher_name);
        teacherState = (TextView) findViewById(R.id.teacher_state);
        teacherLayout = (LinearLayout) findViewById(R.id.teacher_Layout);
        requestWithUserId();


        teacherLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (type.equals("未评价")){
                     Intent intent1 = new Intent(TeacherEvaluationActivity.this,TeacherScoreActivity.class);
                     intent1.putExtra("teacherId",teacherId);
                     intent1.putExtra("id",id);
                     startActivity(intent1);
                 }
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
                .add("id",id)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherEvaluation)
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
                        } catch (Exception e) {
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
    private  void jsonParseWithJsonStr (String jsonStr){

        JSONArray jsArr = null;
        try {
            jsArr = new JSONArray(jsonStr);
            List<String> data = new ArrayList<String>();
            ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
            for (int i = 0; i < jsArr.length(); i++)
            {
                JSONObject js = jsArr.getJSONObject(i);
                String meetType;
                if (js.getString("type").equals("nul"))
                {
                    type = "未评价";
                    teacherState.setText("未评价");
                }else
                {
                    type = "已评价";
                    teacherState.setText("已评价");
                }
                teacherName.setText(js.getString("name"));
                teacherId = js.getString("id");
            }


            if (jsArr.length() == 0)
            {
                midToast("无数据",3);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherEvaluationActivity.this);
            builder.setTitle("提示");
            builder.setMessage("请先选择课程！");
            builder.setPositiveButton("确定", null);
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
}
