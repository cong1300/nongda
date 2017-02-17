package com.xinnuo.apple.nongda.studentActivity.OnlineTest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApplyForExemptionActivity extends AppCompatActivity {
    private OkHttpClient client;
    private LinearLayout Layout_application;        //添加申请
    private LinearLayout apply_layout;               //申请列表
    private TextView stuName;                         //学生姓名
    private TextView stuNo;                           //学生编号
    private TextView stuTime;                         //申请时间
    private TextView stuState;                        //状态（1、成功 2、否 3、申请中 4、拒绝）
    private Button apply_withdraw;                   //取消申请
    private String id;
    private String name;
    private String studentNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for_exemption);
        initOkHttp();
        binding();
        final Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        studentNo = intent.getStringExtra("stuNo");
        apply_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ApplyForExemptionActivity.this);

                builder.setMessage("是否确认取消申请！").
                        // 设置确定按钮
                                setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        scanCodeSign1("2");


                                    }
                                }).
                        // 设置取消按钮
                                setNegativeButton("取消",
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
            }
        });
        Layout_application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ApplyForExemptionActivity.this,AddApplicationActivity.class);
                intent1.putExtra("name",name);
                intent1.putExtra("studentNo",studentNo);
                intent1.putExtra("id",id);
                startActivity(intent1);
            }
        });

    }
    private void binding()
    {
        Layout_application = (LinearLayout) findViewById(R.id.Layout_application);
        apply_layout = (LinearLayout) findViewById(R.id.apply_layout);
        stuName = (TextView) findViewById(R.id.apply_stuName);
        stuNo = (TextView) findViewById(R.id.apply_stuNo);
        stuTime = (TextView) findViewById(R.id.apply_time);
        stuState = (TextView) findViewById(R.id.apply_state);
        apply_withdraw = (Button) findViewById(R.id.apply_withdraw);
    }
    public void onStart(){
        super.onStart();
        scanCodeSign();
    }
//添加或取消申请免试
    private void scanCodeSign1(String states){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("studentNo",studentNo)
                .add("exemprtionStatus",states)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.AddApplication)
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
                            String retStr = jsObj.getString("melodyClass");
                            if (retStr.equals("yes")){
                                scanCodeSign();
                                Layout_application.setVisibility(View.VISIBLE);
                                apply_layout.setVisibility(View.VISIBLE);
                                apply_withdraw.setVisibility(View.INVISIBLE);
                            }else{

                            }
                            Layout_application.setVisibility(View.VISIBLE);
                            apply_layout.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //statuTV.setText("签到异常");
                        }
                    }
                });
            }
        });
    }
    private void scanCodeSign(){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",id)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.ApplyForExemptionQueery)
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
                            apply_layout.setVisibility(View.VISIBLE);
                            Layout_application.setVisibility(View.GONE);
                            JSONArray jsArray = new JSONArray(retStr);
                            for (int i = 0 ; i < jsArray.length() ; i++){
                                JSONObject jsObj1 = jsArray.getJSONObject(i);
                                stuName.setText(jsObj1.getString("name"));
                                stuNo.setText(studentNo);
                                stuTime.setText(jsObj1.getString("time"));
                                String states = null;
                                if (jsObj1.getString("state").equals("1")){
                                    states = "成功";
                                }else if(jsObj1.getString("state").equals("2")){
                                    states = "否";
                                    Layout_application.setVisibility(View.VISIBLE);
                                }else if (jsObj1.getString("state").equals("3")){
                                    states = "申请中";
                                    apply_withdraw.setVisibility(View.VISIBLE);
                                }else if (jsObj1.getString("state").equals("4")){
                                    states = "拒绝";
                                }
                                stuState.setText(states);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            JSONObject jsObj = null;
                            try {
                                jsObj = new JSONObject(retStr);
                                String retStr = jsObj.getString("melodyClass");
                                Layout_application.setVisibility(View.VISIBLE);
                                apply_layout.setVisibility(View.GONE);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }


                            //statuTV.setText("签到异常");
                        }
                    }
                });
            }
        });
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
