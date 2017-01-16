package com.xinnuo.apple.nongda.TeacherActivity.SportsStandards;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class SportsDetails extends BaseActivity {
    private String id;
    private OkHttpClient client;
    private ListView listView;
    private JSONArray dataArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_details);
        initOkHttp();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        listView= (ListView)findViewById(R.id.list_sport_details);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long is) {
                try {
                    //点击相应的班级进行跳转
                    Intent intent=new Intent(SportsDetails.this,Sport_Class.class);
                    JSONObject js = dataArr.getJSONObject(position);
                    //传值 班级id 所教班级id
                    intent.putExtra("id",id);
                    intent.putExtra("sportkindSex",js.getString("sportkindSex"));
                    intent.putExtra("sportkindName",js.getString("sportkindName"));
                    String status = position+1+"";
                    intent.putExtra("status",status);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void onStart(){
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
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.SportsDetails)
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
        dataArr = jsArr;
        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length(); i++) {
            JSONObject jss = jsArr.getJSONObject(i);
             String sportkindName = jss.getString("sportkindName");
             String sportkindSex = jss.optString("sportkindSex");

             HashMap<String, Object> map = new HashMap<String, Object>();
//          ap.put("id",id);
             map.put("sportkindName",sportkindName);
             map.put("sportkindSex",sportkindSex);


             listItem.add(map);

        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_sports_details,
                new  String[]{"sportkindName","sportkindSex"},
                new int[]{R.id.sport_detail1, R.id.sport_detail2});
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
