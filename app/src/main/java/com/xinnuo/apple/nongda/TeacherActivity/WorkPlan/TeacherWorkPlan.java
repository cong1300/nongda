package com.xinnuo.apple.nongda.TeacherActivity.WorkPlan;

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

public class TeacherWorkPlan extends BaseActivity {
    private ListView listView;
    private OkHttpClient client;
    private String teacherId;
    private TextView newlyAdded;
    private JSONArray dataArr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOkHttp();
        setContentView(R.layout.activity_teacher_work_plan);
        listView = (ListView) findViewById(R.id.list_work_plan);

        newlyAdded = (TextView) findViewById(R.id.newly_added);

        final Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        requestWithUserId(teacherId);
        newlyAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(TeacherWorkPlan.this,TeacherAddWorkPlan.class);
                intent1.putExtra("teacherId",teacherId);
                startActivity(intent1);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(TeacherWorkPlan.this,TeacherUpdateWorkPlan.class);
                try {
                    JSONObject work = dataArr.getJSONObject(position);
                    String ids = work.getString("id");
                    String title = work.getString("title");
                    String createDate = work.getString("createDate");
                    String beginDate = work.getString("beginDate");
                    String endDate = work.getString("endDate");
                    String workContent = work.getString("workContent");
                    String state = work.getString("state");
                    String summary = work.getString("summary");
                    Log.d("****",state+workContent+endDate+beginDate);
                    intent1.putExtra("id",ids);
                    intent1.putExtra("title",title);
                    intent1.putExtra("createDate",createDate);
                    intent1.putExtra("beginDate",beginDate);
                    intent1.putExtra("endDate",endDate);
                    intent1.putExtra("workContent",workContent);
                    intent1.putExtra("state",state);
                    intent1.putExtra("summary",summary);
                    intent1.putExtra("teacherId",teacherId);
                    startActivity(intent1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                .add("teacherId",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.WorkPlan)
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


    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {

        JSONArray jsArr = new JSONArray(jsonStr);
        dataArr = jsArr;
        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length(); i++)
        {
            JSONObject js = jsArr.getJSONObject(i);
            String title = js.optString("title");
            String createDate = js.getString("createDate");
            String beginDate = js.getString("beginDate");
            String endDate = js.getString("endDate");
            String workContent = js.getString("workContent");
            String state = js.getString("state");
            String summary = js.getString("summary");
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("title",title);
            map.put("createDate",createDate);
            map.put("beginDate",beginDate);
            map.put("endDate",endDate);
            map.put("workContent",workContent);
            map.put("state",state);
            map.put("summary",summary);
            listItem.add(map);
        }

        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_teacher_work_plan,
                new  String[]{"title","createDate","beginDate","endDate","workContent","state","summary"},
                new int[]{R.id.work_title, R.id.work_createdate, R.id.work_startdate, R.id.work_enddate, R.id.work_plan, R.id.work_yesno, R.id.work_summary});
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
