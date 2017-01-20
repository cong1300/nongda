package com.xinnuo.apple.nongda.Admin.Club;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.xinnuo.apple.nongda.Admin.AttendanceData.AttendanceDataActivity;
import com.xinnuo.apple.nongda.Admin.ResultQuery.AdminResultQueryActivity;
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

public class AdminClub extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private String teacherId;
    private JSONArray dataArr;
    private int positions;
    private String name;
    private String state;
    private String coordinateId;
    private String phone;
    private String subjectName;
    private String item;
    private String statu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_club);
        initOkHttp();
        Intent intent = getIntent();
        statu = intent.getStringExtra("statu");
        listView = (ListView) findViewById(R.id.admin_all_teacher);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //点击相应的班级进行跳转
                    if (statu.equals("0")){
                        Intent intent=new Intent(AdminClub.this,AdminTeacherClassActivity.class);
                        JSONObject js = dataArr.getJSONObject(position-1);
                        //传值 班级id 所教班级id
                        intent.putExtra("id",js.getString("teacherId"));
                        startActivity(intent);
                    }else if (statu.equals("1")){
                        Intent intent=new Intent(AdminClub.this,AttendanceDataActivity.class);
                        JSONObject js = dataArr.getJSONObject(position-1);
                        //传值 班级id 所教班级id
                        intent.putExtra("id",js.getString("teacherId"));
                        startActivity(intent);
                    }else if (statu.equals("2")){
                        Intent intent=new Intent(AdminClub.this,AdminResultQueryActivity.class);
                        JSONObject js = dataArr.getJSONObject(position-1);
                        //传值 班级id 所教班级id
                        intent.putExtra("id",js.getString("teacherId"));
                        intent.putExtra("itemId",js.getString("item"));
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onStart() {
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
                .url(httpUrl.QueryAllTheeachers)
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
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("Item1","序 号");
        map2.put("Item2","姓 名");
        map2.put("Item3","任教学科");
        map2.put("Item4","电 话");
        listItem.add(map2);
        for (int i = 0; i < jsArr.length(); i++) {
            JSONObject js = jsArr.getJSONObject(i);
            name = js.optString("name");
            subjectName = js.optString("subjectName");
            phone = js.optString("phone");
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Item1", i+1);
            map.put("Item2", name);
            map.put("Item3", subjectName);
            map.put("Item4", phone);
            listItem.add(map);
        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }

        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_admin_all_teacher,
                new  String[]{"Item1","Item2","Item3","Item4"},
                new int[]{R.id.admin_number,R.id.admin_name,R.id.admin_subject,R.id.admin_phone});
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
