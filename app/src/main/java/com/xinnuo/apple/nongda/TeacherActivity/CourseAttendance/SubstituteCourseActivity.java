package com.xinnuo.apple.nongda.TeacherActivity.CourseAttendance;
/**
 * 教师代课
 * */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.Utils;
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

public class SubstituteCourseActivity extends BaseActivity {

    private TextView substitute_teacher;            //选择教师
    private TextView substitute_class;              //选择班级
    private TextView substitute_QRcode;             //生成二维码
    private TextView substitute_opening_positioning;//开启定位签到
    private TextView substitute_close_location;     //关闭定位签到
    private TextView substitute_course_starts;       //课程开始
    private TextView substitute_course_closes;       //课程结束
    private TextView substitute_refresh_position;    //刷新位置
    private LinearLayout substitute_layout_effect;    //展示刷新位置的
    private String coordinateId;
    private String sportclassId1;
    private boolean flag;
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
    private static double Longitude;
    private static double Latitude;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private static double distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitute_course);
        initOkHttp();
        binding();
        Intent intent = getIntent();
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
            substitute_teacher.setText("已选择:"+teacherName);
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
            substitute_class.setVisibility(View.VISIBLE);
        }
        if (sportClassName != null && classDate != null && startTime!= null && sportclassId != null)
        {

            substitute_class.setText("已选择:"+sportClassName+"\t"+classDate+"\t"+startTime);
            substitute_class.setVisibility(View.VISIBLE);
            substitute_close_location.setVisibility(View.VISIBLE);
            substitute_opening_positioning.setVisibility(View.VISIBLE);
            substitute_QRcode.setVisibility(View.VISIBLE);
            substitute_course_starts.setVisibility(View.VISIBLE);
            substitute_course_closes.setVisibility(View.VISIBLE);
        }
    }

    //绑定控件
    private void binding()
    {
        substitute_teacher = (TextView) findViewById(R.id.substitute_teacher);
        substitute_class = (TextView) findViewById(R.id.substitute_class);
        substitute_QRcode = (TextView) findViewById(R.id.substitute_QRcode);
        substitute_opening_positioning = (TextView) findViewById(R.id.substitute_opening_positioning);
        substitute_close_location = (TextView) findViewById(R.id.substitute_close_location);
        substitute_course_starts = (TextView) findViewById(R.id.substitute_course_starts);
        substitute_course_closes = (TextView) findViewById(R.id.substitute_course_closes);
        substitute_refresh_position = (TextView) findViewById(R.id.substitute_refresh_position);
        substitute_layout_effect = (LinearLayout) findViewById(R.id.substitute_layout_effect);
    }

    //控件点击事件
    private void clickJump()
    {
        //选择教师
        substitute_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubstituteCourseActivity.this,AllTeachersActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("state","1");
                startActivity(intent);
                finish();

            }
        });
        //选择班级
        substitute_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubstituteCourseActivity.this,ClassListActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("teacherId1",teacherId1);
                intent.putExtra("state","1");
                intent.putExtra("itemId",itemId);
                Log.d("teacherId = ",teacherId+"******"+teacherId1);
                startActivity(intent);

            }
        });
        //生成二维码
        substitute_QRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubstituteCourseActivity.this,TeacherMakeQRActivity.class);
                intent.putExtra("teacherId",teacherId1);
                intent.putExtra("sportClassId",sportclassId);
                intent.putExtra("itemId",itemId);
                startActivity(intent);
            }
        });
        //开启定位签到
        substitute_opening_positioning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("teacherId = ",teacherId);
                String open = "1";
                startAndClose(open);

            }
        });
        //关闭定位签到
        substitute_close_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coordinateId.equals("null") || coordinateId == "")
                {
                    AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                    builder.setTitle("提示" ) ;
                    builder.setMessage("定位失败请重新定位！" ) ;
                    builder.setPositiveButton("确定" ,  null );
                    builder.show();
                }else {
                    String close = "2";
                    startAndClose(close);
                }
            }
        });
        //课程开始
        substitute_course_starts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signRequest();
            }
        });
        //课程结束
        substitute_course_closes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endOfCourse();
                onDestroy();
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
                                    initLocation();
                                    startLocation();

                                    mLoading.show();
                                    substitute_layout_effect.setVisibility(View.VISIBLE);
                                    substitute_refresh_position.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startLocation();

                                        }
                                    });
                                }else
                                {

                                    substitute_layout_effect.setVisibility(View.GONE);
                                    stopLocation();
                                    closeLocation();

                                }
                            }else
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
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
    //存入教师坐标
    private void startPositioning(final String content){

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


                            AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                            builder.setTitle("提示" ) ;
                            builder.setMessage(content) ;
                            String ret = ""+Longitude + Latitude;
                            Log.d("ret = ",ret);
                            builder.setPositiveButton("确定" ,  null );
                            builder.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //statuTV.setText("签到异常");
                        }


                    }
                });
            }
        });
    }
    //查询教师状态
    private void signRequest(){
        initOkHttp();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("sportClassId",sportclassId)
                .add("teacherId",teacherId)
                .add("HaveAclassState","0")
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherCourseStart)
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

                            if (retStr.equals("yes")) {
                                startAttendance();
                            }
                            else if (retStr.equals("BeASubstituteInThe"))
                            {
                                //statuTV.setText("不是签到时间");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("您的课程被代课中！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else if (retStr.equals("NotClickOnTheClass"))
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("请至少一名学生签到成功后开启上课！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else if (retStr.equals("Error"))
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("开启失败，数据异常1！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();

                            }else
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("开启失败，数据异常2！" ) ;
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
    //开始上课录入接口
    private void startAttendance(){
        initOkHttp();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherid",teacherId)
                .add("HaveAclassState","1")
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherCourseStart)
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
                            JSONObject jsObj = new JSONObject("retStr");
                            String melodyClass = jsObj.getString("melodyClass");

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
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("定位关闭成功！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
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
    //下课请求
    private void endOfCourse(){
        initOkHttp();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherId",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherEndOfCourse)
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

                            if (retStr.equals("yes")) {
                                //statuTV.setText("不是签到时间");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("成功，本节课结束！" ) ;
                                builder.setPositiveButton("确定" ,  new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        SharedPreferences sp = getSharedPreferences("userinfo",
                                                Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();

                                        editor.putBoolean("pswd_checkBox", false);
                                        editor.commit();

                                        //进行页面跳转，跳回登录界面
                                        Intent intent = new Intent(SubstituteCourseActivity.this, Class_ListActivity.class);
                                        intent.putExtra("itemId",itemId);
                                        intent.putExtra("teacherId",teacherId);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                builder.show();
                            }
                            else if (retStr.equals("no"))
                            {
                                //statuTV.setText("不是签到时间");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("失败，上课时间不足，请稍后再试！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();

                            }else if (retStr.equals("Error"))
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("失败，未查询到上课时间，请开启课程，满足上课时间后再试！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else if (retStr.equals("Error"))
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("开启失败，数据异常1！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(SubstituteCourseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("开启失败，数据异常2！" ) ;
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
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }
    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
    private static double rad(double d) {
        return d * Math.PI / 180.0;
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
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数

        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }


    /**
     * 开始定位
     * @since 2.8.0
     * @author hongming.wang
     */
    private void startLocation(){

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }
    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        return mOption;

    }
    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                String result = Utils.getLocationStr(loc);
                /**
                 * sb.append("经    度    : " + location.getLongitude() + "\n");
                 sb.append("纬    度    : " + location.getLatitude() + "\n");
                 */
                if (Latitude == 0 && Latitude == 0) {
                    Longitude = loc.getLongitude();
                    Latitude = loc.getLatitude();
                    final String coutent = "教师已上传定位坐标，可以定位签到了";
                    startPositioning(coutent);

                }else {
                    double signLongitude  = loc.getLongitude();
                    double signLatitude  = loc.getLatitude();
                    distance = DistanceOfTwoPoints(Latitude,Longitude,signLatitude,signLongitude);
                    if (distance > 50)
                    {
                        flag = false;
                        stopLocation();
                    }else {
                        flag = true;
                    }
                }

                String ret = ""+Longitude + Latitude;
                String jStr = Longitude+",";
                String wStr = Latitude+"";
                Log.d("定位结果：long = ",Longitude+"lat = "+Latitude);
//                statuTV.setText(jStr+wStr);
//                test3TV.setText("定位结果：long = "+Longitude+"lat = "+Latitude);



            } else {
                //statuTV.setText("定位失败，loc is null");
            }
        }
    };
    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 距离：单位为米
     */
    public static double DistanceOfTwoPoints(double lat1,double lng1,
                                             double lat2,double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
