package bodyfriend.com.customerapp.bodystory.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by 이주영 on 2016-10-07.
 */
public class CustomerWebCromeClient extends WebChromeClient {

    public interface OnDialogListener {
        Dialog showDialog(Object message, Object positiveButtonText, DialogInterface.OnClickListener positiveListener);

        Dialog showDialog(Object message,//
                          Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
                , Object negativeButtonText, DialogInterface.OnClickListener negativeListener);
    }

    private OnDialogListener mOnDialogListener;


    public CustomerWebCromeClient(OnDialogListener mOnDialogListener) {
        this.mOnDialogListener = mOnDialogListener;
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

        Dialog dialog = mOnDialogListener.showDialog(message, "확인", (dialog1, which) -> result.confirm());
        if (dialog != null) dialog.setCancelable(false);

        return dialog != null;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        Log.d("TAG", String.format("onJsAlert(WebView view, %s, %s, JsResult result)", url, message));

        Dialog dialog = mOnDialogListener.showDialog(message
                , "확인", (dialog1, which) -> result.confirm()
                , "취소", (dialog12, which) -> result.cancel()
        );
        if (dialog != null) dialog.setCancelable(false);

        return dialog != null;
    }
}