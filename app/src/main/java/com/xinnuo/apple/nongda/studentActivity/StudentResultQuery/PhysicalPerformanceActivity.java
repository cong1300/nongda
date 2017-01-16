package com.xinnuo.apple.nongda.studentActivity.StudentResultQuery;
/**
 * 体质成绩查询
 * */
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

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

public class PhysicalPerformanceActivity extends BaseActivity {
    private OkHttpClient client;
    private String id;
    private ListView listView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_performance);
        initOkHttp();
        listView = (ListView) findViewById(R.id.list_stu_resultquery);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
    }
    protected void onStart()
    {
        super.onStart();
        requestWithUserId();
    }
    //请求方法
    private  void requestWithUserId(){
        mLoading.show();
        Log.d("StudentList ==== ", id);
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("studentId",id)   //id
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.StudentDetailedScore)    //请求地址
                .post(requestBodyPost)
                .build();
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            //接收后台返回值方法
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
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }

    //绑定控件


    //json解析
    /*
    * 测试数据
    *  "result": "51.60",
        "sex": "2",
        "weight": "54.3",
        "gradeId": "4",
        "vital_capacity_mark": "0.0",
        "jump_mark": "100.0",
        "name": "董萍",
        "studentNo": "12011101",
        "kiol_awarded_marks": "0",
        "vital_capacity_evaluate": "不及格",
        "stature": "166.3",
        "kilo_evaluate": "0",
        "jump_evaluate": "优秀",
        "bend_evaluate": "及格",
        "kilo_capacity": "0",
        "lie_capacity": "0",
        "health": "不及格",
        "kilo_mark": "0",
        "jump": "211",
        "lie_awarded_marks": "0",
        "vital_capacity": "641",
        "Bmi_mark": "100.0",
        "bendl_capacity": "11.0",
        "fifty_mark": "100.0",
        "lie_mark": "0",
        "bmi_evaluate": "100.0",
        "fifty_evaluate": "优秀",
        "lie_evaluate": "0",
        "bend_mark": "66.0",
        "fifty_capacity": "6.53"
    * */

    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {




        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/



        JSONObject js = new JSONObject(jsonStr);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("Item1",js.getString("name"));
        map.put("Item2", js.getString("studentNo"));
        String sex;
        if (js.getString("sex").equals("1")) {
            sex= "男";

        }else{
            sex="女";
        }
        map.put("Item3", sex);
        String gradeId;
        if (js.getString("gradeId").equals("1"))
        {
            gradeId = "一年级";
        }else if (js.getString("gradeId").equals("2"))
        {
            gradeId = "二年级";
        }else if (js.getString("gradeId").equals("3"))
        {
            gradeId = "三年级";
        }else
        {
            gradeId = "四年级";
        }
        map.put("Item4", gradeId);
        //成绩
        map.put("Item6", js.getString("stature"));
        map.put("Item7", js.getString("weight"));
        map.put("Item8", js.getString("vital_capacity"));
        map.put("Item9", js.getString("jump"));
        map.put("Item10", js.getString("lie_capacity"));
        map.put("Item11", js.getString("bendl_capacity"));
        map.put("Item12", js.getString("fifty_capacity"));
        map.put("Item13", js.getString("kilo_capacity"));
        map.put("Item14", js.getString("lie_awarded_marks"));
        map.put("Item15", js.getString("kiol_awarded_marks"));
        //得分
        map.put("Item16", js.getString("Bmi_mark"));
        map.put("Item17", js.getString("Bmi_mark"));
        map.put("Item18", js.getString("vital_capacity_mark"));
        map.put("Item19", js.getString("jump_mark"));
        map.put("Item20", js.getString("lie_mark"));
        map.put("Item21", js.getString("bend_mark"));
        map.put("Item22", js.getString("fifty_mark"));
        map.put("Item23", js.getString("kilo_mark"));
        map.put("Item24", "0");
        map.put("Item25", "0");
        map.put("Item36", js.getString("result"));
        //评价
        map.put("Item26", js.getString("bmi_evaluate"));
        map.put("Item27", js.getString("bmi_evaluate"));
        map.put("Item28", js.getString("vital_capacity_evaluate"));
        map.put("Item29", js.getString("jump_evaluate"));
        map.put("Item30", js.getString("lie_evaluate"));
        map.put("Item31", js.getString("bend_evaluate"));
        map.put("Item32", js.getString("fifty_evaluate"));
        map.put("Item33", js.getString("kilo_evaluate"));
        map.put("Item34", " ");
        map.put("Item35", " ");
        map.put("Item37", js.getString("health"));
        listItem.add(map);

        if (js.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter;
        if (js.getString("sex").equals("1")) {
            qrAdapter = new SimpleAdapter(this, listItem, R.layout.item_student_detailed_boy_query,
                    new String[]{"Item1", "Item2", "Item3", "Item4", "Item6", "Item7", "Item8", "Item9", "Item10", "Item11", "Item12", "Item13", "Item14", "Item15", "Item16", "Item17", "Item18", "Item19", "Item20",
                            "Item21", "Item22", "Item23", "Item24", "Item25", "Item26", "Item27", "Item28", "Item29", "Item30", "Item31", "Item32", "Item33", "Item34", "Item35", "Item36", "Item37"},
                    new int[]{R.id.detailed56, R.id.detailed58, R.id.detailed52, R.id.detailed54, R.id.detailed8, R.id.detailed13, R.id.detailed18, R.id.detailed23, R.id.detailed28, R.id.detailed33
                            , R.id.detailed38, R.id.detailed43, R.id.detailed48, R.id.detailed61, R.id.detailed9, R.id.detailed14, R.id.detailed19, R.id.detailed24, R.id.detailed29, R.id.detailed34,
                            R.id.detailed39, R.id.detailed44, R.id.detailed49, R.id.detailed62, R.id.detailed10, R.id.detailed15, R.id.detailed20, R.id.detailed25, R.id.detailed30, R.id.detailed35,
                            R.id.detailed40, R.id.detailed45, R.id.detailed50, R.id.detailed63, R.id.detailed67, R.id.detailed68});
        }else {
            qrAdapter = new SimpleAdapter(this, listItem, R.layout.item_student_detailed_girl_query,
                    new String[]{"Item1", "Item2", "Item3", "Item4", "Item6", "Item7", "Item8", "Item9", "Item10", "Item11", "Item12", "Item13", "Item14", "Item15", "Item16", "Item17", "Item18", "Item19", "Item20",
                            "Item21", "Item22", "Item23", "Item24", "Item25", "Item26", "Item27", "Item28", "Item29", "Item30", "Item31", "Item32", "Item33", "Item34", "Item35", "Item36", "Item37"},
                    new int[]{R.id.detailed56, R.id.detailed58, R.id.detailed52, R.id.detailed54, R.id.detailed8, R.id.detailed13, R.id.detailed18, R.id.detailed23, R.id.detailed28, R.id.detailed33
                            , R.id.detailed38, R.id.detailed43, R.id.detailed48, R.id.detailed61, R.id.detailed9, R.id.detailed14, R.id.detailed19, R.id.detailed24, R.id.detailed29, R.id.detailed34,
                            R.id.detailed39, R.id.detailed44, R.id.detailed49, R.id.detailed62, R.id.detailed10, R.id.detailed15, R.id.detailed20, R.id.detailed25, R.id.detailed30, R.id.detailed35,
                            R.id.detailed40, R.id.detailed45, R.id.detailed50, R.id.detailed63, R.id.detailed67, R.id.detailed68});
        }
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
