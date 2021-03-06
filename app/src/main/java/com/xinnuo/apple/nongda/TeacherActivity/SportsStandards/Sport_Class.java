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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Sport_Class extends BaseActivity {
    private OkHttpClient client;
    private String id;
    private String sportkindSex;
    private String sportkindName;
    private String status;
    private ListView listView;
    private JSONArray dataArr;
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport__class);
        initOkHttp();
        listView = (ListView) findViewById(R.id.list_stu_sport_class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long is) {
                try {
                    if (state.equals("2")){
                        //点击相应的班级进行跳转
                        Intent intent=new Intent(Sport_Class.this,SportsScore.class);
                        JSONObject js = dataArr.getJSONObject(position);
                        //传值 班级id 所教班级id
                        intent.putExtra("id",id);
                        intent.putExtra("sex",js.getString("sex"));
                        intent.putExtra("studentId",js.getString("id"));
                        intent.putExtra("studentNo",js.getString("studentNo"));
                        intent.putExtra("name",js.getString("name"));
                        intent.putExtra("sportkindSex",sportkindSex);
                        intent.putExtra("sportkindName",sportkindName);
                        intent.putExtra("status",status);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent=new Intent(Sport_Class.this,SportSecondActivity.class);
                        intent.putExtra("status",status);
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    protected void onStart()
    {
        super.onStart();
        //取出上个界面传过来的id
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        sportkindSex = intent.getStringExtra("sportkindSex");
        sportkindName = intent.getStringExtra("sportkindName");
        status = intent.getStringExtra("status");
        state = intent.getStringExtra("state");
        Log.d("*****",status+"*****************************");
        requestWithUserId(id);
    }
    //请求方法
    private  void requestWithUserId(String id){
        mLoading.show();
        Log.d("StudentList ==== ", id);
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",id)   //id
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherClubStudentNumberUrl)    //请求地址
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

        JSONArray jsArr = new JSONArray(jsonStr);
        dataArr = jsArr;
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/

        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("Item1","序 号");
        map2.put("Item2"," 学 号");
        map2.put("Item3","姓 名");
        map2.put("Item4","班 级");
        map2.put("Item5","性 别");
        listItem.add(map2);

        /*
        *   "id": 42960,
            "sex": "1",(1 = 男 、2 = 女)
            "studentNo": "14011411",
            "gradeId": 2,
            "name": "姜博",
            "className": "汉语14-4",
            "collegeName": "人文学院"*/
        for (int i = 0; i < jsArr.length(); i++) {

            String jsStr = jsArr.getString(i);
            JSONObject js = jsArr.getJSONObject(i);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Item1",i+1);
            map.put("Item2", js.getString("studentNo"));
            map.put("Item3", js.getString("name"));
            map.put("Item4", js.getString("collegeName")+"\n"+js.getString("className"));
            String sex = null;
            if (js.getString("sex").equals("1")){
                sex = "男";
                map.put("Item5", sex);
            } else{
                sex = "女";
                map.put("Item5", sex);
            }
            if (sportkindSex.equals(sex)){
                listItem.add(map);
            }



        }





        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_teacher_classsize,
                new  String[]{"Item1","Item2","Item3","Item4","Item5"},
                new int[]{R.id.teacher_stunumber1, R.id.teacher_stunumber2, R.id.teacher_stunumber3, R.id.teacher_stunumber4, R.id.teacher_stunumber5});


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
