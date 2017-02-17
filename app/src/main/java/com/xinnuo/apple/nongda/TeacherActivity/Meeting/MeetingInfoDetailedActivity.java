package com.xinnuo.apple.nongda.TeacherActivity.Meeting;
/**
 * 详细的会议展示 可以进行签到 查询会议名单 请假
 * */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.Utils;
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

public class MeetingInfoDetailedActivity extends BaseActivity {
    private OkHttpClient client;
    private TextView leave;
    private TextView sign;
    private TextView conferencelist;
    private TextView relTime;
    private TextView meetType;
    private TextView teacherinfoName;
    private TextView theme;
    private TextView meetingTime;
    private TextView place;
    private TextView introduction;
    private String id;
    private String teacherId;
    private String relTime1;
    private String meetType1;
    private String teacherinfoName1;
    private String theme1;
    private String meetingTime1;
    private String place1;
    private String introduction1;
    private static double signLongitude;
    private static double signLatitude;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private static final double ALERT_DISTANCE = 50;
    private static double distance;
    private static double Longitude;
    private static double Latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info_detailed);
        Binding();
        Intent intent = getIntent();
        relTime.setText(intent.getStringExtra("relTime"));
        relTime1 = intent.getStringExtra("relTime");
        meetType1 = intent.getStringExtra("meetType");
        teacherinfoName1 = intent.getStringExtra("teacherinfoName");
        theme1 = intent.getStringExtra("theme");
        meetingTime1 = intent.getStringExtra("meetingTime");
        place1 = intent.getStringExtra("place");
        introduction1 = intent.getStringExtra("introduction");
        String meetTypes;
        if (intent.getStringExtra("meetType").equals("0"))
        {
            meetTypes = "签到未开始";
        }else if (intent.getStringExtra("meetType").equals("1"))
        {
            meetTypes = "签到进行中，请准备签到";
        }else
        {
            meetTypes = "签到已结束";
        }
        meetType.setText(meetTypes);
        teacherinfoName.setText(intent.getStringExtra("teacherinfoName"));
        theme.setText(intent.getStringExtra("theme"));
        meetingTime.setText(intent.getStringExtra("meetingTime"));
        place.setText(intent.getStringExtra("place"));
        introduction.setText(intent.getStringExtra("introduction"));
        id = intent.getStringExtra("id");
        Log.d("id======",id);
        teacherId = intent.getStringExtra("teacherId");
        initLocation();
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("meetingId", id)
                .add("teacherId",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeachersGetCoordinateMeeting)
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
                        mLoading.dismiss();

                        Log.d("网络请求返回值",retStr);

                        try {

                            JSONArray js = new JSONArray(retStr);
                            for (int i = 0; i < js.length(); i++) {
                                boolean flag = true;
                                JSONObject jsObj = js.getJSONObject(i);

                                signLongitude = jsObj.optDouble("longitude");
                                signLatitude = jsObj.optDouble("latitude");
                               Log.d("公寓坐标:long = ",signLongitude+"lat = "+signLatitude);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
        Jump();
    }
    //绑定控件方法
    private void Binding()
    {
        leave = (TextView) findViewById(R.id.meet_leave);
        sign= (TextView) findViewById(R.id.meet_sign);
        conferencelist = (TextView) findViewById(R.id.meet_conferencelist);
        relTime = (TextView) findViewById(R.id.teacher_meeting_info1);
        meetType = (TextView) findViewById(R.id.teacher_meeting_info2);
        teacherinfoName = (TextView) findViewById(R.id.teacher_meeting_info3);
        theme = (TextView) findViewById(R.id.teacher_meeting_info4);
        meetingTime = (TextView) findViewById(R.id.teacher_meeting_info5);
        place = (TextView) findViewById(R.id.teacher_meeting_info6);
        introduction = (TextView) findViewById(R.id.teacher_meeting_info7);
    }
    /**
     * 跳转方法
     * */
    private void Jump(){
        conferencelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetingInfoDetailedActivity.this,ConferenceListActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetingInfoDetailedActivity.this,MeetingLeaveActivity.class);
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
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startLocation();


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
     *
     * @since 2.8.0
     * @author hongming.wang
     *
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
                Double Longitude =  loc.getLongitude();
                Double Latitude =  loc.getLatitude();

                String ret = ""+Longitude + Latitude;
                String jStr = Longitude+",";
                String wStr = Latitude+"";
                Log.d("定位结果：long = ",Longitude+"lat = "+Latitude);
//                statuTV.setText(jStr+wStr);
//                test3TV.setText("定位结果：long = "+Longitude+"lat = "+Latitude);

                distance = DistanceOfTwoPoints(Latitude,Longitude,signLatitude,signLongitude);
                if (distance < 50 && distance > 0) {
                    signRequest();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MeetingInfoDetailedActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("不是签到位置！");
                    builder.setPositiveButton("确定", null);
                    builder.show();
                    stopLocation();
                }

            } else {
                //statuTV.setText("定位失败，loc is null");
            }
        }
    };
    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     *
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
    private void signRequest(){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("meetingId", id)
                .add("id",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherSign)
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
                                AlertDialog.Builder builder  = new AlertDialog.Builder(MeetingInfoDetailedActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("签到成功！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();

                            }
                            else if (retStr.equals("no"))
                            {
                                //statuTV.setText("不是签到时间");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(MeetingInfoDetailedActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("不是签到时间！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();

                            } else{
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(MeetingInfoDetailedActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("服务器异常！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();

                            }
                            stopLocation();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //statuTV.setText("签到异常");

                            stopLocation();

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

}
