package com.xinnuo.apple.nongda.TeacherActivity.WorkPlan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherAddWorkPlan extends BaseActivity {
    private EditText title1;
    private EditText createDate1;
    private EditText beginDate1;
    private EditText endDate1;
    private EditText workContent1;
    private EditText summary1;
    private CheckBox work_yes;
    private CheckBox work_no;
    private TextView workplan_submit;
    private OkHttpClient client;
    private String title;
    private String createDate;
    private String beginDate;
    private String endDate;
    private String workContent;
    private String state;
    private String summary;
    private String teacherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_work_plan);
        initOkHttp();
        binding();
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        createDate = formatter.format(curDate);
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        state = "0";
        createDate1.setText(createDate);
        workplan_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = title1.getText().toString();
                beginDate = beginDate1.getText().toString();
                endDate = endDate1.getText().toString();
                workContent = workContent1.getText().toString();
                summary = summary1.getText().toString();
                work_no.setChecked(true);
                Log.d("dawdaw","+++"+title+"++++++++++++++++++++++++++++");
                if (title.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddWorkPlan.this);
                    builder.setTitle("提示");
                    builder.setMessage("标题不能为空！");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                }else if (beginDate.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddWorkPlan.this);
                    builder.setTitle("提示");
                    builder.setMessage("开始时间不能为空！");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                }else if (endDate.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddWorkPlan.this);
                    builder.setTitle("提示");
                    builder.setMessage("结束时间不能为空！");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                }else if (workContent.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddWorkPlan.this);
                    builder.setTitle("提示");
                    builder.setMessage("计划不能为空！");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                }else if (summary.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddWorkPlan.this);
                    builder.setTitle("提示");
                    builder.setMessage("总结不能为空！");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                }else{
                    // 设置显示信息
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddWorkPlan.this);
                    builder.setMessage("是否提交").
                            // 设置确定按钮
                                    setPositiveButton("提交",
                                    new DialogInterface.OnClickListener() {
                                        // 单击事件
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            // 设置TextView文本
                                            //点击是的时候去进行提交
                                            // 单击事件
                                            requestWithUserId();


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


                    builder.show();

                }
            }
        });
    }

    //绑定控件
    private void binding()
    {
        title1 = (EditText) findViewById(R.id.add_work_title);
        createDate1 = (EditText) findViewById(R.id.add_work_createdate);
        beginDate1 = (EditText) findViewById(R.id.add_work_startdate);
        endDate1 = (EditText) findViewById(R.id.add_work_enddate);
        workContent1 = (EditText) findViewById(R.id.add_work_plan);
        summary1 = (EditText) findViewById(R.id.add_work_summary);
        work_no = (CheckBox) findViewById(R.id.add_workplan_no);
        work_yes = (CheckBox) findViewById(R.id.add_workplan_yes);
        workplan_submit = (TextView) findViewById(R.id.workplan_submit);

    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("title",title)
                .add("createDate",createDate)
                .add("beginDate",beginDate)
                .add("endDate",endDate)
                .add("workContent",workContent)
                .add("state",state)
                .add("summary",summary)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.AddWorkPlan)
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
        JSONArray jaArr = new JSONArray(jsonStr);
        for (int i = 0 ; i < jaArr.length() ; i++){
            JSONObject json = jaArr.getJSONObject(i);
            String updateYesOrNo = json.getString("state");
            if (updateYesOrNo.equals("增加成功"))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddWorkPlan.this);
                builder.setTitle("提示");
                builder.setMessage("增加成功！");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            // 单击事件
                            public void onClick(DialogInterface dialog, int which) {
                                // 设置TextView文本
                                //点击是的时候去进行提交
                                Intent intent = new Intent(TeacherAddWorkPlan.this,TeacherWorkPlan.class);
                                intent.putExtra("teacherId",teacherId);
                                startActivity(intent);
                                finish();
                            }
                        });
                builder.show();
            }else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddWorkPlan.this);
                builder.setTitle("提示");
                builder.setMessage("增加失败！");
                builder.setPositiveButton("确定", null);
                builder.show();
            }

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
