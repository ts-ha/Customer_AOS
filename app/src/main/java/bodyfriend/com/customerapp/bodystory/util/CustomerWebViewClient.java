package bodyfriend.com.customerapp.bodystory.util;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by 이주영 on 2016-09-27.
 */
public class CustomerWebViewClient extends WebViewClient {


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

}
