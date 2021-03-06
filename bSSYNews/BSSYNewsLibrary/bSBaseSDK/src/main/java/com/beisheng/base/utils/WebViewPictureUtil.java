
package com.beisheng.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.beisheng.base.activity.ImagePreviewActivity;

import java.util.ArrayList;

@SuppressLint("JavascriptInterface")
public class WebViewPictureUtil extends WebViewClient {
    private Context mContext;
    private String mText;
    private Handler mHandler = new Handler();
    private JSCallBack mJSCallback;
    private String mTable;
    private String mValue;
    private WebView mWebView;

    public interface JSCallBack {
        public void jsCallBack(String status);

        public void jsCallBack(String str, String status);
    }

    public WebViewPictureUtil(Context context, WebView webView, String table, String value) {
        this.mContext = context;
        this.mTable = table;
        this.mValue = value;
        webView.addJavascriptInterface(new JsObject(context), "viewlistner");
    }

    public WebViewPictureUtil(Context context, WebView webView, JSCallBack jsCallBack) {
        this.mContext = context;
        this.mWebView = webView;
        this.mJSCallback = jsCallBack;
        mWebView.addJavascriptInterface(new JsObject(mContext), "JsObject");
    }

    public JsObject getJsObject(Context context) {
        return new JsObject(context);
    }

    // 注入js函数监听
    private void addImageClickListner(WebView webView) {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        // "var obj = document.getElementById("testbtn");" +
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"" +
                mTable +
                "\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.viewlistner.onViewClick(" +
                mValue +
                ");  " +
                "    }  " +
                "}" +
                "})()");
    }

    // js通信接口
    public class JsObject {

        private Context context;

        public JsObject(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void onViewClick(final String status) {
            if (mJSCallback == null) {
                Intent intent = new Intent();
                ArrayList<String> piclist = new ArrayList<String>();
                piclist.add(status);
                intent.putStringArrayListExtra("piclist", (ArrayList<String>) piclist);
                intent.setClass(context, ImagePreviewActivity.class);
                intent.putExtra("imgIndex", "1");
                context.startActivity(intent);
            } else {
                mJSCallback.jsCallBack(status);
            }
        }

        @JavascriptInterface
        public void onViewClick(String str, final String status) {
            // 1是连接 //2是图片
            mJSCallback.jsCallBack(str, status);
        }

        public void callHtmlJS() {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript: showFromHtml2('IT-homer blog')");
                }
            });
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        view.getSettings().setJavaScriptEnabled(true);

        super.onPageFinished(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        view.getSettings().setJavaScriptEnabled(true);

        super.onPageStarted(view, url, favicon);
        // html加载完成之后，添加监听图片的点击js函数，添加JS,并添加监听回调
        if (mJSCallback == null) {
            addImageClickListner(view);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

        super.onReceivedError(view, errorCode, description, failingUrl);

    }

    public JSCallBack getmJSCallback() {
        return mJSCallback;
    }

    public void setmJSCallback(JSCallBack mJSCallback) {
        this.mJSCallback = mJSCallback;
    }

    public String getmTable() {
        return mTable;
    }

    public void setmTable(String mTable) {
        this.mTable = mTable;
    }

}
