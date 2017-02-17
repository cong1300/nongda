package com.xinnuo.apple.nongda.Admin.Substitute;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

public class AdminSubstituteActivity extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private JSONArray dataArr;
    private String teacherId;
    private int number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_substitute);
        listView = (ListView) findViewById(R.id.admin_list_substitute);
        initOkHttp();
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                        number = position;
                        final String[] items = new String[]{"同意","拒绝","取消"};
                        final AlertDialog.Builder builder = new AlertDialog.Builder(AdminSubstituteActivity.this);
                        builder.setTitle("操作该条申请？");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Toast.makeText(AdminSubstituteActivity.this, "您选中了："+items[0], Toast.LENGTH_SHORT).show();
                                        JSONObject js = null;
                                        try {
                                            js = dataArr.getJSONObject(number);
                                            Log.d("网络请求返回值","if"+js.getString("substituteId"));
                                            RequestBody requestBodyPost = new FormBody.Builder()
                                                    .add("id",js.getString("substituteId"))
                                                    .build();
                                            Request requestPost = new Request.Builder()
                                                    .url(httpUrl.AdminYes)
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
                                                                    Intent intent1 = new Intent(AdminSubstituteActivity.this,AdminSubstituteActivity.class);
                                                                    intent1.putExtra("teacherId",teacherId);
                                                                    Log.d("网络请求返回值","if");
                                                                    startActivity(intent1);
                                                                }else{
                                                                    AlertDialog.Builder builder  = new AlertDialog.Builder(AdminSubstituteActivity.this);
                                                                    builder.setTitle("提示" ) ;
                                                                    builder.setMessage("异常！" ) ;
                                                                    builder.setPositiveButton("确定" ,  null );
                                                                    builder.show();
                                                                    Log.d("网络请求返回值","else");
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

                                        break;
                                    case 1:
                                        Toast.makeText(AdminSubstituteActivity.this, "您选中了："+items[1], Toast.LENGTH_SHORT).show();
                                        try {
                                            js = dataArr.getJSONObject(number);
                                            RequestBody requestBodyPost = new FormBody.Builder()
                                                    .add("id",js.getString("substituteId"))
                                                    .build();
                                            Request requestPost = new Request.Builder()
                                                    .url(httpUrl.AdminNo)
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
                                                                    Intent intent1 = new Intent(AdminSubstituteActivity.this,AdminSubstituteActivity.class);
                                                                    intent1.putExtra("teacherId",teacherId);
                                                                    startActivity(intent1);
                                                                }else{
                                                                    AlertDialog.Builder builder  = new AlertDialog.Builder(AdminSubstituteActivity.this);
                                                                    builder.setTitle("提示" ) ;
                                                                    builder.setMessage("异常！" ) ;
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

                                        break;
                                    case 2:

                                        Toast.makeText(AdminSubstituteActivity.this, "您选中了："+items[2], Toast.LENGTH_SHORT).show();
                                        break;
                                }

                            }

                        }).show();
                    //点击相应的会议进行跳转
                    //final Intent intent=new Intent(AdminSubstituteActivity.this,MeetingInfoDetailedActivity.class);

                } catch (Exception e) {
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
    private void requestWithUserId(){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.AdminInfo)
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
                state = "申请中";
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
