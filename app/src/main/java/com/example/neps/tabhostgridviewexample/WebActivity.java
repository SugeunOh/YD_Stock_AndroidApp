package com.example.neps.tabhostgridviewexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Neps on 2017. 11. 24..
 */

public class WebActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        setTitle("웹뷰");

        Intent intent = getIntent();
        String title = intent.getStringExtra("selectedItem");

        WebView WebView01 = (WebView) findViewById(R.id.webView);
        WebView01.setWebViewClient(new WebViewClient());

        WebSettings webSettings = WebView01.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if(title.equals("코스피"))
            WebView01.loadUrl("http://m.stock.naver.com/sise/siseIndex.nhn?code=KOSPI");
        else if(title.equals("코스닥"))
            WebView01.loadUrl("http://m.stock.naver.com/sise/siseIndex.nhn?code=KOSDAQ");
        else if(title.equals("다우산업"))
            WebView01.loadUrl("http://m.stock.naver.com/world/item.nhn?symbol=DJI@DJI");
        else if(title.equals("나스닥종합"))
            WebView01.loadUrl("http://m.stock.naver.com/world/item.nhn?symbol=NAS@IXIC");
    }
}
