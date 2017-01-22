package com.xinnuo.apple.nongda.studentActivity.StudentResultQuery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class SportsPerformanceQueryActivity extends BaseActivity {
    private TextView stu_number1;
    private TextView stu_number2;
    private TextView stu_number3;
    private TextView stu_number4;
    private TextView stu_number5;
    private TextView stu_number6;
    private TextView stu_number7;
    private OkHttpClient client;
    private String studentNo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_performance_query);
        initOkHttp();
        Intent intent = getIntent();
        studentNo = intent.getStringExtra("studentNo");
        Binding();
        requestWithUserId();
    }
    private void Binding(){
        stu_number1 = (TextView) findViewById(R.id.stu_number1);
        stu_number2 = (TextView) findViewById(R.id.stu_number2);
        stu_number3 = (TextView) findViewById(R.id.stu_number3);
        stu_number4 = (TextView) findViewById(R.id.stu_number4);
        stu_number5 = (TextView) findViewById(R.id.stu_number5);
        stu_number6 = (TextView) findViewById(R.id.stu_number6);
        stu_number7 = (TextView) findViewById(R.id.stu_number7);
    }

    /**
     * 请求方法
     * */
    private  void requestWithUserId(){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("studentNo",studentNo)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.SportsPerformanceQuery)
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

        JSONArray jsArr = new JSONArray(jsonStr);

        /**
         *
         * "studentNo": "11042121",
         "name": "王冰雪",
         "moKao": 0,
         "zhangKao": 0,
         "jiShu": 0,
         "": 18.090000000000003,
         "pingShi": 60.300000000000004*/
        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length(); i++) {
            JSONObject jss = jsArr.getJSONObject(i);
            String studentNo = jss.optString("studentNo");
            stu_number1.setText(studentNo);
            stu_number2.setText(jss.optString("name"));
            stu_number3.setText(jss.optString("pingShi"));
            stu_number4.setText(jss.optString("zhangKao"));
            stu_number5.setText(jss.optString("moKao"));
            stu_number6.setText(jss.optString("jiShu"));
            stu_number7.setText(jss.optString("zongFen"));

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
