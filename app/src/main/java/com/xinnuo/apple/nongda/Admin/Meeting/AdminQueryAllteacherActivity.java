package com.xinnuo.apple.nongda.Admin.Meeting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

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

public class AdminQueryAllteacherActivity extends BaseActivity {
    private OkHttpClient client;
    private ListView listView;
    private String teacherId;
    private String teacherIds;
    private JSONArray dataArr;
    private int positions;
    private String name;
    private String state;
    private String coordinateId;
    private String phone;
    private String subjectName;
    private String item;
    private String statu;

    private List<String> arraylist;
    private List<String> arraylist1;
    private List<String> arraylist2;
    private List<String> arraylist3;
    private List<String> arraylist4;
    private List<String> arraylist6;
    private String[] arraylist5;

    private Myadapter myAdapter;
    private Button btn;
    public static boolean[] barray;
    private RelativeLayout relayout;
    private Button cancle;
    private Button select;
    private int count = 0;
    public static boolean visiblecheck = true;  //是否显示checkbox

    public static boolean isMulChoice = true; //是否多选
    private int n = 0;


    private TextView Determine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_query_allteacher);
        initOkHttp();
        Intent intent = getIntent();
        statu = intent.getStringExtra("statu");
        teacherIds  = intent.getStringExtra("teacherId");
        arraylist = new ArrayList<String>();
        arraylist1 = new ArrayList<String>();
        arraylist2 = new ArrayList<String>();
        arraylist3 = new ArrayList<String>();
        arraylist4 = new ArrayList<String>();
        arraylist6 = new ArrayList<String>();

        Determine = (TextView) findViewById(R.id.Determine);
        Determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMulChoice = true;
                visiblecheck = true;

                n = arraylist4.size();
                for (int i = n-1; i >= 0; i--) {
                    if(barray[i]){
                        if (arraylist4.get(i).length() != 0){
                            arraylist6.add(arraylist4.get(i));
                            Log.d("+++++++++++++++++++++++",arraylist6.size()+"*************");
                        }

                    }else {
                        Log.d("+++++++++++++++++++++++","*********************");
                    }
                }
                // 设置显示信息
                final AlertDialog.Builder builder = new AlertDialog.Builder(AdminQueryAllteacherActivity.this);

                builder.setMessage("确定要提交名单？").
                        // 设置确定按钮
                                setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        try{
                                            Intent intent = new Intent(AdminQueryAllteacherActivity.this,NewReleaseActivity.class);
                                            arraylist5 = new String[arraylist6.size()];
                                            for (int i = 0;i < arraylist6.size(); i++){
                                                arraylist5[i] = arraylist6.get(i);
                                            }
                                            intent.putExtra("string",arraylist5);
                                            intent.putExtra("teacherId",teacherIds);
                                            startActivity(intent);
                                        }catch (Exception e){

                                        }

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
        listView = (ListView) findViewById(R.id.admin_query_all_teacher);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //处理点击当前项的事件
               // relayout.setVisibility(View.GONE);
                visiblecheck = false;
                isMulChoice = false;
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                isMulChoice = true;
                if(isMulChoice){
                    barray[position] = true;
                    relayout.setVisibility(View.VISIBLE);
                    visiblecheck = true;
                    myAdapter.notifyDataSetChanged();

                    Toast.makeText(AdminQueryAllteacherActivity.this, "可多选", Toast.LENGTH_SHORT).show();
                }else{
                    visiblecheck = true;
                    isMulChoice = true;
                    for (int i = 0; i < arraylist.size(); i++) {
                        barray[i] = false;
                    }
                    myAdapter.notifyDataSetChanged();
                    //处理点击当前项的事件
                    Toast.makeText(AdminQueryAllteacherActivity.this, "点击了第"+position+"项", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        requestWithUserId();
    }
    /**
     * 请求方法
     * */
    private  void requestWithUserId(){
        //菊花的开始方法
        mLoading.show();
        RequestBody requestBodyPost = new FormBody.Builder()
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.QueryAllTheeachers)
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

        JSONArray jsArr = new JSONArray(jsonStr);
        dataArr = jsArr;
        List<String> data = new ArrayList<String>();
        arraylist5 = new String[jsArr.length()];
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();/*在数组中存放数据*/
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        arraylist.add("序 号");
        arraylist1.add("姓 名");
        arraylist2.add("任教学科");
        arraylist3.add("电 话");
        for (int i = 1; i < jsArr.length(); i++) {
            JSONObject js = jsArr.getJSONObject(i);
            arraylist.add(i+"");
            arraylist1.add(js.optString("name"));
            arraylist2.add(js.optString("subjectName"));
            arraylist3.add(js.optString("phone"));
            arraylist4.add(js.optString("teacherId"));
        }
        if (jsArr.length() == 0)
        {
            midToast("无数据",3);
        }
        barray = new boolean[arraylist.size()];
        for (int i = 0; i < arraylist.size(); i++) {
            barray[i] = false;
        }
        myAdapter = new Myadapter(AdminQueryAllteacherActivity.this,arraylist,arraylist1,arraylist2,arraylist3,barray);
        listView.setAdapter(myAdapter);

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
