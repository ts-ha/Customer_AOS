package bodyfriend.com.customerapp.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import bodyfriend.com.customerapp.base.BFActivity;

/**
 * Created by 이주영 on 2016-10-07.
 */
public class CustomerWebCromeClient extends WebChromeClient {


    public interface OnProgressChangeListener {
        void onProgressChanged(int newProgress);
    }

    private OnProgressChangeListener mOnProgressChangeListener;

    public CustomerWebCromeClient(OnProgressChangeListener listener) {
        mOnProgressChangeListener = listener;
    }

    private BFActivity mBFActivity;

    public CustomerWebCromeClient(BFActivity activity, OnProgressChangeListener onWebProgressListener) {
        this(onWebProgressListener);
        mBFActivity = activity;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        mOnProgressChangeListener.onProgressChanged(newProgress);
    }

    @Override
    public void onCloseWindow(WebView window) {
        Log.d("TAG", "onCloseWindow");
        super.onCloseWindow(window);
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        Log.d("TAG", "onCreateWindow");
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        Log.d("TAG", String.format("onJsAlert(WebView view, %s, %s, JsResult result)", url, message));

        Dialog dialog = mBFActivity.showDialog(message, "확인", (dialog1, which) -> result.confirm());
        if (dialog != null) dialog.setCancelable(false);

        return dialog != null;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        Log.d("TAG", String.format("onJsAlert(WebView view, %s, %s, JsResult result)", url, message));

        Dialog dialog = mBFActivity.showDialog(message
                , "확인", (dialog1, which) -> result.confirm()
                , "취소", (dialog12, which) -> result.cancel()
        );
        if (dialog != null) dialog.setCancelable(false);

        return dialog != null;
    }
}