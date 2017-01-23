package com.xinnuo.apple.nongda;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.xinnuo.apple.nongda.Admin.AdminActivity;
import com.xinnuo.apple.nongda.Binding.StudentBindingActivity;
import com.xinnuo.apple.nongda.Binding.TeacherBindingActivity;
import com.xinnuo.apple.nongda.entity.AdminModel;
import com.xinnuo.apple.nongda.entity.StudentModel;
import com.xinnuo.apple.nongda.entity.TeacherModel;
import com.xinnuo.apple.nongda.singin.Student_Singin;
import com.xinnuo.apple.nongda.singin.Teacher_Singin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends BaseActivity  {
    private EditText phone_editText;
    private EditText pswd_editText;
    private CheckBox pswd_checkBox;
    private Button login_button;
    private Button Retrieve_button;
    private OkHttpClient client;
    SharedPreferences sp = null;
    private static final int REQUEST_CODE_SCAN = 0x0000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("userinfo",Context.MODE_PRIVATE);
        initView();
        initOkHttp();
        //自动登录 若选中为true 自动登录 不勾选为fouse
        if (sp.getBoolean("pswd_checkBox", true)) {
            phone_editText.setText(sp.getString("uname", null));
            pswd_editText.setText(sp.getString("upswd", null));
            //设置为勾选
            pswd_checkBox.setChecked(true);
            //取出记住的账号和密码
            String name = phone_editText.getText().toString().trim();
            String pswd = pswd_editText.getText().toString().trim();
            //若不为空自动登录
            if (name.length() > 0 && pswd.length() > 0) {
                //调用请求方法进行请求
                onClieck(name, pswd);
            }

        } else {
            //若为空 将checkboxButton设置为false
            pswd_checkBox.setChecked(false);

        }

    }
    //绑定控件
    private void initView() {
        phone_editText = (EditText) findViewById(R.id.phone_editext);
        pswd_editText = (EditText) findViewById(R.id.pswd_editText);
        pswd_checkBox = (CheckBox) findViewById(R.id.pswd_checkBox);
        login_button = (Button) findViewById(R.id.login_button);
        Retrieve_button = (Button) findViewById(R.id.Retrieve_button);
        LimitsEditEnter(phone_editText);
        login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if (v == login_button) {
                    mLoading.show();

                    //取出用户输入的账号和密码
                    final String name = phone_editText.getText().toString().trim();
                    String pswd = pswd_editText.getText().toString().trim();
                    //判断进行相应的弹窗提示
                    if (name.equals("")) {
                        midToast("用户名空", 1);
                        mLoading.dismiss();
                        return;
                    }
                    if (pswd.equals("")) {
                        midToast("密码空", 1);
                        mLoading.dismiss();
                        return;
                    }
                    //查看CheckBoxLogin是否勾选上
                    boolean CheckBoxLogin = pswd_checkBox.isChecked();
                    if (CheckBoxLogin) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("uname", name);
                        editor.putString("upswd", pswd);
                        editor.putBoolean("checkboxBoolean", true);
                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("uname", null);
                        editor.putString("upswd", null);
                        editor.putBoolean("checkboxBoolean", false);
                        editor.commit();
                    }
//                    //判断输入的账号格式是否正确 正确进行跳转 不正确给出提示信息 并跳出此方法
//                    if (IsPhoneNumUtil.isMobileNumber(name))
//                    {
//                        Log.d("phone","phonenumber格式正确");
//                    }else
//                    {   //菊花的结束方法
//                        mLoading.dismiss();
//                        midToast("不是电信号段", 1);
//                        return;
//                    }
                    //请求的登录方法
                    onClieck(name, pswd);

                }

            }
        });
    }

    //请求方法 参数手机号 密码
    private void onClieck(String userId, String password) {

        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String szImei = "";//TelephonyMgr.getDeviceId();
        String rid = JPushInterface.getRegistrationID(getApplicationContext());
        String phoneType = android.os.Build.MODEL;
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("name", userId)
                .add("password", password)
                .add("registrationID", rid)//极光推送的id  手机类型
                .add("deviceNumber", szImei)//绑定的手机序列号
                .add("phoneType",phoneType)//手机的产品名字
                .build();
        Request requestPost = new Request.Builder()
                .url(httpUrl.loginUrl)
                .post(requestBodyPost)
                .build();
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "网络请求 = onFailure: "+e);
//                midToast("登录异常:error = 3", 1);
                mLoading.dismiss();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //接收httpresponse的json数据
                final String retStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("网络请求返回值", retStr);
                        //菊花结束方法
                        mLoading.dismiss();
                        try {
                            //调用解析方法解析数据
                            jsonParseWithJsonStr(retStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mLoading.dismiss();
                            //提示信息
                            midToast("登录异常:error = 1", 1);

                        }
                    }
                });
            }
        });
    }
    //解析数据对象
    private void jsonParseWithJsonStr(String jsonStr) throws JSONException {
        Student_Singin student_singin = new Student_Singin();
        Teacher_Singin teacher_singin = new Teacher_Singin();
        StudentModel studentModel = new StudentModel();
        TeacherModel teacherModel = new TeacherModel();
        JSONArray js = new JSONArray(jsonStr);
        for (int i = 0; i < js.length(); i++)
        {
            JSONObject jsObj = js.getJSONObject(i);
            Log.d("******","********");
            String loginSta = jsObj.getString("loginSta");
            Log.d("******",loginSta);
            /**
             * loginSta:
             * fail             失败
             * success          教师登录成功
             * successToStu     学生登录成功
             * formation        立即验证学生
             * teacherFormation 立即验证教师
             * deviceNumber      不是绑定设备
             *
             */
            //fail             失败
            if (loginSta.equals("fail"))
            {
                midToast("用户名或密码错误", 1);
            }
            //success          学生登录成功
            else if (loginSta.equals("successToStu"))
            {
                /**
                 * status:
                 * status=1 学生
                 * status=2 管理员
                 * status=3 教师
                 * */
                int status = jsObj.getInt("status");
                Log.d("******",status+"");
                //学生登录
                if (status == 1)
                {
                    //为学生对象赋值
                    studentModel = student_singin.setSutdentInfoWith(jsObj);
                    //调用跳转方法返回intent
                    Intent intent =  student_singin.stu_Login(MainActivity.this,jsObj,studentModel);
                    //启动跳转
                    startActivity(intent);
                }
            }else if (loginSta.equals("success"))
            {
                int status = jsObj.getInt("status");
                Log.d("******",status+"");
                if (status == 3)
                {
                    midToast("登陆成功",3);
                    teacherModel = teacher_singin.setTeacherInfo(jsObj);
                    Intent intent = teacher_singin.teacher_Login(MainActivity.this,jsObj,teacherModel);
                    intent.putExtra("cardNo",teacherModel.getCardNo());
                    intent.putExtra("pswd",teacherModel.getPassword());
                    intent.putExtra("phone",teacherModel.getPhone());
                    startActivity(intent);
                }else if (status == 2)
                {
                    /**
                     *
                     * "birthday": "null",
                     "phone": "123456",
                     "sex": 1,
                     "status": 2,
                     "jurisdiction": "1",
                     "collegeName": "null",
                     "password": "admin",
                     "id": 67,
                     "cardNo": "null",
                     "picture": "03.jpg",
                     "name": "admin",
                     "itro": "null",
                     "loginSta": "success"*/
                    AdminModel adminModel = new AdminModel();
                    adminModel.setBirthday(jsObj.optString("birthday"));
                    adminModel.setPhone(jsObj.optString("phone"));
                    adminModel.setSex(jsObj.getInt("sex"));
                    adminModel.setStatus(jsObj.getInt("status"));
                    adminModel.setJurisdiction(jsObj.optString("jurisdiction"));
                    adminModel.setCollegeName(jsObj.optString("collegeName"));
                    adminModel.setPassword(jsObj.getString("password"));
                    adminModel.setId(jsObj.getString("id"));
                    adminModel.setCardNo(jsObj.getString("cardNo"));
                    adminModel.setPicture(jsObj.getString("picture"));
                    adminModel.setItro(jsObj.getString("itro"));
                    adminModel.setLoginSta(jsObj.getString("loginSta"));
                    adminModel.setName(jsObj.getString("name"));
                    Intent intent = new Intent(MainActivity.this,AdminActivity.class);
                    intent.putExtra("adminName",jsObj.getString("name"));
                    intent.putExtra("adminId",jsObj.getString("id"));
                    startActivity(intent);

                }

            }else if (loginSta.equals("formation"))
            {

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final String stuNo = phone_editText.getText().toString().trim();
                Log.d("*****",stuNo);
                // 设置显示信息
                builder.setMessage("身份未绑定，是否绑定？").
                        // 设置确定按钮
                                setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which) {
                                        //进行页面跳转 跳到绑定界面需要输入身份证号和手机号进行绑定
                                        Intent intent = new Intent(MainActivity.this, StudentBindingActivity.class);
                                        //传值学生id
                                        intent.putExtra("stuNo", stuNo);
                                        String state = "1";
                                        intent.putExtra("state",state);
                                        startActivity(intent);
                                    }
                                }).
                        // 设置取消按钮
                                setNegativeButton("否",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                });
                // 创建对话框
                AlertDialog ad = builder.create();
                // 显示对话框
                ad.show();
            }else if (loginSta.equals("teacherFormation"))
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final String userNo = phone_editText.getText().toString().trim();
                //取出教师id
                final String teacherBindId = jsObj.getString("id");


                // 设置显示信息
                builder.setMessage("身份未绑定，是否绑定？").
                        // 设置确定按钮
                                setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    // 单击事件
                                    public void onClick(DialogInterface dialog, int which) {
                                        //点击是进行页面跳转，输入教师工号和手机号进行绑定
                                        Intent intent = new Intent(MainActivity.this, TeacherBindingActivity.class);
                                        //传值 教师id
                                        intent.putExtra("teacherInfoId", teacherBindId);
                                        String state = "1";
                                        intent.putExtra("state",state);

                                        startActivity(intent);
                                    }
                                }).
                        // 设置取消按钮
                                setNegativeButton("否",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                });
                // 创建对话框
                AlertDialog ad = builder.create();
                // 显示对话框
                ad.show();
            }
        }
    }
    //初始化请求
    public void initOkHttp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }
}
