package com.xinnuo.apple.nongda.TeacherActivity.CourseAttendance;
/**
 * e二维码签到
 * */
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherMakeQRActivity extends AppCompatActivity {

    private OkHttpClient client;
    private String sportClassId;
    private String itemId;
    private String teacherId;
    private Button refreshBtn;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_make_qr);
        initOkHttp();

        //绑定 控件

        refreshBtn = (Button)findViewById(R.id.refreshBtn);
        imageView = (ImageView) findViewById(R.id.code_image);

        //取出教师id 所教课程id

        Intent intent = getIntent();
        teacherId = intent.getStringExtra("teacherId");
        itemId = intent.getStringExtra("itemId");
        sportClassId = intent.getStringExtra("sportClassId");
        makeQRImg();

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeQRImg();
            }
        });

    }

    private void  makeQRImg()
    {
        String QRNumber = getRandomString(10);
        Bitmap qrcode = generateQRCode(teacherId+itemId+sportClassId+","+QRNumber);
        upLoadQR(QRNumber,"1");
        imageView.setImageBitmap(qrcode);

    }

    private void initOkHttp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }
    private Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;
                if (matrix.get(i, j)) {
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }
    //生成二维码 参数教师id  教师所教课程id
    private Bitmap generateQRCode(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            // MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 700, 700);
            return bitMatrix2Bitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    private void upLoadQR(String theKey, String status) {
        Log.d("000", "onDestroy:++++++++++++++++++++++++++ ");

        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        String rid = JPushInterface.getRegistrationID(getApplicationContext());
        String phoneType = android.os.Build.MODEL;
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("teacherid", teacherId)
                .add("status",status)
                .add("theKey", theKey)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.TeacherUpdateTheQrCodeKey)
                .post(requestBodyPost)
                .build();
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("000", "onFailure:++++++++++++++++++++++++++ ");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //接收httpresponse的json数据
                final String retStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("网络请求返回值", retStr);



                    }
                });
            }
        });


    }

}
