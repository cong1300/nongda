package com.xinnuo.apple.nongda.studentActivity.CampusDynamics;
/**
 * 校园动态主界面
 * */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xinnuo.apple.nongda.studentActivity.StuClub.StuClubActivity;
import com.xinnuo.apple.nongda.studentActivity.StudentActivity;
import com.xinnuo.apple.nongda.studentActivity.TeacherShow.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.studentActivity.TeacherShow.TeacherShowActivity;
import com.xinnuo.apple.nongda.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StuCampusDynamics extends BaseActivity implements XListView.IXListViewListener {
    private Handler mHandler;
    private OkHttpClient client;
    private XListView mListView;
    private DisplayImageOptions options; // 设置图片显示相关参数
//    private String[] imageUrls; // 图片路径

    private int page;
    private ArrayList <Map>list = new ArrayList();
//    private JSONArray jsonArr;

    @Override
    public int initResource() {
        return R.layout.activity_stu_campus_dynamics;
    }

    @Override
    public void initComponent() {
         mListView = (XListView) findViewById(R.id.xListView);
    }

    @Override
    public void initData() {
        initOkHttp();
        requestWithDataAdd(0);

        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(StuCampusDynamics.this);
        mHandler = new Handler();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {

//                    JSONObject jo = jsonArr.getJSONObject(i-1);
                    Map aMap = list.get(i-1);
                    String xiaoxi = aMap.get("xiaoXi").toString();
                    Intent intent = new Intent(StuCampusDynamics.this,StuCampusDynamicsInfo.class);
                    intent.putExtra("xiaoXi",xiaoxi);

                    Log.d("消息 - ", "onItemClick: "+xiaoxi);

                    startActivity(intent);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public void addListener() {

    }



    /**
     * 网络请求
     * @param add add = 0 刷新 add = 1 加载更多
     */
    private  void requestWithDataAdd(int add){

        if (add == 0)
        {
            page = 1;
            list = new ArrayList<>();

        }
        else
        {
            page ++;
        }
        //菊花的开始方法
         RequestBody requestBodyPost = new FormBody.Builder()
                .add("pageNumber",page+"")
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.StuCampusDynamics)
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
                         try {
                            //解析json数据
                            jsonParseWithJsonStr(retStr);
                        } catch (JSONException e) {
                             e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 解析数据方法
     *
     * */
    //json解析方法

    private  void jsonParseWithJsonStr (final String jsonStr) throws JSONException {
       ImageView imageView = (ImageView) LayoutInflater.from(StuCampusDynamics.this).inflate(R.layout.item_campus_dynamics, null).findViewById(R.id.stu_photo);
        JSONArray jsArr = new JSONArray(jsonStr);
          for (int i = 0 ; i < jsArr.length() ; i++)
        {
            JSONObject jsonObject = jsArr.getJSONObject(i);
            String title = jsonObject.getString("title");
            String content = jsonObject.getString("content");
            String time = jsonObject.getString("time");
            String picture = jsonObject.getString("picture");
            String xiaoXi = jsonObject.getString("xiaoXi");


            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("title",title);
            map.put("content",content);
            map.put("time",time);
            map.put("picture",picture);
            map.put("xiaoXi",xiaoXi);

            list.add(map);
        }
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 构建完成


        mListView.setAdapter(new ItemListAdapter());
     }

    class ItemListAdapter extends BaseAdapter{


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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_campus_dynamics,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) convertView
                        .findViewById(R.id.stu_photo);
                viewHolder.text = (TextView) convertView
                        .findViewById(R.id.stu_title);
                viewHolder.text1 = (TextView) convertView
                        .findViewById(R.id.stu_title1);
                viewHolder.text2 = (TextView) convertView
                        .findViewById(R.id.stu_title2);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String picture = list.get(position).get("picture").toString();
            String title = list.get(position).get("title").toString();
            String content = list.get(position).get("content").toString();
            String time = list.get(position).get("time").toString();
            Log.d("img", "getView: img = "+picture);

            imageLoader.displayImage(picture,
                    viewHolder.image, options);
            viewHolder.text.setText(title);
            viewHolder.text1.setText(content);
            viewHolder.text2.setText(time);
            return convertView;
        }
    }
    public class ViewHolder {
        public ImageView image;
        public TextView text;
        public TextView text1;
        public TextView text2;

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

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestWithDataAdd(0);
                onLoad();
            }
        }, 2000);

    }

    @Override
    public void onLoadMore()
    {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestWithDataAdd(1);
                onLoad();
            }
        }, 2000);
    }
    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("刚刚");
    }

}
