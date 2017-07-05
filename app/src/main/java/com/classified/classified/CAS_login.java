package com.classified.classified;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CAS_login extends AppCompatActivity {

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }
        @JavascriptInterface
        public void showHTML(String html) {
            Log.d("hello", html);

            String netid = html.replaceAll("<.*>", "").trim();
            Log.d("hello",netid);

            Intent intent = new Intent(ctx, HomePage.class);
            intent.putExtra("net_id", netid);
            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cas_login);

        final Context context = this;


        final WebView webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl("https://www.cs.princeton.edu/~cjhsu/fristrations/CASlogin.php");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                // url contains ticket
                Log.d("hello", url);
                if (url.contains("ticket=ST")) {
                    webView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                }
            }
        });
    }
}