package com.xinnuo.apple.nongda.studentActivity.News;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.xinnuo.apple.nongda.R;

public class MessageDetails extends AppCompatActivity {
    private TextView time;
    private TextView content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        Intent intent = getIntent();
        time = (TextView) findViewById(R.id.stu_news_times);
        content = (TextView) findViewById(R.id.stu_news_contents);
        time.setText( intent.getStringExtra("time"));
        content.setText(intent.getStringExtra("content"));
    }
}
