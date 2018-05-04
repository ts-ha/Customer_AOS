package bodyfriend.com.customerapp.home.util;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.miscellaneous.Log;
import android.net.Net;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.webkit.JavascriptInterface;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karrel.mylibrary.RLog;

import bodyfriend.com.customerapp.base.BFActivity;
import bodyfriend.com.customerapp.base.NetConst;
import bodyfriend.com.customerapp.base.util.PP;
import bodyfriend.com.customerapp.home.OnLoadUrlListener;
import bodyfriend.com.customerapp.home.net.InsertDeviceToken;
import bodyfriend.com.customerapp.setting.SettingsStore;

/**
 * Created by 이주영 on 2017-02-06.
 */

public class AndroidBridge {
    private BFActivity mBFActivity;

    public interface BridgeListener {
        void onConnect(String id, String no);
    }

    private BridgeListener mBridgeListener;


    private OnLoadUrlListener onLoadUrlListener;

    public void setOnLoadUrlListener(OnLoadUrlListener onLoadUrlListener) {
        this.onLoadUrlListener = onLoadUrlListener;
    }

    public AndroidBridge(BFActivity a, BridgeListener mBridgeListener) {
        mBFActivity = a;
        this.mBridgeListener = mBridgeListener;
    }

    private void showDialog(final String str) {
        mBFActivity.showDialog(str);
    }

    @JavascriptInterface
    public void showPdf(String url) {
        Log.d("showPdf *******************");
        Log.d("url => " + url);

        PdfDownloader.newInstance(mBFActivity).downloadPdf(NetConst.host + url);
    }

    // 서버에서 로그인 정보를 로드한다.
    @JavascriptInterface
    public void loadLoginInfo() {
        Log.d(":: loadLoginInfo");
        String id = PP.LOGIN_ID.get();
        String pw = PP.LOGIN_PW.get();

        if (id == null || id.isEmpty()) return;
        if (pw == null || pw.isEmpty()) return;

        final String resultLoginInfo = String.format("javascript:resultLoginInfo('%s', '%s')", id, pw);
        Log.d("resultLoginInfo -> " + resultLoginInfo);
        loadUrl(resultLoginInfo);

        insertDeviceToken(id);
    }

    private void insertDeviceToken(String id) {
        Log.d(" SettingsStore.getInstance().getMsgToken() : " +  SettingsStore.getInstance().getMsgToken());
        Net.async(new InsertDeviceToken(id, SettingsStore.getInstance().getMsgToken()), new Net.OnNetResponse<InsertDeviceToken>() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onErrorResponse ~~~~~~~~~~~~~~~~~~~~~~~~");
                Log.d("error.toString() : " + error.toString());
            }

            @Override
            public void onResponse(InsertDeviceToken response) {
                Log.d("response ~~~~~~~~~~~~~~~~~~~~~~~~");
            }
        });
    }

    @JavascriptInterface
    public void logout() {
        PP.LOGIN_ID.set("");
        PP.LOGIN_PW.set("");
    }



    @JavascriptInterface
    public void delPushType(String type) {
        Log.d("delPushType : " + type);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(type);
    }

    @JavascriptInterface
    public void setPushType(String type) {
        Log.d("setPushType : " + type);
        FirebaseMessaging.getInstance().subscribeToTopic(type);
    }


    /**
     * 서버의 자바 스크립트에 정의된 펑션을 호출하기 위해 만들었다.
     *
     * @param url
     */
    private void loadUrl(String url) {
        Message message = new Message();
        message.obj = url;
        if (onLoadUrlListener != null) onLoadUrlListener.onLoadUrl(url);
    }


    @JavascriptInterface
    public void loginSuccess(String id, String pw) {
        Log.d("loginSuccess~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        PP.LOGIN_ID.set(id);
        PP.LOGIN_PW.set(pw);
        Log.d(String.format(":: loginSuccess(%s, %s)", id, pw));

        final String resultLoginInfo = String.format("javascript:resultLoginInfo('%s', '%s')", id, pw);
        Log.d("resultLoginInfo -> " + resultLoginInfo);
        loadUrl(resultLoginInfo);

        insertDeviceToken(id);
    }


    @JavascriptInterface
    public void goMobileWeb(String url) {
        Log.d("url : " + url);
        url = Uri.decode(url);
        String bkpUrl = null;

        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mBFActivity.startActivity(myIntent);

    }


    @JavascriptInterface
    public void loginFail() {
        Log.d("loginFail");
    }

    @JavascriptInterface
    public void startUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.android.chrome");
        mBFActivity.startActivity(intent);
    }

    /**
     * 전화 걸기
     */
    @JavascriptInterface
    public void call(final String callName, final String callNum) {
        Log.d(String.format("call(%s, %s)", callName, callNum));

        DialogInterface.OnClickListener onPositiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mBFActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mBFActivity
                                , new String[]{Manifest.permission.CALL_PHONE}
                                , 1234);
                        return;
                    }
                }

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNum));
                mBFActivity.startActivity(intent);
            }
        };
        mBFActivity.showDialog(String.format("%s 로 전화하시겠습니까?\n(%s)", callName, callNum), "전화하기", onPositiveListener, "취소", null);
    }

    @JavascriptInterface
    public void getDeviceId() {
        Log.d("getDeviceId : ");
        String token = SettingsStore.getInstance().getMsgToken();
        Log.d("token : " + token);
        final String resultLoginInfo = String.format("javascript:resultDeviceId('%s')", token);
        Log.d("resultLoginInfo -> " + resultLoginInfo);
        loadUrl(resultLoginInfo);
    }


    @JavascriptInterface
    public void showGuide() {
        Log.d("showGuide()");
//            startActivity(new Intent(mContext, GuideActivity.class));
    }

    @JavascriptInterface
    public void getInfo(String id, String no) {
        mBridgeListener.onConnect(id, no);
        RLog.e(String.format("getInfo(%s, %s)", id, no));
    }
}
