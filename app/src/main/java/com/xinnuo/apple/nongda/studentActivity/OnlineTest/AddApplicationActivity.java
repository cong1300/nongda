package com.xinnuo.apple.nongda.studentActivity.OnlineTest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

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

public class AddApplicationActivity extends AppCompatActivity {
    private TextView addStuName;
    private TextView addStuNo;
    private Button addSubmite;
    private String stuNo;
    private String stuName;
    private String id;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_application);
        Intent intent = getIntent();
        stuNo = intent.getStringExtra("studentNo");
        stuName = intent.getStringExtra("name");
        id = intent.getStringExtra("id");
        addStuName = (TextView) findViewById(R.id.add_stuName);
        addStuNo = (TextView) findViewById(R.id.add_stuNo);
        addSubmite = (Button) findViewById(R.id.add_submit);
        addStuName.setText(stuName);
        addStuNo.setText(stuNo);
        initOkHttp();
        addSubmite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddApplicationActivity.this);

                builder.setMessage("是否确认取消申请！").
                        // 设置确定按钮
                                setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        scanCodeSign1();
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
    }
    //添加或取消申请免试
    private void scanCodeSign1(){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("studentNo",stuNo)
                .add("exemprtionStatus","3")
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
                                Intent intent = new Intent(AddApplicationActivity.this,ApplyForExemptionActivity.class);
                                intent.putExtra("id",id);
                                intent.putExtra("name",stuName);
                                intent.putExtra("stuNo",stuNo);
                                startActivity(intent);
                                finish();
                            }else{
                                AlertDialog.Builder builder  = new AlertDialog.Builder(AddApplicationActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("提交失败请重新提交！" ) ;
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
