package com.xinnuo.apple.nongda.studentActivity.StudentResultQuery;
/**
 * 签到记录查询
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

public class StuAttendanceRecordActivity extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_attendance_record);
        initOkHttp();
        listView = (ListView) findViewById(R.id.list_stu_attendance_record);
        Intent intent = getIntent();
        studentId = intent.getStringExtra("id");
        Log.d("studentId = ",studentId);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent=new Intent(StuAttendanceRecordActivity.this,SignInTimeActivity.class);
                intent.putExtra("studentId",studentId);
                startActivity(intent);
            }
        });
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
            JSONArray jsarr = jss.getJSONArray("testDate");
            String className = jss.optString("sportClassName");
            String name = jss.getString("name");
            String number = jsarr.length() + "";
            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("id",id);
            map.put("className",className);
            map.put("name",name);
            map.put("number",number);
            listItem.add(map);
        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem,R.layout.item_stu_attendance_record,
                new  String[]{"className","name","number"},
                new int[]{R.id.stu_attendance_curriculum,R.id.stu_attendance_teacher,R.id.stu_attendance_number});
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
