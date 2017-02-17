package com.xinnuo.apple.nongda.TeacherActivity.CourseAttendance;
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
import com.xinnuo.apple.nongda.TeacherActivity.Substitute.TeacherSubstituteAddActivity;
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

public class AllTeachersActivity extends BaseActivity {

    private OkHttpClient client;
    private ListView listView;
    private String teacherId;
    private JSONArray dataArr;
    private int positions;
    private String name;
    private String state;
    private String coordinateId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_teachers);
        listView = (ListView) findViewById(R.id.all_teacher);
        Intent intent = getIntent();
        coordinateId = intent.getStringExtra("coordinateId");
        teacherId = intent.getStringExtra("teacherId");
        state = intent.getStringExtra("state");
        initOkHttp();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent=new Intent(AllTeachersActivity.this,TeacherSubstituteAddActivity.class);
                    JSONObject js = dataArr.getJSONObject(position);
                    positions = position;
                    // 设置显示信息
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AllTeachersActivity.this);
                    builder.setMessage("确定要提交？").
                            // 设置确定按钮
                                    setPositiveButton("是",
                                    new DialogInterface.OnClickListener() {
                                        // 单击事件
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            //点击相应的班级进行跳转
                                            try {
                                                if (state.equals("1"))
                                                {
                                                    Intent intent=new Intent(AllTeachersActivity.this,SubstituteCourseActivity.class);
                                                    JSONObject js = dataArr.getJSONObject(positions);
                                                    //传值 班级id 所教班级id
                                                    intent.putExtra("substituteId",js.getString("substituteId"));
                                                    intent.putExtra("teacherId",teacherId);
                                                    intent.putExtra("teacherId1",js.getString("teacherId"));
                                                    intent.putExtra("teacherName",js.getString("teacherName"));
                                                    intent.putExtra("itemId",js.getString("itemId"));
                                                    Log.d("name = ",name);
                                                    startActivity(intent);
                                                }else
                                                {
                                                    Intent intent=new Intent(AllTeachersActivity.this,CombinedLessonActivity.class);

                                                    JSONObject js = dataArr.getJSONObject(positions);
                                                    //传值 班级id 所教班级id
                                                    intent.putExtra("substituteId",js.getString("substituteId"));
                                                    intent.putExtra("teacherId",teacherId);
                                                    intent.putExtra("teacherId1",js.getString("teacherId"));
                                                    intent.putExtra("teacherName",js.getString("teacherName"));
                                                    intent.putExtra("itemId",js.getString("itemId"));
                                                    Log.d("name = ",name);
                                                    startActivity(intent);
                                                }

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
                .add("subTeacherId",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherSubstituteTeachersQuery)
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
        for (int i = 0; i < jsArr.length(); i++) {
            JSONObject js = jsArr.getJSONObject(i);
            name = js.optString("teacherName");
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            listItem.add(map);
        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }

        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_all_teacher,
                new  String[]{"name"},
                new int[]{R.id.all_teacher1});
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
