package bodyfriend.com.customerapp.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.miscellaneous.Log;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.karrel.mylibrary.RLog;

import java.util.ArrayList;
import java.util.Observable;

import bodyfriend.com.customerapp.R;
import bodyfriend.com.customerapp.base.BFDialog;
import bodyfriend.com.customerapp.base.util.PP;
import bodyfriend.com.customerapp.bodystory.presenter.GameStartPresenter;
import bodyfriend.com.customerapp.bodystory.presenter.GameStartPresenterImpl;
import bodyfriend.com.customerapp.bodystory.presenter.WebPresenter;
import bodyfriend.com.customerapp.bodystory.presenter.WebPresenterImpl;
import bodyfriend.com.customerapp.bodystory.view.GameActivity;
import bodyfriend.com.customerapp.bodystory.view.SocketConnectActivity;
import bodyfriend.com.customerapp.setting.SettingsStore;
import bodyfriend.com.customerapp.base.BFActivity;
import bodyfriend.com.customerapp.base.NetConst;
import bodyfriend.com.customerapp.base.util.OH;
import bodyfriend.com.customerapp.home.util.AndroidBridge;
import bodyfriend.com.customerapp.login.intro.IntroActivity;

public class HomeActivity extends BFActivity implements WebPresenter.View, bodyfriend.com.customerapp.bodystory.util.AndroidBridge.BridgeListener, bodyfriend.com.customerapp.bodystory.util.CustomerWebCromeClient.OnDialogListener, GameStartPresenter.View, AndroidBridge.BridgeListener {

    private static final int CHECK_PERMISSIONS = 1000;

    private WebView mWebView;
    private String mStartPageUrl = NetConst.host;
    private String mCallNum;
    private OnBackPageListener mCustomerWebViewClient;

    private static final int CONNECT_CODE = 1001;

    private WebPresenter presenter;
    private GameStartPresenter gameStartPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        startActivity(new Intent(this, IntroActivity.class));
        presenter = new WebPresenterImpl(this);
        gameStartPresenter = new GameStartPresenterImpl(this);
    }

    @Override
    protected void onLoadOnce() {
        super.onLoadOnce();
        // 퍼미션 체크
        checkPermissions();
        // 웹뷰 초기화
        setupWebView();

    }

    /**
     * 웹뷰 초기화
     */
    private void setupWebView() {
        mWebView = (WebView) findViewById(R.id.web);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // 안드로이드 브릿지를 세팅한다.
        AndroidBridge bridge = new AndroidBridge(HomeActivity.this, this);
        bridge.setOnLoadUrlListener(onLoadUrlListener);
        mWebView.addJavascriptInterface(bridge, "Android");

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        CustomerWebViewClient customerWebViewClient = new CustomerWebViewClient(this);
        customerWebViewClient.setOnLoadUrlListener(onLoadUrlListener);
        mCustomerWebViewClient = customerWebViewClient;
        mWebView.setWebViewClient(customerWebViewClient);
        mWebView.setWebChromeClient(new CustomerWebCromeClient(this, onWebProgressListener));

        // local storage enable
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mWebView.getSettings().setDatabasePath("/data/data/" + mWebView.getContext().getPackageName() + "/databases/");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        } else {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
        }

        mWebView.loadUrl(NetConst.host);
        Log.d("NetConst.host : " + NetConst.host);


        FirebaseMessaging.getInstance().subscribeToTopic("customer_notice");
        FirebaseMessaging.getInstance().subscribeToTopic("customer_event");
        FirebaseMessaging.getInstance().subscribeToTopic("customer_pay");
        // [END subscribe_topics]

//        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");

        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        String msg = getString(R.string.msg_token_fmt, token);
        Log.d("msg : " + msg);

        SettingsStore instance = SettingsStore.getInstance();
        instance.putMsgToken(token);
