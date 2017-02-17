package com.xinnuo.apple.nongda.singin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.xinnuo.apple.nongda.BaseActivity;
import com.xinnuo.apple.nongda.entity.StudentModel;
import com.xinnuo.apple.nongda.Binding.StudentBindingActivity;
import com.xinnuo.apple.nongda.studentActivity.StudentActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 学生登录方法
 * stu_Login 跳转方法 setSutdentInfoWith 学生对象赋值方法 stu_Binding 跳转学生绑定方法
 */

public class Student_Singin extends BaseActivity {

    /**
     * 学生跳转方法：登陆成功跳转到学生操作界面
     * 传的值 studentId：学生id studentNo：学号 studentClassId：班级id studentName：学生姓名及班级名称:
     * */
    public Intent stu_Login(Activity activity,JSONObject jsObj , StudentModel studentInfo)
    {
        Intent intent = null;
        try {
            studentInfo = setSutdentInfoWith(jsObj);
            //跳转界面
            intent = new Intent(activity, StudentActivity.class);
            Log.d("student id = ", studentInfo.getId());
            //传值 学生id 学生学号 班级id 学生姓名 班级名称
            intent.putExtra("studentId", studentInfo.getId());
            intent.putExtra("studentNo", studentInfo.getStudentNo());
            intent.putExtra("studentClassId", studentInfo.getClassId());
            intent.putExtra("studentName",studentInfo.getName());
            intent.putExtra("className",studentInfo.getClassName());
            intent.putExtra("password",studentInfo.getPassword());
            Integer gId = studentInfo.getGradeId();
            intent.putExtra("gradeId",gId.toString());
            // Log.d("~~~~~mainclassId = ", studentInfo.getClassId()+"\n"+studentInfo.getStudentNo()+"\n"+studentInfo.getName()+"\n"+studentInfo.getClassName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  intent;
    }

    /**
     * 为学生对象赋值方法
     * */
    public StudentModel setSutdentInfoWith(JSONObject jsObj) throws JSONException
    {
        //学生model
        StudentModel student = new StudentModel();
        student.setCardId(jsObj.getString("cardId"));
        student.setBirthday(jsObj.getString("birthday"));
        student.setPhone(jsObj.getString("phone"));
        student.setStudentNo(jsObj.getString("studentNo"));
        student.setSex(jsObj.getString("sex"));
        student.setGradeId(jsObj.getInt("gradeId"));
        student.setStatus(jsObj.getInt("status"));
        student.setClassId(jsObj.getString("classId"));
        student.setElectiveInformation(jsObj.getString("electiveInformation"));
        student.setPassword(jsObj.getString("password"));
        student.setCollegeName(jsObj.getString("collegeName"));
        student.setNationId(jsObj.getString("nationId"));
        student.setId(jsObj.getString("id"));
        student.setStuSource(jsObj.getString("stuSource"));
        student.setHomeAddress(jsObj.getString("homeAddress"));
        student.setName(jsObj.getString("name"));
        student.setClassName(jsObj.getString("className"));
        student.setLoginSta(jsObj.getString("loginSta"));
        student.setReelect(jsObj.getInt("reelect"));
        student.setIdNumber(jsObj.getString("idNumber"));
        return student;
    }

    /**
     *学生绑定方法 跳转到学生绑定界面
     * 传的值 stuNo 学号
     *  */
    public void stu_Binding(final String stuNo, final Activity activity ,AlertDialog.Builder builder) throws Exception
    {


        //final String userNo = userIdEditText.getText().toString().trim();

        // 设置显示信息
        builder.setMessage("身份未绑定，是否绑定？").
                // 设置确定按钮
                        setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            // 单击事件
                            public void onClick(DialogInterface dialog, int which) {
                                //进行页面跳转 跳到绑定界面需要输入身份证号和手机号进行绑定
                                Intent intent = new Intent(activity, StudentBindingActivity.class);
                                //传值学生id
                                intent.putExtra("stuNo", stuNo);
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
