package com.xinnuo.apple.nongda.TeacherActivity.CourseAttendance;
/**
 * 课程签到   班级列表
 * */
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class Class_ListActivity extends BaseActivity {
    private OkHttpClient client;
    private String teacherId;
    private String itemId;
    private ListView listView;
    private JSONArray dataArr;
    private TextView substitute_course;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class__list);
        listView = (ListView) findViewById(R.id.class_list);
        substitute_course = (TextView) findViewById(R.id.substitute_course);
        initOkHttp();
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        itemId = intent.getStringExtra("itemId");
        substitute_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Class_ListActivity.this,SubstituteCourseActivity.class);
                intent1.putExtra("teacherId",teacherId);
                intent1.putExtra("itemId",itemId);
                startActivity(intent1);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //点击相应的班级进行跳转
                try {

                        JSONObject js = dataArr.getJSONObject(position);
                        Intent intent = new Intent(Class_ListActivity.this,ClassListDetailsActivity.class);
                        intent.putExtra("sportclassId",js.getString("sportClassId"));
                        intent.putExtra("teacherId",teacherId);
                        intent.putExtra("teacherId",js.getString("id"));
                        intent.putExtra("itemId",itemId);
                        startActivity(intent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                .add("teacherId",teacherId)
                .add("itemId",itemId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherClassList)
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
     * 测试数据：
     *   "startTime": "5,6节",
         "id": 27,
         "subName": "体育班",
         "sportClassId": 204,
         "name": "杨俊伟",
         "classDate": "周一",
         "number": "1001",
         "collegeName": "",
         "sportClassName": "乒乓球"
     * */


    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {

        JSONArray jsArr = new JSONArray(jsonStr);
        dataArr = jsArr;
        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length(); i++)
        {
            JSONObject js = jsArr.getJSONObject(i);
            String sportClassName = js.optString("sportClassName");
            String subName = js.optString("subName");
            String classDate = js.optString("classDate")+"\t"+js.optString("startTime");
            String name = js.optString("name");
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("sportClassName",sportClassName);
            map.put("subName",subName);
            map.put("classDate",classDate);
            map.put("name",name);
            listItem.add(map);
        }

        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_class_list,
                new  String[]{"sportClassName","subName","classDate","name"},
                new int[]{R.id.class_list_className, R.id.class_list_number, R.id.class_list_time, R.id.class_list_teacher});
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
