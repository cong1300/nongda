package com.xinnuo.apple.nongda.singin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.TeacherActivity.TeacherActivity;
import com.xinnuo.apple.nongda.entity.TeacherModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/11/10 0010.
 */

public class Teacher_Singin extends BaseActivity {
    public Intent teacher_Login(Activity activity, JSONObject jsObj , TeacherModel teacherInfo)
    {
        Intent intent = null;
        try {
            setTeacherInfo(jsObj);
            //跳转界面并进行传值 教师姓名 教的班级 教师姓名
            intent = new Intent(activity, TeacherActivity.class);
            intent.putExtra("teacherId", teacherInfo.getId());
            intent.putExtra("itemId", teacherInfo.getItemId());
            intent.putExtra("teacherName",teacherInfo.getName());
            Log.d("*****",teacherInfo.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return intent;
    }
    //教师model
    public TeacherModel setTeacherInfo(JSONObject jsObj) throws JSONException {
        TeacherModel teacher = new TeacherModel();

        teacher.setBirthday(jsObj.getString("birthday"));
        teacher.setPhone(jsObj.getString("phone"));
        teacher.setSex(jsObj.getInt("sex"));
        teacher.setStatus(jsObj.getInt("status"));
        teacher.setJurisdiction(jsObj.getString("jurisdiction"));

        teacher.setItemId(jsObj.getString("itemId"));
        teacher.setCollegeName(jsObj.getString("collegeName"));
        teacher.setPassword(jsObj.getString("password"));
        teacher.setIntro(jsObj.getString("intro"));
        teacher.setId(jsObj.getString("id"));

        teacher.setCardNo(jsObj.getString("cardNo"));
        teacher.setPicture(jsObj.getString("picture"));
        teacher.setNationName(jsObj.getString("nationName"));
        teacher.setName(jsObj.getString("name"));
        teacher.setLoginSta(jsObj.getString("loginSta"));

        return teacher;
    }
}
