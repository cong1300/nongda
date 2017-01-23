package com.xinnuo.apple.nongda.studentActivity.ClassAttendance;
/**
 * 学生考勤
 * */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.Utils;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.zxing.android.CaptureActivity;

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


public class StuClassAttendanceActivity extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private String studentId;
    private double signLongitude;
    private double signLatitude;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private static final int REQUEST_CODE_SCAN = 0x0000;
    private String teacherId;
    private double distance;
    private double Longitude;
    private double Latitude;
    private String number;
    private String studentNo;
    private String sportClassId;
    private String classDate;
    private String state;
    private String strings;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_class_attendance);
        initOkHttp();
        initLocation();
        listView = (ListView) findViewById(R.id.list_stu_class_attendance);
        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        studentNo = intent.getStringExtra("studentNo");
        Log.d("studentId = ",studentId);

        RequestBody requestBodyPost = new FormBody.Builder()
                .add("studentId",studentId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.classListUrl)
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
                            JSONArray jsArr = new JSONArray(retStr);
                            List<String> data = new ArrayList<String>();
                            ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
                            for (int i = 0; i < jsArr.length(); i++) {
                                JSONObject jss = jsArr.getJSONObject(i);
                                teacherId = jss.getString("id");
                                Log.d("teacherId = ", teacherId);
                                String className = jss.optString("sportClassName")+"\t"+jss.optString("classDate");
                                String name = jss.getString("name");
                                number = jss.getString("number");
                                sportClassId = jss.getString("sportClassId");
                                classDate = jss.optString("classDate");
                                strings = teacherId+number+sportClassId;
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("className",className);
                                map.put("name",name);
                                listItem.add(map);

                            }

                            if (jsArr.length() == 0)
                            {
                                midToast("无数据",3);
                            }
                            SimpleAdapter qrAdapter = new SimpleAdapter(StuClassAttendanceActivity.this,listItem,R.layout.item_stu_class_attendance,
                                    new  String[]{"className","name"},
                                    new int[]{R.id.attendance_curriculum,R.id.attendance_teacherName});
                            listView.setAdapter(qrAdapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final String[] items = new String[]{"扫码签到","扫码签退","定位签到","定位签退","取消"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                builder.setTitle("签到方式");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Toast.makeText(StuClassAttendanceActivity.this, "您选中了："+items[0], Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(StuClassAttendanceActivity.this, CaptureActivity.class);
                                startActivityForResult(intent1, REQUEST_CODE_SCAN);
                                state = "1";
                                break;
                            case 1:
                                Toast.makeText(StuClassAttendanceActivity.this, "您选中了："+items[1], Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(StuClassAttendanceActivity.this, CaptureActivity.class);
                                startActivityForResult(intent2, REQUEST_CODE_SCAN);
                                state = "2";
                                break;
                            case 2:
                                state = "1";
                                startLocation();
                                requestWithUserId(teacherId);
                                Toast.makeText(StuClassAttendanceActivity.this, "您选中了："+items[2], Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                state = "2";
                                startLocation();
                                Toast.makeText(StuClassAttendanceActivity.this, "您选中了："+items[3], Toast.LENGTH_SHORT).show();


                                break;
                            case 4:
                                Toast.makeText(StuClassAttendanceActivity.this, "您选中了："+items[4], Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).show();
            }
        });

    }
    //
    private void scanCodeSign(){
        RequestBody requestBodyPost = new FormBody.Builder()
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
                            AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                            builder.setTitle("提示" ) ;
                            builder.setMessage("签到成功！" ) ;
                            builder.setPositiveButton("确定" ,  null );
                            builder.show();

                        }
                        else if (retStr.equals("no"))
                        {
                            //statuTV.setText("不是签到时间");
                            AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                            builder.setTitle("提示" ) ;
                            builder.setMessage("不是签到时间！" ) ;
                            builder.setPositiveButton("确定" ,  null );
                            builder.show();

                        } else{
                            //statuTV.setText("服务器异常");
                            AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                            builder.setTitle("提示" ) ;
                            builder.setMessage("服务器异常！" ) ;
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

    protected void onStart()
    {
        super.onStart();

    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(String id){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherId",teacherId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.QueryTeacherToordinates)
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
    //json解析方法

    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {
            JSONObject jsObj = new JSONObject(jsonStr);
            signLongitude = jsObj.optDouble("jingDu");
            signLatitude = jsObj.optDouble("weiDu");

            Log.d("公寓坐标:long = ",signLongitude+"lat = "+signLatitude);
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
     * 初始化网络请求
     * */
    public void initOkHttp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
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
                if (Latitude == 0.0 && Longitude == 0.0) {

                    Longitude = loc.getLongitude();
                    Latitude = loc.getLatitude();
                    distance = DistanceOfTwoPoints(Latitude,Longitude,signLatitude,signLongitude);
                    final String coutent = "教师已上传定位坐标，可以定位签到了";
                    if (distance > 50)
                    {
                        AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                        builder.setTitle("提示" ) ;
                        builder.setMessage("不在签到范围！" ) ;
                        builder.setPositiveButton("确定" ,  null );
                        builder.show();
                        stopLocation();
                        Longitude = 0.0;
                        Latitude = 0.0;
                    }else
                    {
                        locationSignIn(state, "sssss", "Zero");
                    }
                }
                String ret = ""+Longitude + Latitude;
                String jStr = Longitude+",";
                String wStr = Latitude+"";
                Log.d("定位结果：long = ",Longitude+"lat = "+Latitude);
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
                                             double lat2,double lng2)
    {
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
    //取出扫码返回的值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra("codedContent");
                Bitmap bitmap = data.getParcelableExtra("codedBitmap");
                String[] ss = content.split(",");
                Log.d("**********",ss[0]+"+++++"+ss[1]);
                if (strings.equals(ss[0])){
                    //state (1为扫码签到  2位扫码签退)
                    if (state.equals("1")){
                        scanCodeSign(state,ss[1],"ss");
                    }else
                    {
                        scanCodeSign(state,ss[1],"ss");
                    }

                }else
                {
                    AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                    builder.setTitle("提示" ) ;
                    builder.setMessage("信息不正确！" ) ;
                    builder.setPositiveButton("确定" ,  null );
                    builder.show();
                }
                Log.d("\"解码结果： \\n\"",content);

            }
        }
    }
    //扫码签到
    private void scanCodeSign(final String state, String theKey,String signType){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("itemId",number)
                .add("studentNo",studentNo)
                .add("state",state)
                .add("theKey",theKey)
                .add("teacherId",teacherId)
                .add("classid",sportClassId)
                .add("classDate",classDate)
                .add("signType",signType)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.QRResultUrl)
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
                        Log.d("网络请求返回值",state);
                        try {
                            JSONObject jsObj = new JSONObject(retStr);
                            String retStr = jsObj.getString("melodyClass");

                            if (retStr.equals("yes")) {
                                if (state.equals("1")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                    builder.setTitle("提示");
                                    builder.setMessage("签到成功！");
                                    builder.setPositiveButton("确定", null);
                                    builder.show();
                                }else if (state.equals("2"))
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                    builder.setTitle("提示");
                                    builder.setMessage("签退成功！");
                                    builder.setPositiveButton("确定", null);
                                    builder.show();
                                }

                            }
                            else if (retStr.equals("no"))
                            {
                                //statuTV.setText("不是签到时间");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("签到失败！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();

                            } else if (retStr.equals("DateError"))
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("签到失败，日期错误！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else if (retStr.equals("theKeyNotCorrect"))
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("签到失败，二维码已过期！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            } else if (retStr.equals("NotInClass"))
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("签退失败，未上课！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }else if (retStr.equals("exist"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("上课签到考勤已记录，请满足课时后再点击下课！");
                                builder.setPositiveButton("确定", null);
                                builder.show();
                            }else {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("签到失败！");
                                builder.setPositiveButton("确定", null);
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

    //定位签到
    private void locationSignIn(final String state, String theKey,String signType){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("itemId",number)
                .add("studentNo",studentNo)
                .add("state",state)
                .add("theKey",theKey)
                .add("classid",sportClassId)
                .add("classDate",classDate)
                .add("signType",signType)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.QRResultUrl)
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
                        Log.d("网络请求返回值",state);
                        try {
                            JSONObject jsObj = new JSONObject(retStr);
                            String retStr = jsObj.getString("melodyClass");

                            if (retStr.equals("yes")) {
                                if (state.equals("1")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                    builder.setTitle("提示");
                                    builder.setMessage("签到成功！");
                                    builder.setPositiveButton("确定", null);
                                    builder.show();
                                    stopLocation();
                                    Latitude = 0.0;
                                    Longitude = 0.0;
                                }else if (state.equals("2"))
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                    builder.setTitle("提示");
                                    builder.setMessage("签退成功！");
                                    builder.setPositiveButton("确定", null);
                                    builder.show();
                                    stopLocation();
                                    Latitude = 0.0;
                                    Longitude = 0.0;
                                }

                            }
                            else if (retStr.equals("no"))
                            {
                                //statuTV.setText("不是签到时间");
                                AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("签到失败！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                                stopLocation();
                                Latitude = 0.0;
                                Longitude = 0.0;

                            } else if (retStr.equals("DateError"))
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("签到失败，日期错误！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                                stopLocation();
                                Latitude = 0.0;
                                Longitude = 0.0;
                            }else if (retStr.equals("theKeyNotCorrect"))
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("签到失败，二维码已过期！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                                stopLocation();
                                Latitude = 0.0;
                                Longitude = 0.0;
                            } else if (retStr.equals("NotInClass"))
                            {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("签退失败，未上课！" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                                stopLocation();
                                Latitude = 0.0;
                                Longitude = 0.0;
                            }else if (retStr.equals("exist"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("上课签到考勤已记录，请满足课时后再点击下课！");
                                builder.setPositiveButton("确定", null);
                                builder.show();
                                stopLocation();
                                Latitude = 0.0;
                                Longitude = 0.0;
                            }else {
                                //statuTV.setText("服务器异常");
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClassAttendanceActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("签到失败！");
                                builder.setPositiveButton("确定", null);
                                builder.show();
                                stopLocation();
                                Latitude = 0.0;
                                Longitude = 0.0;

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
}

