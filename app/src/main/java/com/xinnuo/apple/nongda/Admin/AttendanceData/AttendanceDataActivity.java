package com.xinnuo.apple.nongda.Admin.AttendanceData;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.view.XListView;

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

public class AttendanceDataActivity extends BaseActivity implements XListView.IXListViewListener{

    private OkHttpClient client;
    private String teacherId;
    private int page;
    private ArrayList<HashMap<String, Object>> dlist;
    private boolean flag = false;
    private ArrayAdapter<String> mAdapter;
    private int number = 0;
    private Handler mHandler;
    private ListView listView;
    private boolean isFirstIn = true;
    private RelativeLayout mHeaderViewContent;
    private TextView tea_attendance_record;         //考勤情况
    private TextView tea_turnover_record;           //离职情况
    private TextView tea_tendance_times;           //考勤次数及离职次数
    private XListView mListView;
    private ArrayList<String> items;
    private ArrayList<String> items1;
    private String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_data);
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("id");
        tea_attendance_record = (TextView) findViewById(R.id.admin_attendance_record);
        tea_turnover_record = (TextView) findViewById(R.id.admin_turnover_record);
        tea_tendance_times = (TextView) findViewById(R.id.admin_number);
        initOkHttp();
        tea_attendance_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.clear();
                state = "1";
                number = 0;
                requestWithDataAdd(0,state);

            }
        });
        tea_turnover_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.clear();
                state = "0";
                number = 0;
                requestWithDataAdd(0,state);

            }
        });
    }
    protected void onStart()
    {
        super.onStart();
        state = "1";
        requestWithDataAdd(0,state);
        mListView = (XListView) findViewById(R.id.admin_xListView);
        mListView.setPullLoadEnable(true);
        mAdapter = new ArrayAdapter<String>(AttendanceDataActivity.this, R.layout.item_attendance_data_sta, items);
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(AttendanceDataActivity.this);
        mHandler = new Handler();
    }

    /**
     * 网络请求
     * @param add add = 0 刷新 add = 1 加载更多
     */
    private  void requestWithDataAdd(int add,String state){

        if (add == 0)
        {
            page = 1;
            items = new ArrayList<String>();

        }
        else
        {
            page ++;
        }
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherid",teacherId)
                .add("pageNumber",page+"")
                .add("state",state)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.AttendanceDataStatistics)
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
     * 0 离职 1未离职
     * */
    //json解析方法

    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {
        try{
            JSONArray jsArr = null;
            String state = null;
            if ((!jsonStr.equals("[]")) || (!jsonStr.equals("{\"nul\":\"\"}")))
            {
                jsArr = new JSONArray(jsonStr);
                String currName = null;
                String attendEndDate = null;
                for (int i = 0; i < jsArr.length(); i++)
                {
                    JSONObject js = jsArr.getJSONObject(i);
                    state = js.getString("state");

                    if (state.equals("0"))
                    {
                        JSONArray jss = js.getJSONArray("date");
                        number ++;
                        for (int j = 0 ; j < jss.length(); j++)
                        {
                            JSONObject jsonObject = jss.getJSONObject(j);
                            currName = "离职时间:" + jsonObject.getString("DepartureTime");
                            tea_tendance_times.setText("考勤:"+number+"次");
                            items.add(currName);
                        }
                    }else
                    {
                        JSONArray jss = js.getJSONArray("date");
                        number ++;
                        for (int j = 0 ; j < jss.length(); j++)
                        {
                            JSONObject jsonObject = jss.getJSONObject(j);
                            currName = "上课时间:" + jsonObject.getString("attendDate");
                            attendEndDate = "下课时间:"+ jsonObject.getString("attendEndDate");
                            String s = currName + "\n" +attendEndDate;
                            tea_tendance_times.setText("考勤:"+number+"次");
                            items.add(s);
                        }
                    }
                }
            }else if (jsonStr.equals("{\"nul\":\"\"}")){
                midToast("无数据",3);
            }else {
                midToast("无数据",3);
            }

            mAdapter = new ArrayAdapter<String>(AttendanceDataActivity.this, R.layout.item_attendance_data_sta, items);
            mListView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            onLoad();
        }catch (Exception e)
        {
            midToast("无数据",3);
            return;
        }


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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
        }
        return false;
    }


    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (state.equals("0"))
                {
                    requestWithDataAdd(1,state);
                    onLoad();
                }else
                {
                    requestWithDataAdd(1,state);
                    onLoad();
                }
            }
        }, 2000);

    }
    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("刚刚");
    }


    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (state.equals("0"))
                {
                    requestWithDataAdd(page,state);
                    onLoad();
                }else
                {
                    requestWithDataAdd(page,state);
                    onLoad();
                }
            }
        }, 2000);
    }
}
