package com.xinnuo.apple.nongda.studentActivity.StuClub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.studentActivity.StudentActivity;
import com.xinnuo.apple.nongda.view.XListView;

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

public class StuClubActivity extends BaseActivity implements XListView.IXListViewListener  {
    private TextView monday;
    private TextView tuesday;
    private TextView wednesday;
    private TextView thursday;
    private TextView friday;
    private TextView saturday;
    private TextView sunday;
    private TextView weeks;
    private LinearLayout week;
    private int page;
    private ArrayAdapter<String> mAdapter;
    private Handler mHandler;
    private ArrayList<String> items;
    private OkHttpClient client;
    private XListView mListView;
    private String classDate = "";
    private String studentId;
    private TextView transfer;
    private TextView Alldate;
    private LinearLayout ListView;
    private String state;
    private String name;
    private String password;
    private String studentNo;
    private List<String> item = new ArrayList<String>();
    private JSONArray clubJson;

    JSONObject datrr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_club);
        initOkHttp();

        binding();
        final Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        name = intent.getStringExtra("name");
        studentNo = intent.getStringExtra("studentNo");
        password = intent.getStringExtra("pswd");

        requestWithDataAdd(0,"");
        mListView = (XListView) findViewById(R.id.xListView);
        mListView.setPullLoadEnable(true);
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(StuClubActivity.this);
        mHandler = new Handler();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                try{
                    if (datrr.getString("loginSta").equals("AfterBinding")){
                        // 设置显示信息
                        final AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);

                        builder.setMessage("是否确定调课?选课期间只有一次手动调课机会！").
                                        // 设置确定按钮
                                        setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            // 单击事件
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                // 设置TextView文本
                                                //点击是的时候去进行提交
                                                applicationForTransfer();

                                            }
                                        }).
                                // 设置取消按钮
                                        setNegativeButton("取消",
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
                    }else {
                        // 设置显示信息
                        final AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);

                        builder.setMessage("是否加入该俱乐部?").
                                // 设置确定按钮
                                        setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            // 单击事件
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                // 设置TextView文本
                                                //点击是的时候去进行提交
                                                String clubId = item.get(position);
                                                scanCodeSign(clubId);
                                            }
                                        }).
                                // 设置取消按钮
                                        setNegativeButton("取消",
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

                }catch (Exception e){

                }


            }
        });

        weeks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView.setVisibility(View.GONE);
                week.setVisibility(View.VISIBLE);
                monday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        week.setVisibility(View.GONE);
                        classDate = "星期一";
                        requestWithDataAdd(0,classDate);
                        ListView.setVisibility(View.VISIBLE);
                    }
                });
                tuesday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        week.setVisibility(View.GONE);
                        classDate = "星期二";
                        requestWithDataAdd(0,classDate);
                        ListView.setVisibility(View.VISIBLE);
                    }
                });
                wednesday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        week.setVisibility(View.GONE);
                        classDate = "星期三";
                        requestWithDataAdd(0,classDate);
                        ListView.setVisibility(View.VISIBLE);
                    }
                });
                thursday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        week.setVisibility(View.GONE);
                        classDate = "星期四";
                        requestWithDataAdd(0,classDate);
                        ListView.setVisibility(View.VISIBLE);
                    }
                });
                friday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        week.setVisibility(View.GONE);
                        classDate = "星期五";
                        requestWithDataAdd(0,classDate);
                        ListView.setVisibility(View.VISIBLE);
                    }
                });
                saturday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        classDate = "星期六";
                        week.setVisibility(View.GONE);
                        requestWithDataAdd(0,classDate);
                        ListView.setVisibility(View.VISIBLE);
                    }
                });
                sunday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        week.setVisibility(View.GONE);
                        classDate = "星期日";
                        requestWithDataAdd(0,classDate);
                        ListView.setVisibility(View.VISIBLE);
                    }
                });
                Alldate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        week.setVisibility(View.GONE);
                        classDate = "";
                        requestWithDataAdd(0,classDate);
                        ListView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
    public void binding()
    {
        monday = (TextView) findViewById(R.id.Monday);
        tuesday = (TextView) findViewById(R.id.Thursday);
        wednesday = (TextView) findViewById(R.id.Wednesday);
        thursday = (TextView) findViewById(R.id.Thursday);
        friday = (TextView) findViewById(R.id.Friday);
        saturday = (TextView) findViewById(R.id.Saturday);
        sunday = (TextView) findViewById(R.id.Sunday);
        weeks = (TextView) findViewById(R.id.weeks);
        week = (LinearLayout) findViewById(R.id.week);
        ListView = (LinearLayout) findViewById(R.id.ListView);
        Alldate = (TextView) findViewById(R.id.Alldate);
    }
    /**
     * 网络请求
     * @param add add = 0 刷新 add = 1 加载更多
     */
    private  void requestWithDataAdd(int add,String classDate){

        if (add == 0)
        {
            page = 1;
            items = new ArrayList<String>();

        }
        else
        {
            page ++;
        }
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",studentId)
                .add("pageNumber",page+"")
                .add("classDate",classDate)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.StuClubList)
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
     *
     * */
    //json解析方法

    private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {

        JSONArray jsArr = new JSONArray(jsonStr);

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        for (int i = 0; i < jsArr.length() ; i++)
        {
            JSONObject js = jsArr.getJSONObject(i);
            datrr = js;
            if (js.getString("loginSta").equals("AfterBinding"))
            {
                weeks.setVisibility(View.GONE);
                JSONObject jsonObject = js.getJSONObject("classList");
                classDate = jsonObject.getString("classDate");
                String sportsClassName = jsonObject.getString("sportsClassName");
                String teacherName = jsonObject.getString("teacherName");
                String schoolDistrictId = jsonObject.getString("schoolDistrictId");
                String time = jsonObject.getString("classDate")+"\t"+jsonObject.getString("startTime");
                String factAmount = jsonObject.getString("factAmount");
                String transfer = "申请调课";
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("sportsClassName",sportsClassName);
                map.put("teacherName",teacherName);
                map.put("schoolDistrictId",schoolDistrictId);
                map.put("time",time);
                map.put("factAmount",factAmount);
                map.put("transfer",transfer);
                listItem.add(map);

            }else
            {
                JSONArray jsonArray = js.getJSONArray("classList");
                clubJson = jsonArray;
                for (int j = 0; j < jsonArray.length(); j++)
                {

                    JSONObject stuJson = jsonArray.getJSONObject(i);
                    item.add(stuJson.getString("id"));
                    classDate = stuJson.getString("classDate");
                    String sportsClassName = stuJson.getString("sportsClassName");
                    String teacherName = stuJson.getString("teacherName");
                    String schoolDistrictId = stuJson.getString("schoolDistrictId");
                    String time = stuJson.getString("classDate")+"\t"+stuJson.getString("startTime");
                    String factAmount = stuJson.getString("factAmount");
                    String transfer = "加入课程";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("sportsClassName",sportsClassName);
                    map.put("teacherName",teacherName);
                    map.put("schoolDistrictId",schoolDistrictId);
                    map.put("time",time);
                    map.put("factAmount",factAmount);
                    map.put("transfer",transfer);
                    listItem.add(map);
                }
            }
        }

        SimpleAdapter qrAdapter = new SimpleAdapter(this,listItem,R.layout.item_stu_club,
                new  String[]{"sportsClassName","teacherName","schoolDistrictId","time","factAmount","transfer"},
                new int[]{R.id.stuclub_curriculum,R.id.stuclub_teacher,R.id.stuclub_classroom,R.id.stuclub_time,R.id.stuclub_numberof,R.id.stuclub_transfers});
        mListView.setAdapter(qrAdapter);
        qrAdapter.notifyDataSetChanged();
    }


    private void scanCodeSign(final String jid){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("jid",jid)
                .add("id",studentId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.StuAddClub)
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
                            String retStr = jsObj.getString("msg");
                            if (retStr.equals("1"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("加入成功！");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        Intent intent = new Intent(StuClubActivity.this, StudentActivity.class);
                                        intent.putExtra("studentId",studentId);
                                        intent.putExtra("studentNo",studentNo);
                                        intent.putExtra("password",password);
                                        intent.putExtra("studentName",name);
                                        startActivity(intent);
                                    }
                                });
                                builder.show();
                            }else if (retStr.equals("2"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("加入失败，性别不符！");
                                builder.setPositiveButton("确定", null);
                                builder.show();
                            }else if (retStr.equals("3"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("加入失败，年级不符！");
                                builder.setPositiveButton("确定", null);
                                builder.show();
                            }else if (retStr.equals("4"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("加入失败，当前班级人数已满！");
                                builder.setPositiveButton("确定", null);
                                builder.show();
                            }else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("加入失败！");
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

    //调课请求
    private void applicationForTransfer(){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("id",studentId)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.StuApplicationForTransfer)
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
                            if (retStr.equals("1"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("申请成功！");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        Intent intent = new Intent(StuClubActivity.this, StudentActivity.class);
                                        intent.putExtra("studentId",studentId);
                                        intent.putExtra("studentNo",studentNo);
                                        intent.putExtra("password",password);
                                        intent.putExtra("studentName",name);
                                        startActivity(intent);
                                    }
                                });
                                builder.show();
                            }else if (retStr.equals("2"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("已重选过体育班，请联系管理员！");
                                builder.setPositiveButton("确定", null);
                                builder.show();
                            }else if (retStr.equals("3"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("已过调班时间,请联系管理员！！");
                                builder.setPositiveButton("确定", null);
                                builder.show();
                            }else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StuClubActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("申请失败！");
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
                requestWithDataAdd(0,classDate);
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
                requestWithDataAdd(page,classDate);
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
