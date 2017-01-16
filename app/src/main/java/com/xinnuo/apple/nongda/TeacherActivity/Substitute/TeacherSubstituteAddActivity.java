package com.xinnuo.apple.nongda.TeacherActivity.Substitute;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

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

public class TeacherSubstituteAddActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener {
    private OkHttpClient client;
    private RadioGroup substitute_add;
    private EditText substitute_reason;
    private TextView substitute_teacher;
    private Button substitute_button;
    private String teacherId;       //教师ID
    private String subTeacherId;    //代课教师Id
    private String reason;          //请假原因
    private String typeId;          //状态（1代课、2合课）
    private String name;            //代课教师姓名
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_substitute_add);
        binding();
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        Log.d("teacherId =",teacherId);
        if (intent.getStringExtra("subTeacherId") == null || intent.getStringExtra("subTeacherId").equals("null")){
            subTeacherId = null;
        }else
        {
            subTeacherId = intent.getStringExtra("subTeacherId");
            Log.d("subTeacherId =",subTeacherId);
        }
        if (intent.getStringExtra("name") == null || intent.getStringExtra("name").equals("null")){
            name = null;
        }else
        {
            name = intent.getStringExtra("name");
            Log.d("name =",name);
            substitute_teacher.setText("代课教师:"+name);
        }
        initOkHttp();



            substitute_button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    reason = substitute_reason.getText().toString();
                    if (reason == "")
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherSubstituteAddActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("原因不能为空！");
                        builder.setPositiveButton("确定", null);
                        builder.show();
                    }else if (typeId == "")
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherSubstituteAddActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("请选择代课或者合课！");
                        builder.setPositiveButton("确定", null);
                        builder.show();
                    }else if (subTeacherId == "")
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherSubstituteAddActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("代课教师不能为空！");
                        builder.setPositiveButton("确定", null);
                        builder.show();
                    }else {
                        // 设置显示信息
                        final AlertDialog.Builder builder = new AlertDialog.Builder(TeacherSubstituteAddActivity.this);

                        builder.setMessage("确定要提交？").
                                // 设置确定按钮
                                        setPositiveButton("是",
                                        new DialogInterface.OnClickListener() {
                                            // 单击事件
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                // 设置TextView文本
                                                //点击是的时候去进行提交
                                                requestWithUserId();
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

        substitute_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        substitute_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(TeacherSubstituteAddActivity.this,QueryAllTheeachersActivity.class);
                intent1.putExtra("teacherId",teacherId);
                startActivity(intent1);
                finish();
            }
        });
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.substitute_for :
                typeId = "1";
                Log.d("typeId = ",typeId);
                break;
            case R.id.class_application :
                typeId = "2";
                Log.d("typeId = ",typeId);
                break;
            default :
                break;
        }

    }

    //绑定控件
    private void binding()
    {
        substitute_add = (RadioGroup) findViewById(R.id.substitute_add);
        substitute_reason = (EditText) findViewById(R.id.substitute_reason);
        substitute_teacher = (TextView) findViewById(R.id.substitute_teacher);
        substitute_button = (Button) findViewById(R.id.substitute_button);
        substitute_add.setOnCheckedChangeListener(this);
    }

    /**
     * 请求方法
     * */
    private void requestWithUserId(){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherId",teacherId)
                .add("subTeacherId",subTeacherId)
                .add("reason",reason)
                .add("typeId",typeId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherAddSubstitute)
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
     *
     *
     * */


    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {
            JSONObject js = new JSONObject(jsonStr);
        if (js.getString("melodyClass").equals("yes"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherSubstituteAddActivity.this);
            builder.setTitle("提示");
            builder.setMessage("提交申请成功！");
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                // 单击事件
                public void onClick(DialogInterface dialog, int which) {
                    // 设置TextView文本
                    //点击是的时候去进行提交
                    Intent intent = new Intent(TeacherSubstituteAddActivity.this,SubstituteActivity.class);
                    intent.putExtra("teacherId",teacherId);
                    startActivity(intent);
                }
                });
            builder.show();
        }else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherSubstituteAddActivity.this);
            builder.setTitle("提示");
            builder.setMessage("提交失败，请重新提交！");
            builder.setPositiveButton("确定", null);
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
