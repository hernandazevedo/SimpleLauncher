package br.com.buy4.simplelauncher;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends Activity {

    public static final String EXTRA_URL = "EXTRA_URL";
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = (String) getIntent().getExtras().get(EXTRA_URL);
        webView.loadUrl("file://"+ url);

    }
}
