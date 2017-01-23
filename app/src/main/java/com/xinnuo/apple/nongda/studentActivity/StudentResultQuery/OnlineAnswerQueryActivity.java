package com.xinnuo.apple.nongda.studentActivity.StudentResultQuery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class OnlineAnswerQueryActivity extends BaseActivity {
    private TextView answer_score;
    private TextView answer_numberi;
    private OkHttpClient client;
    private String studentId;
    private String studentNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_answer_query);
        answer_score = (TextView) findViewById(R.id.answer_score);
        answer_numberi = (TextView) findViewById(R.id.answer_number);
        initOkHttp();
        Intent intent = getIntent();
        studentId = intent.getStringExtra("id");
        studentNo = intent.getStringExtra("studentNo");
        requestWithUserId();

    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("studentId",studentId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.StuSportsClassIdQueryStudentsOnline)
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

        JSONObject jsArr = new JSONObject(jsonStr);

        answer_numberi.setText(studentNo);

       answer_score.setText(jsArr.getString("stuScore"));
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
