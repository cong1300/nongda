package com.xinnuo.apple.nongda.TeacherActivity.Substitute;
/**
 * 查询所有教师
 * */
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class QueryAllTheeachersActivity extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private String teacherId;
    private JSONArray dataArr;
    private int positions;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_all_theeachers);
        listView = (ListView) findViewById(R.id.queryall_teacher);
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        initOkHttp();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent=new Intent(QueryAllTheeachersActivity.this,TeacherSubstituteAddActivity.class);
                    JSONObject js = dataArr.getJSONObject(position);
                    positions = position;
                    // 设置显示信息
                    final AlertDialog.Builder builder = new AlertDialog.Builder(QueryAllTheeachersActivity.this);
                    builder.setMessage("确定要提交？").
                            // 设置确定按钮
                                    setPositiveButton("是",
                                    new DialogInterface.OnClickListener() {
                                        // 单击事件
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            // 设置TextView文本
                                            //点击是的时候去进行提交
                                            //点击相应的班级进行跳转
                                            Intent intent=new Intent(QueryAllTheeachersActivity.this,TeacherSubstituteAddActivity.class);
                                            JSONObject js = null;
                                            try {
                                                js = dataArr.getJSONObject(positions-1);
                                                //传值 班级id 所教班级id
                                                intent.putExtra("subTeacherId",js.getString("teacherId"));
                                                intent.putExtra("teacherId",teacherId);
                                                intent.putExtra("name",js.getString("name"));
                                                Log.d("name = ",name);
                                                startActivity(intent);
                                                finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }).
                            // 设置取消按钮
                                    setNegativeButton("否",
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog,
                                                            int which)
                                        {

                                        }
                                    });
                    // 创建对话框
                    AlertDialog ad = builder.create();
                    // 显示对话框
                    ad.show();


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
        map2.put("count", "序 号");
        map2.put("name", " 姓 名");
        map2.put("subjectName", "任教学科");
        map2.put("phone", "电 话");
        listItem.add(map2);
        for (int i = 0; i < jsArr.length(); i++) {
            JSONObject js = jsArr.getJSONObject(i);

            /*
            *
            *
            *
            */
            String count = i + 1 + "";
            name = js.optString("name");
            String subjectName = js.optString("subjectName");
            String phone = js.optString("phone");
            Log.d("******", teacherId);
            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("id",id);
            map.put("count", count);
            map.put("name", name);
            map.put("subjectName", subjectName);
            map.put("phone", phone);

            listItem.add(map);

        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }

        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_queryall_teacher,
                new  String[]{"count","name","subjectName","phone"},
                new int[]{R.id.queryall_teacher_number, R.id.queryall_teacher_name, R.id.queryall_teacher_subjectName, R.id.queryall_teacher_phone});
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
