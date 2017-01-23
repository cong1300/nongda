package com.xinnuo.apple.nongda.TeacherActivity.CourseAttendance;
/**
 * 签到主界面 二维码 定位 上课 下课 合课
 * */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xinnuo.apple.nongda.Anticlockwise;
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

public class ClassListDetailsActivity extends BaseActivity {
    private TextView scan_code_sign;                //扫码签到
    private TextView opening_positioning;          //开启定位
    private TextView close_location;                //关闭定位
    private TextView Attendance_list;               //考勤列表
    private TextView combined_lesson;               //合课
    private TextView course_start;                  //课程开始
    private TextView end_of_course;                 //课程结束
    private TextView refresh_position;              //刷新位置

    private OkHttpClient client;
    private String sportclassId;
    private static double Longitude;
    private static double Latitude;
    private String teacherId;
    private Anticlockwise mTimer;
    private LinearLayout layouts;
    private String classState;
    private JSONObject datrr;

    private String itemId;
    private static double distance;
    private String coordinateId;        //坐标id
    private LinearLayout layout;
    private boolean flag;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list_details);
        binding();
        initOkHttp();
        Intent intent = getIntent();
        sportclassId = intent.getStringExtra("sportclassId");
        Log.d("sportclassId = ",sportclassId);
        teacherId = intent.getStringExtra("teacherId");
        itemId = intent.getStringExtra("itemId");
        //初始化时间

        clickJump();

    }
    protected void onStart()
    {
        super.onStart();
    }

    //绑定控件
    private void binding()
    {
        scan_code_sign = (TextView) findViewById(R.id.scan_code_sign);
        opening_positioning = (TextView) findViewById(R.id.opening_positioning);
        close_location = (TextView) findViewById(R.id.close_location);
        Attendance_list = (TextView) findViewById(R.id.Attendance_list);
        combined_lesson = (TextView) findViewById(R.id.combined_lesson);
        course_start = (TextView) findViewById(R.id.course_start);
        end_of_course = (TextView) findViewById(R.id.end_of_course);
        refresh_position = (TextView) findViewById(R.id.refresh_position);
        layout = (LinearLayout) findViewById(R.id.layout_effect);
        mTimer = (Anticlockwise) findViewById(R.id.id_timer);
    }

    //点击事件
    private void clickJump()
    {
        //扫码签到
        scan_code_sign.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassListDetailsActivity.this,TeacherMakeQRActivity.class);
                intent.putExtra("teacherId",teacherId);
                intent.putExtra("sportClassId",sportclassId);
                intent.putExtra("itemId",itemId);
                startActivity(intent);
            }
        });
        //开启定位
        opening_positioning.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String open = "1";
                startAndClose(open);
                Log.d("********","走的这里"+Latitude+"Latitude"+Latitude);
            }
        });
        //关闭定位
        close_location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (coordinateId.equals("null") || coordinateId == "")
                {
                    AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
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
        //考勤列表
        Attendance_list.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassListDetailsActivity.this,TeacherAttendanceListActivity.class);
                intent.putExtra("sportclassId",sportclassId);
                startActivity(intent);
            }
        });
        //合课
        combined_lesson.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (Latitude == 0 && Latitude == 0)
                {
                    //statuTV.setText("不是签到时间");
                    AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                    builder.setTitle("提示" ) ;
                    builder.setMessage("请先开启定位！" ) ;
                    builder.setPositiveButton("确定" ,  null );
                    builder.show();
                }else
                {
                    Intent intent = new Intent(ClassListDetailsActivity.this,CombinedLessonActivity.class);
                    intent.putExtra("coordinateId",coordinateId);
                    intent.putExtra("sportclassId1",sportclassId);
                    intent.putExtra("teacherId",teacherId);
                    startActivity(intent);
                }
            }
        });
        //课程开始
        course_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                    if (distance > 50){
                        mTimer.reStart();
                    }
                    signRequest();

            }
        });
        //课程结束
        end_of_course.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                endOfCourse();

            }
        });
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

                if (Latitude == 0.0 && Longitude == 0.0) {

                    Longitude = loc.getLongitude();
                    Latitude = loc.getLatitude();
                    final String coutent = "教师已上传定位坐标，可以定位签到了";
                    startPositioning(coutent);
                }else {
                    mLoading.dismiss();
                    double signLongitude  = loc.getLongitude();
                    double signLatitude  = loc.getLatitude();
                    distance = DistanceOfTwoPoints(Latitude,Longitude,signLatitude,signLongitude);
                    if (distance > 50)
                    {

                        mTimer.setOnTimeCompleteListener(new Anticlockwise.OnTimeCompleteListener()
                        {
                            @Override
                            public void onTimeComplete()
                            {
                                int num = 0;
                                startAttendance(num);
                            }
                        });

                        stopLocation();
                    }else {
                        mTimer.stop();
                        mTimer.initTime(10,0);
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
                mLoading.dismiss();
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
    //查询教师状态
    private void signRequest(){
        initOkHttp();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("sportsClassId",sportclassId)
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
                                int num = 1;
                                startAttendance(num);
                            }
                            else if (retStr.equals("BeASubstituteInThe"))
                            {
                                //statuTV.setText("不是签到时间");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("您的课程被代课中！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else if (retStr.equals("NotClickOnTheClass"))
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("请至少一名学生签到成功后开启上课！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else if (retStr.equals("Error"))
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("开启失败，数据异常1！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();

                            }else
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
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
    private void startAttendance( int number){
        initOkHttp();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherid",teacherId)
                .add("HaveAclassState","1")
                .add("attendState",number+"")
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherStartAttendance)
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
                            if (jsObj.getString("melodyClass").equals("离职"))
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("上传考勤记录！") ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("开始上课！") ;
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
                                    layout.setVisibility(View.VISIBLE);
                                    refresh_position.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startLocation();

                                        }
                                    });
                                }else
                                {

                                        layout.setVisibility(View.GONE);
                                    if (Latitude == 0.0){
                                        closeLocation();
                                    }else {
                                        stopLocation();
                                        destroyLocation();
                                        closeLocation();
                                    }



                                }
                            }else
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
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
                Log.d("请求失败 teache",teacherId+"classId = "+sportclassId+"99999999999999999999999999999999999999999999");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String retStr = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoading.dismiss();
                        try {
                            JSONObject jsObj = new JSONObject(retStr);
                            coordinateId = jsObj.getString("id");


                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
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
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("定位关闭成功！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
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
                            datrr = new JSONObject(retStr);
                            classState = jsObj.getString("melodyClass");
                            if (classState.equals("yes")) {
                                //statuTV.setText("不是签到时间");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("成功，本节课结束！" ) ;
                                builder.setPositiveButton("确定" ,  new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which) {

                                        //进行页面跳转，跳回界面
                                        Intent intent = new Intent(ClassListDetailsActivity.this, Class_ListActivity.class);
                                        intent.putExtra("itemId",itemId);
                                        intent.putExtra("teacherId",teacherId);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                builder.show();
                            }
                            else if (classState.equals("noTime"))
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("失败，上课时间不足，请稍后再试！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();

                            }else if (classState.equals("no"))
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("失败，未查询到上课！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else if (classState.equals("Error"))
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("开启失败，数据异常1！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else
                            {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ClassListDetailsActivity.this);
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


}
