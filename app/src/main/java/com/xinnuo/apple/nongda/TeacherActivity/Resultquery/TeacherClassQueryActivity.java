package com.xinnuo.apple.nongda.TeacherActivity.Resultquery;
/**
 * 展示班级列表
 * */

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

public class TeacherClassQueryActivity extends BaseActivity {
    private OkHttpClient client;
    private String teacherId;
    private String itemId;
    private JSONArray dataArr;
    private ListView listView;
    private String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class_query);
        initOkHttp();
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        itemId = intent.getStringExtra("itemId");
        state = intent.getStringExtra("state");
        requestWithUserId(teacherId);
        listView= (ListView)findViewById(R.id.list_classList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //点击相应的班级进行跳转
                    if (state.equals("2")){
                        Intent intent=new Intent(TeacherClassQueryActivity.this,PhysicalFitnessTestActivity.class);
                        JSONObject js = dataArr.getJSONObject(position);
                        //传值 班级id 所教班级id
                        intent.putExtra("sportsClassNo",js.getString("sportsClassNo"));
                        intent.putExtra("itemId",itemId);
                        intent.putExtra("state",state);
                        startActivity(intent);
                    }else{
                        Intent intent=new Intent(TeacherClassQueryActivity.this,StudentListQueryActivity.class);
                        JSONObject js = dataArr.getJSONObject(position);
                        //传值 班级id 所教班级id
                        intent.putExtra("id",js.getString("id"));
                        intent.putExtra("itemId",itemId);
                        intent.putExtra("state",state);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(String teacherId){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.teacherClubUrl)
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
            JSONArray jsarr = jss.getJSONArray("classList");
            dataArr = jsarr;
            for (int j = 0; j < jsarr.length(); j++)
            {
                JSONObject js = jsarr.getJSONObject(j);
                String className = js.optString("sportsClassName")+" / "+js.optString("classDate");
                Log.d("******",className);
                HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("id",id);
                map.put("className",className);

                listItem.add(map);
            }
        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_teacher_class_query,
                new  String[]{"className"},
                new int[]{R.id.teacher_class});
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
