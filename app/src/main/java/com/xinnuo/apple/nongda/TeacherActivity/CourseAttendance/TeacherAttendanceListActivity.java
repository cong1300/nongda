package com.xinnuo.apple.nongda.TeacherActivity.CourseAttendance;
/**
 * 考勤列表
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

public class TeacherAttendanceListActivity extends BaseActivity {
    private OkHttpClient client;
    private String sportclassId;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance_list);
        listView = (ListView) findViewById(R.id.teacher_attendance_list);
        initOkHttp();
        Intent intent = getIntent();
        sportclassId = intent.getStringExtra("sportclassId");
        requestWithUserId();
    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("sportclassId",sportclassId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherAttendanceList)
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
     * 解析数据
     * 测试数据
     *   "sex": "2",
         "name": "石健美",
         "sportClass": "乒乓球",
         "type": "no",
         "sutdentNo": "11042220"
     * */


    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {

        JSONArray jsArr = new JSONArray(jsonStr);

        List<String> data = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length(); i++) {
            JSONObject js = jsArr.getJSONObject(i);
            String count = i+1+"";
            String sutdentNo = js.getString("sutdentNo");
            String name = js.getString("name");
            String sex;
            if (js.getString("sex").equals("1"))
            {
                sex = "男";
            }else
            {
                sex = "女";
            }
            String sportClass = js.getString("sportClass");
            String type ;
            if (js.getString("type").equals("yes"))
            {
                type = js.getString("date");
            }else
            {
                type = "未签到";
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("count",count);
            map.put("sutdentNo",sutdentNo);
            map.put("name",name);
            map.put("sex",sex);
            map.put("sportClass",sportClass);
            map.put("type",type);
            listItem.add(map);

        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem, R.layout.item_teacher_attendance_list,
                new  String[]{"count","sutdentNo","name","sex","sportClass","type"},
                new int[]{R.id.tea_att_list_number, R.id.tea_att_list_stunumber, R.id.tea_att_list_name, R.id.tea_att_list_sex, R.id.tea_att_list_class, R.id.tea_att_list_time,});
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
