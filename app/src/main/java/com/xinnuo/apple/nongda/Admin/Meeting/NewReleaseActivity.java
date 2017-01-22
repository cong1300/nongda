package com.xinnuo.apple.nongda.Admin.Meeting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinnuo.apple.nongda.Admin.widget.PickTimeView;
import com.xinnuo.apple.nongda.Admin.widget.PickValueView;
import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewReleaseActivity extends BaseActivity implements View.OnClickListener, PickTimeView.onSelectedChangeListener, PickValueView.onSelectedChangeListener {
    private EditText title;
    private TextView oject;
    private TextView times;
    private EditText place;
    private EditText introduction;
    private TextView immediate_release;
    private String[] strings;
    private int count = 0;
    private int state = 0;
    private OkHttpClient client;
    private String titles;
    private String places;
    private String introductions;
    private String invitationState;
    private String invitationTeachers;
    private String pushState;

    LinearLayout pvLayout;
    PickTimeView pickTime;
    PickTimeView pickDate;
    PickValueView pickValue;
    PickValueView pickValues;
    PickValueView pickString;
    SimpleDateFormat sdfTime;
    SimpleDateFormat sdfDate;
    private String meetingTime;
    private String teacherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_release);
        binding();
        initOkHttp();
        Intent intent = getIntent();
        if (intent.getStringArrayExtra("string") != null){
            strings = intent.getStringArrayExtra("string");
            invitationState = "2";
            for (int i = 0; i < strings.length; i++){
                if(i == 0) invitationTeachers = strings[i];
                else invitationTeachers += "," + strings[i];
                Log.d("==============",invitationTeachers+strings[i]+"****************");
            }
            Log.d("==============",invitationTeachers+"****************");
            oject.setText("从名单选择");
        }else{
            oject.setText("学校全体教师");
        }
        if (intent.getStringExtra("teacherId") != null){
            teacherId = intent.getStringExtra("teacherId");
        }


    }
    private void binding(){
        title = (EditText) findViewById(R.id.admin_title);
        oject = (TextView) findViewById(R.id.admin_object);
        times = (TextView) findViewById(R.id.admin_meeting_time);
        place = (EditText) findViewById(R.id.admin_meeting_place);
        introduction = (EditText) findViewById(R.id.admin_meeting_introduction);
        immediate_release = (TextView) findViewById(R.id.immediate_release);
        pvLayout = (LinearLayout) findViewById(R.id.Main_pvLayout);
        pickTime = (PickTimeView) findViewById(R.id.pickTime);
        pickDate = (PickTimeView) findViewById(R.id.pickDate);
        pickValue = (PickValueView) findViewById(R.id.pickValue);
        pickValues = (PickValueView) findViewById(R.id.pickValues);
        pickString = (PickValueView) findViewById(R.id.pickString);
        sdfTime = new SimpleDateFormat("MM-dd EEE HH:mm");
        sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        pickTime.setOnSelectedChangeListener(this);
        pickDate.setOnSelectedChangeListener(this);
        pickValue.setOnSelectedChangeListener(this);
        pickValues.setOnSelectedChangeListener(this);
        pickString.setOnSelectedChangeListener(this);

        title.setOnClickListener(this);
        oject.setOnClickListener(this);
        times.setOnClickListener(this);
        place.setOnClickListener(this);
        introduction.setOnClickListener(this);
        immediate_release.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.admin_object:
                final String[] items = new String[]{"学校全体教师","从名单选择","取消"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(NewReleaseActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Toast.makeText(NewReleaseActivity.this, "您选中了："+items[0], Toast.LENGTH_SHORT).show();
                                oject.setText(items[0]);
                                state = 1;
                                invitationState = "1";
                                invitationTeachers = "0";
                                break;
                            case 1:
                                Toast.makeText(NewReleaseActivity.this, "您选中了："+items[1], Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(NewReleaseActivity.this, AdminQueryAllteacherActivity.class);
                                intent2.putExtra("teacherId",teacherId);
                                startActivity(intent2);
                                oject.setText(items[1]);
                                state = 1;

                                break;
                            case 2:
                                Toast.makeText(NewReleaseActivity.this, "您选中了："+items[2], Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).show();
                break;
            case R.id.admin_meeting_time:
                if (count == 0){
                    pvLayout.setVisibility(View.VISIBLE);
                    count++;
                }else {
                    pvLayout.setVisibility(View.GONE);
                    count = 0;
                }

                break;
            case R.id.immediate_release:
                titles = title.getText().toString();
                places = place.getText().toString();
                introductions = introduction.getText().toString();
                if (titles.length() == 0 ){
                    AlertDialog.Builder builders  = new AlertDialog.Builder(NewReleaseActivity.this);
                    builders.setTitle("提示" ) ;
                    builders.setMessage("请补全信息！" ) ;
                    builders.setPositiveButton("确定" ,  null );
                    builders.show();
                }else if (places.length() == 0){
                    AlertDialog.Builder builders  = new AlertDialog.Builder(NewReleaseActivity.this);
                    builders.setTitle("提示" ) ;
                    builders.setMessage("请补全信息！" ) ;
                    builders.setPositiveButton("确定" ,  null );
                    builders.show();
                }else if (introductions.length() == 0){
                    AlertDialog.Builder builders  = new AlertDialog.Builder(NewReleaseActivity.this);
                    builders.setTitle("提示" ) ;
                    builders.setMessage("请补全信息！" ) ;
                    builders.setPositiveButton("确定" ,  null );
                    builders.show();
                }else{
                    final String[] itemss = new String[]{"发布,并通知相关人员","发布但不通知","取消"};
                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(NewReleaseActivity.this);
                    builder1.setItems(itemss, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    pushState = "0";
                                    scanCodeSign(pushState);
                                    break;
                                case 1:
                                    pushState = "1";
                                    scanCodeSign(pushState);
                                    break;
                                case 2:
                                    Toast.makeText(NewReleaseActivity.this, "您选中了："+itemss[2], Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }).show();

                }
                break;
        }
    }

    @Override
    public void onSelected(PickTimeView view, long timeMillis) {
        if (view == pickTime) {
            String str = sdfTime.format(timeMillis);
            times.setText(str);
            meetingTime = str;
        } else if (view == pickDate) {
            String str = sdfDate.format(timeMillis);
            times.setText(str);
            meetingTime = str;
        }
    }

    @Override
    public void onSelected(PickValueView view, Object leftValue, Object middleValue, Object rightValue) {
        if (view == pickValue) {
            int left = (int) leftValue;
            times.setText("selected:" + left);
        } else if (view == pickValues) {
            int left = (int) leftValue;
            int middle = (int) middleValue;
            int right = (int) rightValue;
            times.setText("selected: left:" + left + "  middle:" + middle + "  right:" + right);
        } else {
            String selectedStr = (String) leftValue;
            times.setText(selectedStr);
        }
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
    private void scanCodeSign(String pushState){
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("meetingTime",meetingTime)
                .add("id",teacherId)
                .add("place",places)
                .add("introduction",introductions)
                .add("theme",titles)
                .add("invitationState",invitationState)
                .add("invitationTeachers",invitationTeachers)
                .add("pushState",pushState)
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.AddMeeting)
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
                                AlertDialog.Builder builder  = new AlertDialog.Builder(NewReleaseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("会议发布成功！" ) ;
                                builder.setPositiveButton("确定" ,  new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 设置TextView文本
                                        //点击是的时候去进行提交
                                        Intent intent = new Intent(NewReleaseActivity.this,AdministrationActivity.class);
                                        intent.putExtra("teacherId",teacherId);
                                        startActivity(intent);
                                    }
                                } );
                                builder.show();

                            }else {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(NewReleaseActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("会议发布失败！" ) ;
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

}
