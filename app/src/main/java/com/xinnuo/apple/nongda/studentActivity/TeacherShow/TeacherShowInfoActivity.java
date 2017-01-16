package com.xinnuo.apple.nongda.studentActivity.TeacherShow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xinnuo.apple.nongda.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class TeacherShowInfoActivity extends AppCompatActivity {
    private String imgStr;
    private String name;
    private String SportsClassName;
    private String score;
    private String jiangXiang;
    private String intro;

    private ImageView imgView;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView text5;
    private ImageLoader imageLoader;
    private ImageLoadingListener animateFirstListener;//这个监听主要是监听加载过程中的各状态

    private DisplayImageOptions options;//图片展示配置
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_show_info);

        Intent intent = getIntent();
        imgStr = intent.getStringExtra("img");
        name = intent.getStringExtra("name");
        SportsClassName = intent.getStringExtra("SportsClassName");
        score = intent.getStringExtra("score");
        jiangXiang = intent.getStringExtra("jiangXiang");
        intro = intent.getStringExtra("intro");

        bingView();
        setToView();
    }

    private void bingView() {
        imgView = (ImageView) findViewById(R.id.img_teacherInfo2);

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 构建完成
//        imageLoader.displayImage(imgStr,
//        imgView, options);
        ImageLoader.getInstance().displayImage(imgStr,imgView, options);



        text1 = (TextView) findViewById(R.id.tv_teachInfo_1);
        text2 = (TextView) findViewById(R.id.tv_teachInfo_2);
        text3 = (TextView) findViewById(R.id.tv_teachInfo_3);
        text4 = (TextView) findViewById(R.id.tv_teachInfo_4);
        text5 = (TextView) findViewById(R.id.tv_teachInfo_5);
    }

    private void setToView() {
        String star = "";
        double aScore;
        if (score.equals("nul")){
            aScore = 0;
        }else {
            aScore = Double.valueOf(score).doubleValue();
//a
        }
        //☆★
        if (aScore == 0){
            star = "";
        }else if (aScore == 1){
            star = "★";

        }else if (aScore == 2){
            star = "★★";

        }else if (aScore == 3){
            star = "★★★";

        }else if (aScore == 4){
            star = "★★★★";

        }else if (aScore == 5){
            star = "★★★★★";

        }else if (aScore > 0 && aScore < 1){
            star = "☆";

        }else if (aScore > 1 && aScore < 2){
            star = "★☆";

        }else if (aScore > 2 && aScore < 3){
            star = "★★☆";

        }else if (aScore > 3 && aScore < 4){
            star = "★★★☆";

        }else if (aScore > 4 && aScore < 5){
            star = "★★★★☆";

        }

        text1.setText("教师:\r" + name);
        text2.setText("课程:\r" + SportsClassName);
        text3.setText("评价:\r" + star);
        text4.setText("奖项:\r"+ jiangXiang);
        text5.setText("个人介绍:\r"+ intro);

        Log.d("img = ", "setToView: img = "+imgStr);
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public  Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();//a
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;

    }
}
