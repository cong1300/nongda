package com.xinnuo.apple.nongda.TeacherActivity.Substitute;
/**
 * 代课主界面
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
import android.widget.TextView;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.TeacherActivity.Meeting.MeetingInfoDetailedActivity;
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

public class SubstituteActivity extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private JSONArray dataArr;
    private String teacherId;
    private int number;
    private TextView add_application;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitute);
        listView = (ListView) findViewById(R.id.teacher_list_substitute);
        add_application = (TextView) findViewById(R.id.add_application);
        initOkHttp();
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //点击相应的会议进行跳转
                    final Intent intent=new Intent(SubstituteActivity.this,MeetingInfoDetailedActivity.class);
                    JSONObject js = dataArr.getJSONObject(position);
                    number = position;
                    if (js.getString("state").equals("1"))
                    {
                        // 设置显示信息
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SubstituteActivity.this);

                        builder.setMessage("是否确认取消申请？").
                                // 设置确定按钮
                                        setPositiveButton("是",
                                        new DialogInterface.OnClickListener() {
                                            // 单击事件
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                // 设置TextView文本
                                                //点击是的时候去进行提交
                                                // 单击事件

                                                    JSONObject js = null;
                                                    try {
                                                        js = dataArr.getJSONObject(number);
                                                        RequestBody requestBodyPost = new FormBody.Builder()
                                                                .add("id",js.getString("substituteId"))
                                                                .build();
                                                        Request requestPost = new Request.Builder()
                                                                .url(httpUrl.TeacherWithdrawSubstitute)
                                                                .post(requestBodyPost)
                                                                .build();
                                                        client.newCall(requestPost).enqueue(new Callback() {
                                                            @Override
                                                            public void onFailure(Call call, IOException e) {

                                                            }
                                                            //接收方法
                                                            @Override
                                                            public void onResponse(Call call, Response response) throws IOException {
                                                                final String retStr = response.body().string();
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        mLoading.dismiss();

                                                                        Log.d("网络请求返回值",retStr);
                                                                        try {
                                                                            JSONObject js = new JSONObject(retStr);
                                                                            if (js.getString("melodyClass").equals("yes")){
                                                                                Intent intent1 = new Intent(SubstituteActivity.this,SubstituteActivity.class);
                                                                                intent1.putExtra("teacherId",teacherId);
                                                                                startActivity(intent1);
                                                                            }else{
                                                                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteActivity.this);
                                                                                builder.setTitle("提示" ) ;
                                                                                builder.setMessage("取消申请代课失败，请重新申请！" ) ;
                                                                                builder.setPositiveButton("确定" ,  null );
                                                                                builder.show();
                                                                            }

                                                                        } catch (JSONException e1) {
                                                                            e1.printStackTrace();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        });
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


                        builder.show();

                    }else {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        add_application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubstituteActivity.this,TeacherSubstituteAddActivity.class);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
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
    private void requestWithUserId(){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherSubstitute)
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
     *   "cardNo": "12345678",
         "reason": "22222",
         "substituteTeacherName": "车毅",
         "state": 3,
         "type": 2,
         "substituteDate": "2016-11-14 00:00:00.0",
         "teacherName": "杨俊伟",
         "substituteId": 37
     * */


    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {

        JSONArray jsArr = new JSONArray(jsonStr);
        dataArr = jsArr;
        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/

        for (int i = 0; i < jsArr.length(); i++)
        {
            JSONObject js = jsArr.getJSONObject(i);
            String teacherName = js.optString("teacherName");
            String state;
            if (js.getString("state").equals("1"))
            {
                state = "申请中[点击撤销申请]";
            }else if (js.getString("state").equals("2"))
            {
                state = "通过";
            }else
            {
                state = "拒绝申请";
            }
            String type;
            if (js.getString("type").equals("1"))
            {
                type = "代课";
            }else
            {
                type = "合课";
            }
            String cardNo = js.getString("cardNo");
            String substituteDate = js.getString("substituteDate");
            String reason = js.getString("reason");
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("teacherName",teacherName);
            map.put("cardNo",cardNo);
            map.put("substituteDate",substituteDate);
            map.put("reason",reason);
            map.put("type",type);
            map.put("state",state);
            listItem.add(map);
        }

        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_teacher_substitute,
                new  String[]{"teacherName","cardNo","substituteDate","reason","type","state"},
                new int[]{R.id.teacher_substitute_name, R.id.teacher_substitute_cardNo, R.id.teacher_substitute_time, R.id.teacher_substitute_reason, R.id.teacher_substitute_class, R.id.teacher_substitute_state});
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
