package bodyfriend.com.customerapp.bodystory.util;

import android.webkit.JavascriptInterface;

import com.karrel.mylibrary.RLog;

/**
 * Created by 이주영 on 2017-02-06.
 */

public class AndroidBridge {

    public interface BridgeListener {
        void onConnect(String id, String no);
    }

    private BridgeListener mBridgeListener;

    public AndroidBridge(BridgeListener mBridgeListener) {
        this.mBridgeListener = mBridgeListener;
    }

    @JavascriptInterface
    public void getInfo(String id, String no) {
        mBridgeListener.onConnect(id, no);
        RLog.e(String.format("getInfo(%s, %s)", id, no));
    }
}
