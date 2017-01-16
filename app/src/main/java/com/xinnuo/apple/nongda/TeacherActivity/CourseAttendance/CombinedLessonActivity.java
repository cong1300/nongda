package com.xinnuo.apple.nongda.TeacherActivity.CourseAttendance;
/**
 * 合课界面
 * */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class CombinedLessonActivity extends BaseActivity {
    private TextView lesson_teacher;            //选择教师
    private TextView lesson_class;              //选择班级
    private TextView lesson_QRcode;             //生成二维码
    private TextView lesson_opening_positioning;//开启定位签到
    private TextView lesson_close_location;     //关闭定位签到
    private String coordinateId;
    private String sportclassId1;
    private String teacherId;
    private String teacherName;                     //被代课教师姓名
    private String teacherId1;                      //被代课教师Id
    private String substituteId;                    //代课记录Id
    private String itemId;                           //课程Id
    private String sportclassId;                    //班级Id
    private String startTime;                       //课节
    private String classDate;                       //周几
    private String sportClassName;                  //课程名称
    private OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combined_lesson);
        initOkHttp();
        binding();
        Intent intent = getIntent();
        coordinateId = intent.getStringExtra("coordinateId");
        teacherId = intent.getStringExtra("teacherId");
        sportclassId1 = intent.getStringExtra("sportclassId1");
        if (intent.getStringExtra("teacherId1") == null || intent.getStringExtra("teacherId1").equals("null")){
            teacherId1 = "null";
        }else {
            teacherId1 = intent.getStringExtra("teacherId1");
        }
        if (intent.getStringExtra("teacherName") == null || intent.getStringExtra("teacherName").equals("null"))
        {
            teacherName = null;
        }else
        {
            teacherName = intent.getStringExtra("teacherName");
            lesson_teacher.setText("已选择:"+teacherName);
        }
        if (intent.getStringExtra("substituteId") == null || intent.getStringExtra("substituteId").equals("null"))
        {
            substituteId = null;
        }else
        {
            substituteId = intent.getStringExtra("substituteId");
        }
        if (intent.getStringExtra("itemId") == null || intent.getStringExtra("itemId").equals("null"))
        {
            itemId = null;
        }else
        {
            itemId = intent.getStringExtra("itemId");
        }
        if (intent.getStringExtra("sportclassId") == null || intent.getStringExtra("sportclassId").equals("null"))
        {
            sportclassId = null;
        }else
        {
            sportclassId = intent.getStringExtra("sportclassId");
        }
        if (intent.getStringExtra("startTime") == null || intent.getStringExtra("startTime").equals("null"))
        {
            startTime = null;
        }else
        {
            startTime = intent.getStringExtra("startTime");
        }
        if (intent.getStringExtra("classDate") == null || intent.getStringExtra("classDate").equals("null"))
        {
            classDate = null;
        }else
        {
            classDate = intent.getStringExtra("classDate");
        }
        if (intent.getStringExtra("sportClassName") == null || intent.getStringExtra("sportClassName").equals("null"))
        {
            sportClassName = null;
        }else
        {
            sportClassName = intent.getStringExtra("sportClassName");
        }
        clickJump();
        if (teacherId1 != null && itemId != null && substituteId != null){
            lesson_class.setVisibility(View.VISIBLE);
        }
        if (sportClassName != null && classDate != null && startTime!= null && sportclassId != null)
        {
            lesson_class.setText("已选择:"+sportClassName+"\t"+classDate+"\t"+startTime);
            lesson_class.setVisibility(View.VISIBLE);
            lesson_close_location.setVisibility(View.VISIBLE);
            lesson_opening_positioning.setVisibility(View.VISIBLE);
            lesson_QRcode.setVisibility(View.VISIBLE);
        }
    }

    //绑定控件
    private void binding()
    {
        lesson_teacher = (TextView) findViewById(R.id.lesson_teacher);
        lesson_class = (TextView) findViewById(R.id.lesson_class);
        lesson_QRcode = (TextView) findViewById(R.id.lesson_QRcode);
        lesson_opening_positioning = (TextView) findViewById(R.id.lesson_opening_positioning);
        lesson_close_location = (TextView) findViewById(R.id.lesson_close_location);
    }

    //控件点击事件
    private void clickJump()
    {
        //选择教师
        lesson_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CombinedLessonActivity.this,AllTeachersActivity.class);
                intent.putExtra("coordinateId",coordinateId);
                intent.putExtra("teacherId",teacherId);
                startActivity(intent);
                finish();

            }
        });
        //选择班级
        lesson_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CombinedLessonActivity.this,ClassListActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("teacherId1",teacherId1);
                intent.putExtra("itemId",itemId);
                intent.putExtra("state","0");
                intent.putExtra("coordinateId",coordinateId);
                Log.d("teacherId = ",teacherId+"******"+teacherId1);
                startActivity(intent);

            }
        });
        //生成二维码
        lesson_QRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CombinedLessonActivity.this,TeacherMakeQRActivity.class);
                intent.putExtra("teacherId",teacherId1);
                intent.putExtra("sportClassId",sportclassId);
                intent.putExtra("itemId",itemId);
                startActivity(intent);
            }
        });
        //开启定位签到
        lesson_opening_positioning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("teacherId = ",teacherId);
                String open = "1";
                startAndClose(open);

            }
        });
        //关闭定位签到
        lesson_close_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String close = "2";
                startAndClose(close);
            }
        });
    }

    //查询定位坐标并上传
    private void signRequest(){

        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherId",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.QueryTeacherCoordinates)
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
                        Log.d("teacherId = ",teacherId);
                        try {
                            JSONObject jsObj = new JSONObject(retStr);
                            String weiDu = jsObj.getString("weiDu");
                            String jingDu = jsObj.getString("jingDu");
                            startPositioning(jingDu,weiDu);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //statuTV.setText("签到异常");
                        }


                    }
                });
            }
        });
    }
    //存入教师坐标
    private void startPositioning(String Longitude,String Latitude){

        initOkHttp();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherId",teacherId)
                .add("longitude",Longitude+"")
                .add("latitude",Latitude+"")
                .add("classId",sportclassId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherStartPositioning)
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
                        mLoading.dismiss();
                        try {
                            JSONObject jsObj = new JSONObject(retStr);
                            coordinateId = jsObj.getString("id");

                            if (coordinateId.equals("null") || coordinateId == "")
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(CombinedLessonActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("定位失败请重新定位！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(CombinedLessonActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("教师已经上传坐标，学生可以开始定位签到了！") ;
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
    //开启或关闭签到通道
    private void startAndClose(final String method)
    {
        initOkHttp();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherid",teacherId)
                .add("theKey","0")
                .add("status",method)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.UpdateTheQrCodeKey)
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
                            if (jsObj.getString("melodyClass").equals("yes"))
                            {
                                if (method.equals("1"))
                                {
                                    signRequest();
                                }else
                                {

                                    closeLocation();
                                }
                            }else
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(CombinedLessonActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("定位关闭失败") ;
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

    //关闭定位
    private void closeLocation(){
        initOkHttp();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",coordinateId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherCloseLocation)
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
                            if (jsObj.getString("Error").equals("yes"))
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(CombinedLessonActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("定位关闭成功！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(CombinedLessonActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("定位关闭失败") ;
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
