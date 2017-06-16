package com.example.a219.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt_kakao = (Button)findViewById(R.id.bt_kakao);
        bt_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
                    kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                    kakaoTalkLinkMessageBuilder.addText(str);
                    kakaoTalkLinkMessageBuilder.addImage(imgSrc, 300, 200);
                    kakaoTalkLinkMessageBuilder.addWebButton("자세히 보기", siteUrl);
                    kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), ViewerActivity.this);
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
