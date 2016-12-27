package com.google.firebase.codelab.friendlychat;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by user on 2016/12/21.
 */

public class MyWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
        return true;
    }

    @Override
    public void onPageFinished(WebView mWebView, String url) {
        super.onPageFinished(mWebView, url);
        //在这里执行你想调用的js函数
        if (android.os.Build.VERSION.SDK_INT < 19) {
            mWebView.addJavascriptInterface(new JsCallBack(), "AndroidApp");
            mWebView.loadUrl("GetFriendMessage()");
        } else {
            mWebView.evaluateJavascript("GetFriendMessage('alan')",new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Log.d("LogName", s); // Log is written, but s is always null
                }
            });

        }
    }
    private class JsCallBack
    {
        @JavascriptInterface
        public void responseResult(final String result)
        {
            Log.w("result",result);
        }
    }
}
