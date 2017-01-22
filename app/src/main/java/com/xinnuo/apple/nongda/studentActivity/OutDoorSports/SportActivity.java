package com.xinnuo.apple.nongda.studentActivity.OutDoorSports;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
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

@SuppressLint("HandlerLeak")

public class SportActivity extends BaseActivity {



    Intent servicec;
    private Switch swi_sport;//按钮
    private TextView tv_minTime,tv_maxTime,tv_minSport,tv_maxSport,tv_show_step, tv_timer;

    private OkHttpClient client;

    private long timer = 0;// 运动时间
    private  long startTimer = 0;// 开始时间
    private  long tempTime = 0;
    private int total_step = 0;   //走的总步数
    private long sportTime = 0;
    private String studentId = null;

    //运动最小时间 运动最小量。运动最大时间 运动最大量
    private long minTime=0,maxTime=0;
    private int minSport=0,maxSport=0;

    private Thread thread;  //定义线程对象
    // 当创建一个新的Handler实例时, 它会绑定到当前线程和消息的队列中,开始分发数据
    // Handler有两个作用, (1) : 定时执行Message和Runnalbe 对象
    // (2): 让一个动作,在不同的线程中执行.

    Handler handler = new Handler() {// Handler对象用于更新当前步数,定时发送消息，调用方法查询数据用于显示？？？？？？？？？？
        //主要接受子线程发送的数据, 并用此数据配合主线程更新UI
        //Handler运行在主线程中(UI线程中), 它与子线程可以通过Message对象来传递数据,
        //Handler就承担着接受子线程传过来的(子线程用sendMessage()方法传递Message对象，(里面包含数据)
        //把这些消息放入主线程队列中，配合主线程进行更新UI。

        @Override                  //这个方法是从父类/接口 继承过来的，需要重写一次
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);        // 此处可以更新UI


            countStep();          //调用步数方法

            tv_show_step.setText(total_step + "");// 显示当前步数
            Log.d(TAG, "handleMessage: 步数"+total_step);
//            tv_timer.setText(getFormatTime(timer));// 显示当前运行时间


            sportTime = getTime(timer);

            Log.d(TAG, "handleMessage: time~ = "+getFormatTime(timer));
            Log.d(TAG, "handleMessage: timeR = "+timer);
            Log.d(TAG, "handleMessage: 分钟 = "+getTime(timer));



        }



    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);
        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
         addView();

        Log.d("activity_sport", "onCreate: activity_sport");
        initOkHttp();



        if (thread == null) {

            thread = new Thread() {// 子线程用于监听当前步数的变化

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    int temp = 0;
                    while (true) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (StepCounterService.FLAG) {
                            Message msg = new Message();
                            if (temp != StepDetector.CURRENT_SETP) {
                                temp = StepDetector.CURRENT_SETP;
                            }
                            if (startTimer != System.currentTimeMillis()) {
                                timer = tempTime + System.currentTimeMillis()
                                        - startTimer;
                            }
                            handler.sendMessage(msg);// 通知主线程
                        }
                    }
                }
            };
            thread.start();
        }
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        Log.i("APP", "on resuame.");
        // 获取界面控件

        // 初始化控件
        init();

    }

    /**
     * 初始化界面
     */
    private void init() {


        countStep();

//        tv_timer.setText(getFormatTime(timer + tempTime));

        tv_show_step.setText(total_step + "");



    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void addView(){
        servicec = new Intent(SportActivity.this, StepCounterService.class);
        swi_sport = (Switch)findViewById(R.id.swi_sport);
//        btnBegin = (Button)findViewById(R.id.button3);
        tv_timer = (TextView)findViewById(R.id.tv_SportState);
        tv_minTime = (TextView)findViewById(R.id.tv_minTime);
        tv_maxTime = (TextView)findViewById(R.id.tv_maxTime);
        tv_minSport = (TextView)findViewById(R.id.tv_minSport);
        tv_maxSport = (TextView)findViewById(R.id.tv_maxSport);
        tv_show_step = (TextView)findViewById(R.id.tv_sportCount);

        StepDetector.CURRENT_SETP = 0;
        tempTime = timer = 0;
//        tv_timer.setText(getFormatTime(timer));      //如果关闭之后，格式化时间
        tv_show_step.setText("0");

        handler.removeCallbacks(thread);

        swi_sport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                            if (isChecked) {

                                tv_timer.setText("采集中..");
                                startService(servicec);

                                startTimer = System.currentTimeMillis();
                                tempTime = timer;

                                Log.d("activity_sport", "onCheckedChanged: show,isChecked="+isChecked);
                } else {

                                tv_timer.setText("请开始采集");
                                stopService(servicec);
//                                ]f.setText(getFormatTime(timer));      //如果关闭之后，格式化时间

                                Log.d("activity_sport", "onCheckedChanged: no,isChecked="+isChecked);
                                handler.removeCallbacks(thread);

                }
            }
        });

    }

