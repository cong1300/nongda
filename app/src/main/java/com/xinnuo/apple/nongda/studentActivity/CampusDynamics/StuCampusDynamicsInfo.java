package com.xinnuo.apple.nongda.studentActivity.CampusDynamics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.studentActivity.TeacherShow.BaseActivity;
import com.xinnuo.apple.nongda.studentActivity.TeacherShow.TeacherShowActivity;
import com.xinnuo.apple.nongda.studentActivity.TeacherShow.TeacherShowInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class StuCampusDynamicsInfo extends BaseActivity {

    private ListView listView;
    private DisplayImageOptions options;//设置图片参数
    private JSONArray xiaoxiArr;

    @Override
    public int initResource() {
        return R.layout.activity_campus_dynamics_info;
    }

    @Override
    public void initComponent() {
        listView = (ListView)findViewById(R.id.lv_stuCampusDynamicsInfo);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        String xiaoxi = intent.getStringExtra("xiaoXi");
        try {
            xiaoxiArr = new JSONArray(xiaoxi);
            for (int i = 0; i < xiaoxiArr.length();i++){
                JSONObject jo = xiaoxiArr.getJSONObject(i);
                String msg = jo.getString("message");
                Log.d("msg", "initData: msg = "+msg);
            }


            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub) // 设置图片下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                    .build(); // 构建完成


            ;
            listView.setAdapter(new ItemListAdapter());



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addListener() {

    }

    class ItemListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return xiaoxiArr.length();
        }

        @Override
        public Object getItem(int i) {
            Object o= null;

            try {
                o = xiaoxiArr.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return o;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null){
                view = getLayoutInflater().inflate(R.layout.item_campus_dynamics_info,null);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView)view.findViewById(R.id.img_campusDynamicsInfo);
                viewHolder.text1 = (TextView)view.findViewById(R.id.tv_campusDynamicsInfo);
            }else {
                viewHolder = (ViewHolder)view.getTag();
            }
            JSONObject jo = null;
            String msg = null;
            String img = null;
            try {
                jo = xiaoxiArr.getJSONObject(i);
                 msg = jo.getString("message");
                 img = jo.getString("mespicture");
                if (msg.length()>5){

                    DisplayMetrics dm2 = getResources().getDisplayMetrics();

                    System.out.println("heigth2 : " + dm2.heightPixels);

                    System.out.println("width2 : " + dm2.widthPixels);

                    viewHolder.image.setMinimumHeight(0);
                    viewHolder.image.setMaxHeight(dm2.heightPixels);
                    viewHolder.image.setMinimumWidth(0);
                    viewHolder.image.setMaxHeight(dm2.widthPixels);

                }else {
                    viewHolder.image.setMinimumHeight(0);
                    viewHolder.image.setMaxHeight(0);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            imageLoader.displayImage(img,
                    viewHolder.image, options);
            viewHolder.text1.setText(msg); // TextView设置文本

            return view;
        }
        public class ViewHolder{
            public ImageView image;
            public TextView text1;
        }
    }

}
