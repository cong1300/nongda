package com.xinnuo.apple.nongda.studentActivity.OutDoorSports;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.studentActivity.CampusDynamics.StuCampusDynamics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SportRecordsActivity extends AppCompatActivity {

    private OkHttpClient client;
    ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("1", "111111");

        setContentView(R.layout.activity_sport_records);
        listView = (ListView)findViewById(R.id.list_sport_record);
        Intent intent = getIntent();
        String studentId = intent.getStringExtra("studentId");
        initOkHttp();
        request(studentId);
    }

    /**
     * 初始化网络请求
     */
    public void initOkHttp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    private void request(String stuId) {
        Log.d("1", "2222222");

        //菊花的开始方法
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("studentId", stuId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.SportCount)
                .post(requestBodyPost)
                .build();
        client.newCall(requestPost).

                enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d("1", "333333");

                            }

                            //接收方法
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                //接收httpresponse返回的json数据
                                final String retStr = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("网络请求返回值", retStr);
                                        //菊花结束方法
                                        try {
                                            //解析json数据
                                            jsonParseWithJsonStr(retStr);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }

                );
    }

    public void jsonParseWithJsonStr(String jsonStr) {

        try {
            JSONArray jsonArr = new JSONArray(jsonStr);

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsObj = jsonArr.getJSONObject(i);
                String exerciseDate = jsObj.getString("exerciseDate");
                String step = jsObj.getString("step");
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("exerciseDate", exerciseDate);
                map.put("step","运动量:"+step);
                list.add(map);
            }

//            listView.setAdapter(new ItemListAdapter());

            SimpleAdapter qrAdapter = new SimpleAdapter(this,list,R.layout.item_sport_records,
                    new  String[]{"exerciseDate","step"},
                    new int[]{R.id.tv_sport_records_time,R.id.tv_sport_records_score});
            listView.setAdapter(qrAdapter);
            qrAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class ItemListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_campus_dynamics,
                        null);
                viewHolder = new ViewHolder();

                viewHolder.textView_score = (TextView) convertView
                        .findViewById(R.id.tv_sport_records_score);
                viewHolder.textView_time = (TextView) convertView
                        .findViewById(R.id.tv_sport_records_time);


                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String score = list.get(position).get("exerciseDate").toString();
            String step = list.get(position).get("step").toString();



            viewHolder.textView_score.setText(score);
            viewHolder.textView_time.setText(step);
             return convertView;
        }
    }
    public class ViewHolder {
        public TextView textView_score;
        public TextView textView_time;


    }
}