//    //运动开始
//    public void onClickSportsStart(View view){
//        Log.d(TAG, "onClickSportsStart: !!!!!!!!!!!!");
//        Intent service = new Intent(this, StepCounterService.class);
//        startService(service);
//
//        startTimer = System.currentTimeMillis();
//        tempTime = timer;
//     }
//    public void onClick(View view) {
//        Intent service = new Intent(this, StepCounterService.class);
//        switch (view.getId()) {
//            case R.id.button3:
//                startService(service);
//
//                startTimer = System.currentTimeMillis();
//                tempTime = timer;
//                break;
//        }
//    }
    //网络请求
    /**
     * 初始化网络请求
     * */
    public void initOkHttp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request requestPost = new Request.Builder()
                .url(httpUrl.SportRules)
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
                        } catch (Exception e) {
                            mLoading.dismiss();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void jsonParseWithJsonStr(String jsonStr){
        try {
            JSONObject jo = new JSONObject(jsonStr);
            String minTimeStr = jo.getString("minTime");
            String maxTimeStr = jo.getString("maxTime");
            String maxSportCountStr = jo.getString("maxSportCount");
            String minSportCountStr = jo.getString("minSportCount");
            //tv_minTime,tv_maxTime,tv_minSport,tv_maxSport,tv_show_step
            tv_minSport.setText("最小运动量:"+minSportCountStr);
            tv_minTime.setText("最小运动时间:"+minTimeStr+"(分钟)");
            tv_maxSport.setText("最大运动量:"+maxSportCountStr);
            tv_maxTime.setText("最大运动时间:"+maxTimeStr+"(分钟)");

            minTime =Long.valueOf(minTimeStr).longValue();
            maxTime =Long.valueOf(maxTimeStr).longValue();
            minSport=Integer.valueOf(minSportCountStr).intValue();
            maxSport=Integer.valueOf(maxSportCountStr).intValue();
            if (minTime == 0 || maxTime==0 || minSport==0||maxSport==0) {
                Dialog alertDialog = new AlertDialog.Builder(this).
                        setTitle("提示").
                        setMessage("跑步信息异常").
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                finish();

                            }
                        }).

                        create();
                alertDialog.show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 得到一个格式化的时间
     *
     * @param time
     *            时间 毫秒
     * @return 时：分：秒：毫秒
     */
    private String getFormatTime(long time) {
        time = time / 1000;
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;

        // 毫秒秒显示两位
        // String strMillisecond = "" + (millisecond / 10);
        // 秒显示两位
        String strSecond = ("00" + second)
                .substring(("00" + second).length() - 2);
        // 分显示两位
        String strMinute = ("00" + minute)
                .substring(("00" + minute).length() - 2);
        // 时显示两位
        String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

        return strHour + ":" + strMinute + ":" + strSecond;
        // + strMillisecond;
    }
    private long getTime(long time) {
        time = time / 1000;
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;

        // 毫秒秒显示两位
        // String strMillisecond = "" + (millisecond / 10);
        // 秒显示两位
        String strSecond = ("00" + second)
                .substring(("00" + second).length() - 2);
        // 分显示两位
        String strMinute = ("00" + minute)
                .substring(("00" + minute).length() - 2);
        // 时显示两位
        String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

        return minute;
        // + strMillisecond;
    }


    /**
     * 实际的步数
     */
    private void countStep() {
        if (StepDetector.CURRENT_SETP % 2 == 0) {
            total_step = StepDetector.CURRENT_SETP;
        } else {
            total_step = StepDetector.CURRENT_SETP +1;
        }

        total_step = StepDetector.CURRENT_SETP;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }


    //计算
    private void calculateGetCompsAndSportsExercise(){
        //当前运动时间
        long resultTime = sportTime;
        //当前运动量
        int sportCount = total_step;

        Log.d(TAG, "当前运动时间: "+resultTime);
        Log.d(TAG, "当前运动量: "+sportCount);
        Log.d(TAG, "最小运动时间: "+minTime);
        Log.d(TAG, "最大运动时间: "+maxTime);
        Log.d(TAG, "最小运动量: "+minSport);
        Log.d(TAG, "最大运动量: "+maxSport);

        if (sportCount < minSport){

            alertShow("运动量不足，请继续!");
            return;
        }else if (sportCount > maxSport){
            sportCount = maxSport;
        }
        if (resultTime<minTime){
            alertShow("运动时间不足，请继续!");
            return;
        }


        //提交

        upload(sportCount);




    }
    //提交
    private void upload(int step){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("step",step+"")
                .add("studentId",studentId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.SportUpload)
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
                            uploadResp(retStr);
                        } catch (Exception e) {
                            mLoading.dismiss();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    private void uploadResp(String jsonStr){
        try {
            JSONObject jo = new JSONObject(jsonStr);
            String retStr = jo.getString("melodyClass");

            if (retStr.equals("成功")){

                Dialog alertDialog = new AlertDialog.Builder(this).
                        setTitle("提示").
                        setMessage("成功").
                 setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    stopService(servicec);

                    finish();

                }
            }).   create();
                alertDialog.show();


            }else {
                Dialog alertDialog = new AlertDialog.Builder(this).
                        setTitle("提示").
                        setMessage("失败").
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).   create();
                alertDialog.show();

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void alertShow(String alertStr){
        AlertDialog.Builder builder = new AlertDialog.Builder(SportActivity.this);
        builder.setTitle("提示");
        builder.setMessage(alertStr);
        builder.setPositiveButton("确定", null);
        builder.show();
     }
    //提交
    public void onClickUpload(View view){
        calculateGetCompsAndSportsExercise();
    }
    //退出
    public void onClickOut(View view){
        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("提示").
                setMessage("确定退出吗？").
                setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        finish();

                    }
                }).
                setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                }).

                create();
        alertDialog.show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {

            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("提示").
                    setMessage("确定退出吗？").
                     setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            finish();

                        }
                    }).
                    setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    }).

                    create();
            alertDialog.show();


        }

        return false;

    }

}
