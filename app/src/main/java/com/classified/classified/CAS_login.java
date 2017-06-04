package com.classified.classified;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CAS_login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cas_login);

        final Context context = this;

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl("https://fed.princeton.edu/cas/");
        //Toast.makeText(this, webView.getUrl(), Toast.LENGTH_LONG).show();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                String cookies = CookieManager.getInstance().getCookie(url);
                if (cookies.contains("CASTGC")) {
                    //view.loadUrl("about:blank");
                    Log.d("hello", "AUTHENTICATED");
                    Intent intent = new Intent(context, HomePage.class);
                    startActivity(intent);
                }
            }
        });
    }
}