//        if(!instance.getMsgToken().equals(token)){
//            instance.putMsgToken(token);
//            //서버 등록
//        }

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 승인
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한이 거부되어 이용할 수 없습니다.
                toast("권한이 거부되어 이용할 수 없습니다.");
                finish();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.GET_ACCOUNTS)
                .check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (CONNECT_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            gameStartPresenter.connectOk();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        RLog.e("onKeyDown");

        // 웹뷰의 home화면이면 종료한다.
        final String url = mWebView.getUrl();

        RLog.d("onKeyDown url : " + url);
        if(url.contains("login") || url.contains("main")){
            presenter.showFinishPopup();
            return true;
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            if (mCustomerWebViewClient != null && mCustomerWebViewClient.onBack()) {
                // OnBackPageListener가 null이 아니고 onBack이 true를 리턴하면 mWebView.goBack();을 호출하지 않는다.
            } else {
                // 백키누르면 웹뷰 뒤로가기 호출
                if (
                        mWebView.getUrl().contains(NetConst.URL_BENEFIT_BENEFIT_MAIN) ||
                                mWebView.getUrl().contains(NetConst.URL_MYINFO_MAIN) ||
                                mWebView.getUrl().contains(NetConst.URL_PROMOTION_PROMOTION_MAIN)) {
                    showDialog("종료하시겠습니까?", "종료", (dialog, which) -> finish(), "취소", null);
                } else if (mWebView.getUrl().contains(NetConst.URL_PROMOTION_PROMOTION_LIST)) {
                    mWebView.loadUrl(NetConst.host + NetConst.URL_MYINFO_MAIN);
                } else {
                    mWebView.goBack();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void update(Observable observable, Object data) {
        super.update(observable, data);
        if (OH.c() != observable)
            return;

        if (data instanceof OH.TYPE) {
            OH.TYPE type = (OH.TYPE) data;
            switch (type) {
                case LOGIN:
                    mWebView.loadUrl(mStartPageUrl);
                    break;
            }
        }
    }


    /**
     * 퍼미션을 체크한다.
     */
    private boolean checkPermissions() {

        return checkAndRequestPermissions(CHECK_PERMISSIONS
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.CALL_PHONE);
    }

    private CustomerWebCromeClient.OnProgressChangeListener onWebProgressListener = new CustomerWebCromeClient.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(int newProgress) {

            if (!HomeActivity.this.isDestroyed()) {
                Log.d("isDestroyed");
                Log.d("newProgress  : " + newProgress);
                if (newProgress < 100) {
                    showWebProgress();
                } else {
                    hideWebProgress();
                }
            } else {
                Log.d("onProgressChanged");
            }

        }
    };

    /**
     * url을 로드한다.
     */
    private OnLoadUrlListener onLoadUrlListener = new OnLoadUrlListener() {
        @Override
        public void onLoadUrl(String url) {
            Log.d(String.format("HomeActivity.class :: onLoadUrl(%s)", url));
            Message message = new Message();
            message.obj = url;
            loadUrl.sendMessage(message);
        }
    };

    /**
     * @JavascriptInterface 로 정의된 메소드에서 WebView의 메소드를 호출할 시 쓰레드 관련 문제가 생기므로 핸들러를 통해 해당 메소드를 호출한다.
     */
    private Handler loadUrl = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d("msg.obj -> " + msg.obj);
            mWebView.loadUrl((String) msg.obj);
            return false;
        }
    });


    @Override
    public void onBackPressed() {
        RLog.e("HomeActivity :: showFinishPopup");

        RLog.d("mWebView.getUrl() : " + mWebView.getUrl());

        presenter.goBackWebPage();
    }


    @Override
    public void startGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);

    }

    @Override
    public void showSocketSettingPopup() {
        Intent intent = new Intent(this, SocketConnectActivity.class);
        startActivityForResult(intent, CONNECT_CODE);

    }

    @Override
    public void webGoBack() {
        mWebView.goBack();

    }

    @Override
    public void showFishDialog() {
        BFDialog.newInstance(this).showDialog("종료하시겠습니까?",
                "종료", (dialog, which) -> presenter.finish(),
                "취소", null).setCancelable(false);
    }

    @Override
    public void onConnect(String id, String no) {
        PP.ID.set(id);
        PP.NO.set(no);
        gameStartPresenter.clickStart();
    }

}
