package com.xinnuo.apple.nongda.studentActivity.StudentResultQuery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

public class SignInTimeActivity extends BaseActivity {
    private OkHttpClient client;
    private String studentId;
    private ListView listView;
    private JSONArray jsarr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_time);
        initOkHttp();
        listView = (ListView) findViewById(R.id.list_sign_in_time);
        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
    }

    protected void onStart()
    {
        super.onStart();
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
                .url(httpUrl.classListUrl)
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

        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length(); i++) {
            JSONObject jss = jsArr.getJSONObject(i);
            jsarr = jss.getJSONArray("testDate");
            for (int j = 0; j < jsarr.length(); j++)
            {
                JSONObject js = jsarr.getJSONObject(j);
                String testDate = js.optString("testDate");
                String overDate = js.optString("overDate");
                HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("id",id);
                map.put("testDate",testDate);
                map.put("overDate",overDate);
                listItem.add(map);
            }
        }
        if (jsarr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem,R.layout.item_sign_in_time,
                new  String[]{"testDate","overDate"},
                new int[]{R.id.class_attendance,R.id.class_attendance1});
        listView.setAdapter(qrAdapter);

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
