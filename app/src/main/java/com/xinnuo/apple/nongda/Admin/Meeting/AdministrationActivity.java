package com.xinnuo.apple.nongda.Admin.Meeting;

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

public class AdministrationActivity extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private String teacherId;
    private JSONArray dataArr;
    private TextView administration_newrelease;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);
        initOkHttp();
        final Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        listView = (ListView) findViewById(R.id.administration_metinginfo);
        administration_newrelease = (TextView) findViewById(R.id.administration_newrelease);
        requestWithUserId(teacherId);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //点击相应的会议进行跳转
                    Intent intent=new Intent(AdministrationActivity.this,MeetingInfoDetailActivity.class);
                    JSONObject js = dataArr.getJSONObject(position);
                    intent.putExtra("relTime",js.getString("relTime"));
                    intent.putExtra("meetType",js.getString("meetType"));
                    intent.putExtra("teacherinfoName",js.getString("teacherinfoName"));
                    intent.putExtra("theme",js.getString("theme"));
                    intent.putExtra("meetingTime",js.getString("meetingTime"));
                    intent.putExtra("place",js.getString("place"));
                    intent.putExtra("introduction",js.getString("introduction"));
                    intent.putExtra("id",js.getString("id"));
                    intent.putExtra("teacherId",teacherId);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        administration_newrelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AdministrationActivity.this,NewReleaseActivity.class);
                intent1.putExtra("teacherId",teacherId);
                startActivity(intent1);
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
                .add("jurisdiction","1")
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.MeetingInfo)
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
     *   "signinType": "1",
     "id": "94",
     "meetType": "0",
     "meetingTime": "2016-11-18 17:09",
     "theme": "大杨俊伟",
     "teacherinfoName": "admin",
     "relTime": "2016-11-18 17:12:42",
     "type": "2",
     "place": "我们",
     "introduction": "你"
     * */


    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {

        JSONArray jsArr = new JSONArray(jsonStr);
        dataArr = jsArr;
        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length(); i++)
        {
            JSONObject js = jsArr.getJSONObject(i);
            String relTime = js.optString("relTime");
            String meetType;
            if (js.getString("meetType").equals("0"))
            {
                meetType = "签到未开始";
            }else if (js.getString("meetType").equals("1"))
            {
                meetType = "签到进行中，请准备签到";
            }else
            {
                meetType = "签到已结束";
            }
            String teacherinfoName = js.getString("teacherinfoName");
            String theme = js.getString("theme");
            String meetingTime = js.getString("meetingTime");
            String place = js.getString("place");
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("relTime",relTime);
            map.put("meetType",meetType);
            map.put("teacherinfoName",teacherinfoName);
            map.put("theme",theme);
            map.put("meetingTime",meetingTime);
            map.put("place",place);
            listItem.add(map);
        }

        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_meeting_info,
                new  String[]{"relTime","meetType","teacherinfoName","theme","meetingTime","place"},
                new int[]{R.id.teacher_meeting_info1, R.id.teacher_meeting_info2, R.id.teacher_meeting_info3, R.id.teacher_meeting_info4, R.id.teacher_meeting_info5, R.id.teacher_meeting_info6});
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
    private void scanCodeSign(String meetingId){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("meetingId",meetingId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.DeleteMeeting)
                .post(requestBodyPost)
                .build();
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                //signSleepTextView.setText("网络异常！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String retStr = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("网络请求返回值",retStr);

                        try {
                            JSONObject jsObj = new JSONObject(retStr);
                            if (jsObj.getString("melodyClass").equals("yes")){
                                AlertDialog.Builder builder  = new AlertDialog.Builder(AdministrationActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("会议删除成功！" ) ;
                                builder.setPositiveButton("确定" ,  new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        Intent intent = new Intent(AdministrationActivity.this,AdministrationActivity.class);
                                        intent.putExtra("teacherId",teacherId);
                                        startActivity(intent);
                                    }
                                } );
                                builder.show();

                            }else {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(AdministrationActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("会议删除失败！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //statuTV.setText("签到异常");
                        }
                    }
                });
            }
        });
    }
}
