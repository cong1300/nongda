package com.xinnuo.apple.nongda.Admin.Club;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.TeacherActivity.club.StudentListActivity;
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

public class AdminTeacherClassActivity extends BaseActivity {
    private OkHttpClient client;
    private String teacherId;
    private String sportClassId;
    private ListView listView;
    private JSONArray dataArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_teacher_class);
        initOkHttp();
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("id");
        Log.d("教师Id  = ",teacherId);

        requestWithUserId();
        listView= (ListView)findViewById(R.id.list_admin_teacher_class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //点击相应的班级进行跳转
                    Intent intent=new Intent(AdminTeacherClassActivity.this,StudentListActivity.class);
                    JSONObject js = dataArr.getJSONObject(position);
                    //传值 班级id 所教班级id
                    intent.putExtra("id",js.getString("id"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
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

            /*
            * "startTime": "3,4节",
                "id": 159,
                "sex": "全",
                "gradeId": "二年级",
                "factAmount": 30,
                "classDate": "周五",
                "sportsClassNo": "0101",
                "ratingAmount": 18,
                "sportsClassName": "健身健美",
                "teacherName": "王大为",
                "schoolDistrictId": "健身馆",
                "teacherId": 19
                */
                String sportsClassNo = js.getString("sportsClassNo");
                String className = js.optString("sportsClassName");
                String teacherName = js.optString("teacherName");
                String factAmount = js.optString("factAmount");
                String schoolDistrictId = js.optString("schoolDistrictId");
                String classDate = js.optString("classDate");
                String startTime = js.optString("startTime");
                Log.d("******",className);
                HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("id",id);
                map.put("sportsClassNo",sportsClassNo);
                map.put("className",className);
                map.put("teacherName",teacherName);
                map.put("factAmount",factAmount);
                map.put("schoolDistrictId",schoolDistrictId);
                map.put("classDate",classDate);
                map.put("startTime",startTime);

                listItem.add(map);
            }
        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_teacher_club,
                new  String[]{"sportsClassNo","className","teacherName","factAmount","schoolDistrictId","classDate","startTime"},
                new int[]{R.id.teacher_club2, R.id.teacher_club4, R.id.teacher_club6, R.id.teacher_club8, R.id.teacher_club10, R.id.teacher_club12, R.id.teacher_club14});
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
