package com.xinnuo.apple.nongda.TeacherActivity.Resultquery;
/**
 * 班级展示
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

public class StudentListQueryActivity extends BaseActivity {
    private OkHttpClient client;
    private String classId;
    private String itemId;
    private ListView listView;
    private String state;
    private JSONArray dataArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list_query);
        initOkHttp();
        listView = (ListView) findViewById(R.id.list_stuquerys);
        Intent intent = getIntent();
        classId = intent.getStringExtra("id");
        itemId = intent.getStringExtra("itemId");
        state = intent.getStringExtra("state");
        if (state .equals("1"))
        {
            requestWithUserId(itemId,classId, httpUrl.studentListUrl);
        }else if(state.equals("2"))
        {
            requestWithUserId(itemId,classId, httpUrl.StudentPhysicalFitnessScore);
        }else  if (state.equals("3"))
        {
            requestWithUserId(itemId,classId, httpUrl.loginUrl);
        }else
        {
            requestWithUserId(itemId,classId, httpUrl.SportsPerformanceQuery);
        }

    }

    /**
     * 请求方法
     * */
    private  void requestWithUserId(String itemId,String id,String url){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("classId",id)
                .add("itemId",itemId)
                .build();
        Request requestPost = new Request.Builder()
                .url(url)
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
                            jsonParseWithJsonStr(retStr,state);
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

    private  void jsonParseWithJsonStr (String jsonStr , String state) throws JSONException {

        JSONArray jsArr = new JSONArray(jsonStr);
        dataArr = jsArr;
        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        if (state .equals("1"))
        {
            HashMap<String, Object> map2 = new HashMap<String, Object>();
            map2.put("count","序 号");
            map2.put("studentNo"," 学 号");
            map2.put("stuname","姓 名");
            map2.put("number","次 数");
            map2.put("state",state);
            listItem.add(map2);
            for (int i = 0; i < jsArr.length(); i++) {
                JSONObject js = jsArr.getJSONObject(i);
            /*
            * "studentNo": "11042227  ",
            *  "name": "张烁",
            *  "number": 0
            */
                String count = i+1+"";
                String studentNo = js.optString("studentNo");
                String stuname = js.optString("name");
                String number = js.optString("number");
                Log.d("******",studentNo);
                HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("id",id);
                map.put("count",count);
                map.put("studentNo",studentNo);
                map.put("stuname",stuname);
                map.put("number",number);

                listItem.add(map);

            }

        }else if (state.equals("2")) {
            HashMap<String, Object> map2 = new HashMap<String, Object>();
            map2.put("count", "序 号");
            map2.put("studentNo", " 学 号");
            map2.put("stuname", "姓 名");
            map2.put("number", "总 分");
            listItem.add(map2);
            for (int i = 0; i < jsArr.length(); i++) {
                JSONObject js = jsArr.getJSONObject(i);

            /*
            * "studentNo": "11042227  ",
            *  "name": "张烁",
            *  "number": 0
            */
                String count = i + 1 + "";
                String studentNo = js.optString("studentId");
                String stuname = js.optString("name");
                String number = js.optString("result");
                Log.d("******", studentNo);
                HashMap<String, Object> map = new HashMap<String, Object>();
//              map.put("id",id);
                map.put("count", count);
                map.put("studentNo", studentNo);
                map.put("stuname", stuname);
                map.put("number", number);

                listItem.add(map);
            }
        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }

        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_student_list_query,
                new  String[]{"count","studentNo","stuname","number"},
                new int[]{R.id.studentlist_query1, R.id.studentlist_query2, R.id.studentlist_query3, R.id.studentlist_query4});
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
