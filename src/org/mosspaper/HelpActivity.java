package org.mosspaper;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.view.KeyEvent;

public class HelpActivity extends Activity  {
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.act_help);

        webview = (WebView) findViewById(R.id.help_webview);
        webview.loadUrl("file:///android_asset/help.html");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack())) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private WebView webview;
}
