package com.xinnuo.apple.nongda.studentActivity.OnlineTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xinnuo.apple.nongda.R;

public class OnlineTestActivity extends AppCompatActivity {
    private String stuNo;
    private String pswd;
    private Button start_online;
    private Button exemption;
    private WebView webView;
    private LinearLayout space;
    private String name;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_test);
        final Intent intent = getIntent();
        start_online = (Button) findViewById(R.id.start_online);
        exemption = (Button) findViewById(R.id.exemption);
        webView = (WebView) findViewById(R.id.webView);
        space = (LinearLayout) findViewById(R.id.spaces);
        stuNo = intent.getStringExtra("studentNo");
        pswd = intent.getStringExtra("password");
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        start_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.setVisibility(View.VISIBLE);
                space.setVisibility(View.GONE);
                init();
            }
        });
        exemption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(OnlineTestActivity.this,ApplyForExemptionActivity.class);
                intent1.putExtra("id",id);
                intent1.putExtra("name",name);
                intent1.putExtra("stuNo",stuNo);
                startActivity(intent1);
            }
        });
    }

    private void init(){
        //WebView加载web资源
        webView.loadUrl("http://192.168.1.166:9090/nongda/onlineAnswer_getRandomQuestions2.action?user="+stuNo+"&pass="+pswd);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }
}
