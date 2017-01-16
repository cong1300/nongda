/*******************************************************************************
 *
 * Copyright (c) Weaver Info Tech Co. Ltd
 *
 * BaseActivity
 *
 * app.ui.BaseActivity.java
 * TODO: File description or class description.
 *
 * @author: Administrator
 * @since:  2014-9-03
 * @version: 1.0.0
 *
 * @changeLogs:
 *     1.0.0: First created this class.
 *
 ******************************************************************************/
package com.xinnuo.apple.nongda;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author gao_chun
 * 该类为Activity基类
 */
public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "gao_chun";

    //在基类中初始化Dialog
    public Dialog mLoading;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (!ValidateUtils.isNetworkAvailable(this)){
            DialogUtils.showToast(this,R.string.text_network_unavailable);
        }*/
        mLoading = DialogUtils.createLoadingDialog(this);
    }
    /**
     * 提示信息框
     * */
    public void midToast(String str, int showTime) {
        Toast toast = Toast.makeText(this, str, showTime);
        toast.show();
    }
    //禁止输入用户名的EditText换行
    public static void LimitsEditEnter(EditText et) {
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
    }

    public void onDetach() {
    }
}
