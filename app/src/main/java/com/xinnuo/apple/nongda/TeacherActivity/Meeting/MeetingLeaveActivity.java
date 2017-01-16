package com.xinnuo.apple.nongda.TeacherActivity.Meeting;
/**
 * 申请会议请假
 * */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xinnuo.apple.nongda.BaseActivity;
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

public class MeetingLeaveActivity extends BaseActivity {
    private OkHttpClient client;
    private Button leave_button;
    private EditText leave_text;
    private String leaveReason;
    private String id;
    private String teacherId;
    private String relTime1;
    private String meetType1;
    private String teacherinfoName1;
    private String theme1;
    private String meetingTime1;
    private String place1;
    private String introduction1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_leave);
        binding();
        initOkHttp();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        relTime1 = intent.getStringExtra("relTime");
        meetType1 = intent.getStringExtra("meetType");
        teacherinfoName1 = intent.getStringExtra("teacherinfoName");
        theme1 = intent.getStringExtra("theme");
        meetingTime1 = intent.getStringExtra("meetingTime");
        place1 = intent.getStringExtra("place");
        introduction1 = intent.getStringExtra("introduction");
        teacherId = intent.getStringExtra("teacherId");
        leave_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveReason = leave_text.getText().toString();
                Log.d("leaveReason=",leaveReason+"**********");
                if (leaveReason.length() < 15)
                {
                    AlertDialog.Builder builder  = new AlertDialog.Builder(MeetingLeaveActivity.this);
                    builder.setTitle("提示" ) ;
                    builder.setMessage("请假原因不得少于15个字！" ) ;
                    builder.setPositiveButton("确定" ,  null );
                    builder.show();
                }else
                {
                    // 设置显示信息
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MeetingLeaveActivity.this);

                    builder.setMessage("确定要提交？").
                            // 设置确定按钮
                                    setPositiveButton("是",
                                    new DialogInterface.OnClickListener() {
                                        // 单击事件
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            // 设置TextView文本
                                            //点击是的时候去进行提交
                                            requestWithUserId(teacherId,id,leaveReason);
                                        }
                                    }).
                            // 设置取消按钮
                                    setNegativeButton("否",
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

            }
        });

    }
    //绑定控件
    private void binding()
    {
        leave_button = (Button) findViewById(R.id.leave_button);
        leave_text = (EditText) findViewById(R.id.leave_text);
    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(String teacherId,String id,String leave){
        //菊花的开始方法
        mLoading.show();

        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",teacherId)
                .add("meetingId",id)
                .add("leaveReason",leave)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.MeetingLeave)
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
     * "leaveReason":"yes"
     * */


    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {


            JSONObject js = new JSONObject(jsonStr);
            if (js.getString("leaveReason").equals("yes")){
                AlertDialog.Builder builder  = new AlertDialog.Builder(MeetingLeaveActivity.this);
                builder.setTitle("提示" ) ;
                builder.setMessage("提交成功！" ) ;
                builder.setPositiveButton("确定" ,
                        new DialogInterface.OnClickListener() {
                            // 单击事件
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // 设置TextView文本
                                //点击是的时候去进行提交
                               Intent intent = new Intent(MeetingLeaveActivity.this,MeetingInfoDetailedActivity.class);
                                intent.putExtra("relTime",relTime1);
                                intent.putExtra("meetType",meetType1);
                                intent.putExtra("teacherinfoName",teacherinfoName1);
                                intent.putExtra("theme",theme1);
                                intent.putExtra("meetingTime",meetingTime1);
                                intent.putExtra("place",place1);
                                intent.putExtra("introduction",introduction1);
                                intent.putExtra("id",id);
                                intent.putExtra("teacherId",teacherId);
                               startActivity(intent);
                            }
                        });
                builder.show();
            }else
            {
                AlertDialog.Builder builder  = new AlertDialog.Builder(MeetingLeaveActivity.this);
                builder.setTitle("提示" ) ;
                builder.setMessage("提交失败！" ) ;
                builder.setPositiveButton("确定" ,
                        new DialogInterface.OnClickListener() {
                            // 单击事件
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // 设置TextView文本
                                //点击是的时候去进行提交
                            }
                        });
                builder.show();
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
}
