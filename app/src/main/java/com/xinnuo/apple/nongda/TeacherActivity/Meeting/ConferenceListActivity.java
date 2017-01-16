package com.xinnuo.apple.nongda.TeacherActivity.Meeting;
/**
 * 会议名单
 * */
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ConferenceListActivity extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_list);
        initOkHttp();
        listView = (ListView) findViewById(R.id.meet_conference_list);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Log.d("id=",id);
        requestWithUserId(id);
    }

    /**
     * 请求方法
     * */
    private  void requestWithUserId(String teacherId){
        //菊花的开始方法
        mLoading.show();

        RequestBody requestBodyPost = new FormBody.Builder()
                .add("meetingId",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.MeetingConferenceList)
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
        //dataArr = jsArr;
        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length(); i++)
        {
            JSONObject js = jsArr.getJSONObject(i);
            String teacherName = js.optString("teacherName");
            String type;
            if (js.getString("type").equals("1"))
            {
                type = "未签到";
            }else if (js.getString("type").equals("2"))
            {
                type = "已签到";
            }else if (js.getString("type").equals("3"))
            {
                type = "请假申请中";
            }else if (js.getString("type").equals("4"))
            {
                type = "拒绝";
            }else
            {
                type = "同意";
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("teacherName",teacherName);
            map.put("type",type);
            listItem.add(map);
        }

        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_conference_list,
                new  String[]{"teacherName","type"},
                new int[]{R.id.conference_list, R.id.conference_list1});
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
