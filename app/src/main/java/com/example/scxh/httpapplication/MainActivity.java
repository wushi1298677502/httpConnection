package com.example.scxh.httpapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 *
 LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
 LOAD_DEFAULT: 根据cache-control决定是否从网络上取数据。
 LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level 11开始作用同LOAD_DEFAULT模式
 LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
 LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
 */
public class MainActivity extends AppCompatActivity {
    public static String APP_CACAHE_DIRNAME ="httpWebView";
     ProgressBar mProgressBar;
    WebView mWebView;

    //第二步 定义JAVAScript交互接口
    interface MyJavaScriptInterface {
        void showToast();
        String htmlToNativeToJs();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.http_progressbar);
        mWebView = (WebView) findViewById(R.id.http_webview);
        //设置javaScript与android原生应用交互接口
        setJavascriptInterface();
        //设置web端
        setWebVieClient();

        loadData();
//        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);样式是滚动条在整个page里
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // 设置  缓存模式
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启 database storage API 功能
        mWebView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath()+ APP_CACAHE_DIRNAME;
        // String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        Logs.e("cacheDirPath=" + cacheDirPath);
        // 设置数据库缓存路径
        mWebView.getSettings().setDatabasePath(cacheDirPath);
        // 设置 Application Caches 缓存目录
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        // 开启 Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置javaScript与android原生应用交互接口
     */
    public void setJavascriptInterface() {
        //第一步 设置javascript 可用,支持js让java和js交互或者本身希望js完成一定的功能
        mWebView.getSettings().setJavaScriptEnabled(true);

        //第三步 添加javaScript交互接口到webview
        MyJavaScriptInterface myJavaScriptInterface = new MyJavaScriptInterface() {
            @JavascriptInterface//适配

            public void showToast() {
                Logs.e("showToast");
                startActivity(new Intent(MainActivity.this, Come_Activity.class));

            }
            @JavascriptInterface
            //这里返回的参数作为shouldOverrideUrlLoading处使用
            public String htmlToNativeToJs() {
                Logs.e("htmlToNativeToJs");
//                return "http://192.168.5.10/WebRoot/胡歌.html";
                return "http://192.168.5.10/serv-app/story.html";
//                return "http://192.168.5.10/serv-app/basic/time";
            }
        };
        /**
         * WebView.addJavascriptInterface(Object,String)方法
         * Object 交互接口对象
         * String 交互接口名称
         */
        mWebView.addJavascriptInterface(myJavaScriptInterface, "MyJavaScriptInterface");

    }


    /**
     * 设置web端
     */

    public void setWebVieClient(){
        /**
         * 请求的url嵌入webview客户端显示
         * 指定只有url里包含langyabang的时候才在webview里打开，否则还是启动浏览器打开.
         */
        mWebView.setWebViewClient(new WebViewClient(){
           
            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, String url) {
//                webview.loadUrl(url);
                if ( url.contains("langyabang") == true){
                    mWebView.loadUrl(url);
                    return true;
                }else{
                    Logs.e("Intent>>>url"+url);
                    Intent in = new Intent (Intent.ACTION_VIEW , Uri.parse(url));
                    startActivity(in);
                    return true;
                }
//                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });
        // TODO: 2016/8/7 设定webview浏览器端
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Logs.e("onProgressChanged :"+newProgress);
                mProgressBar.setProgress(newProgress);
                mProgressBar.setVisibility(View.VISIBLE);
                if(newProgress == 100){
                    //此设定是保证加载完成，进度条显示消失
//                    mProgressBar.setVisibility(newProgress);
                    mProgressBar.setVisibility(View.GONE);
                }

            }
        });

    }



    /**
     * webView加载数据三种方式
     * assets 里面不能放汉字的名称
     */
    public void loadData() {
//      mWebView.loadUrl("http://192.168.5.5:8080/webapp/login.html");
//        mWebView.loadUrl("file:///android_asset/story.html");
        mWebView.loadUrl("http://192.168.5.10/serv-app/langyabang.html");
//      mWebView.loadDataWithBaseURL(null,"<html><body><h1>你好 loadDataWithBaswUrl</h1></body></html>","text/html","UTF-8",null);
    }



}
