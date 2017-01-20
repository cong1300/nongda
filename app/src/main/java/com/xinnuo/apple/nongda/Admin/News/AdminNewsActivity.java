package com.xinnuo.apple.nongda.Admin.News;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.studentActivity.News.MessageDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminNewsActivity extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private JSONArray jsonArray;
    private TextView adminNews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_news);
        initOkHttp();

        listView = (ListView) findViewById(R.id.list_admin_news);
        adminNews = (TextView) findViewById(R.id.admin_news);
        scanCodeSign();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(position);
                    Intent intent = new Intent(AdminNewsActivity.this,MessageDetails.class);
                    intent.putExtra("time",jsonObject.getString("msgDate"));
                    intent.putExtra("content",jsonObject.getString("msgContent"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        adminNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminNewsActivity.this,ReleaseMessageActivity.class);
                startActivity(intent);
            }
        });
    }
    private void scanCodeSign(){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("status",2+"")
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.StuNews)
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
                            JSONArray jsa = new JSONArray(retStr);
                            jsonArray = jsa;
                            ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
                            for (int i = 0 ; i < jsa.length() ; i++)
                            {
                                JSONObject jsonObject = jsa.getJSONObject(i);
                                String masgDate = jsonObject.getString("msgDate");
                                String msgContent = jsonObject.getString("msgContent");
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("masgDate",masgDate);
                                map.put("msgContent",msgContent);
                                listItem.add(map);
                            }
                            if (jsa.length() == 0)
                            {
                                midToast("无数据",3);
                            }
                            SimpleAdapter qrAdapter = new SimpleAdapter(AdminNewsActivity.this,listItem, R.layout.item_news,
                                    new  String[]{"masgDate","msgContent"},
                                    new int[]{R.id.stu_news_time, R.id.stu_news_content});
                            listView.setAdapter(qrAdapter);
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
