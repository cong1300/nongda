package com.xinnuo.apple.nongda.Admin.News;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class ReleaseMessageActivity extends BaseActivity {
    private OkHttpClient client;
    private Button admin_button;
    private EditText admin_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOkHttp();
        setContentView(R.layout.activity_release_message);
        admin_button = (Button) findViewById(R.id.admin_button);
        admin_text = (EditText) findViewById(R.id.admin_text);
        admin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置显示信息
                final AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseMessageActivity.this);

                builder.setMessage("确定要提交？").
                        // 设置确定按钮
                                setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        String msgContent = admin_text.getText().toString();
                                        scanCodeSign(msgContent);
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
        });
    }
    private void scanCodeSign(String msgContent){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("msgContent",msgContent)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.AdminReleaseMessage)
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
                            if (jsObj.getString("melodyClass").equals("yes")){
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ReleaseMessageActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("消息发布成功！" ) ;
                                builder.setPositiveButton("确定" ,  new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        Intent intent = new Intent(ReleaseMessageActivity.this,AdminNewsActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } );
                                builder.show();

                            }else {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(ReleaseMessageActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("消息发布失败！" ) ;
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
