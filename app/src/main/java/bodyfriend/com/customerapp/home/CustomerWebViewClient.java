package bodyfriend.com.customerapp.home;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.miscellaneous.Log;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import bodyfriend.com.customerapp.R;
import bodyfriend.com.customerapp.base.NetConst;

/**
 * Created by 이주영 on 2016-09-27.
 */
public class CustomerWebViewClient extends WebViewClient implements OnBackPageListener {
    private static final String TAG = "CustomerWebViewClient";
    private String mPageFinishedUrl;


    public interface onLoginPageListener {
        void onLogin(String pageStartUrl);

        void onLogout();
    }

    private OnLoadUrlListener onLoadUrlListener;

    public void setOnLoadUrlListener(OnLoadUrlListener onLoadUrlListener) {
        this.onLoadUrlListener = onLoadUrlListener;
    }

    private Activity mActivity;

    public CustomerWebViewClient(Activity activity) {
        super();
        mActivity = activity;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

        android.util.Log.e(TAG, "onReceivedError: " + String.format("errorCode : %d, description : %s, failingUrl : %s", error.getErrorCode(), error.getDescription(), request.getUrl()));
        super.onReceivedError(view, request, error);
    }

    // 오류가 났을 경우, 오류는 복수할 수 없음
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);

        android.util.Log.e(TAG, "onReceivedError: " + String.format("errorCode : %d, description : %s, failingUrl : %s", errorCode, description, failingUrl));

        switch (errorCode) {
            case ERROR_AUTHENTICATION:
                break;               // 서버에서 사용자 인증 실패
            case ERROR_BAD_URL:
                break;                           // 잘못된 URL
            case ERROR_CONNECT:
                break;                          // 서버로 연결 실패
            case ERROR_FAILED_SSL_HANDSHAKE:
                break;    // SSL handshake 수행 실패
            case ERROR_FILE:
                break;                                  // 일반 파일 오류
            case ERROR_FILE_NOT_FOUND:
                break;               // 파일을 찾을 수 없습니다
            case ERROR_HOST_LOOKUP:
                break;           // 서버 또는 프록시 호스트 이름 조회 실패
            case ERROR_IO:
                break;                              // 서버에서 읽거나 서버로 쓰기 실패
            case ERROR_PROXY_AUTHENTICATION:
                break;   // 프록시에서 사용자 인증 실패
            case ERROR_REDIRECT_LOOP:
                break;               // 너무 많은 리디렉션
            case ERROR_TIMEOUT:
                break;                          // 연결 시간 초과
            case ERROR_TOO_MANY_REQUESTS:
                break;     // 페이지 로드중 너무 많은 요청 발생
            case ERROR_UNKNOWN:
                break;                        // 일반 오류
            case ERROR_UNSUPPORTED_AUTH_SCHEME:
                break; // 지원되지 않는 인증 체계
            case ERROR_UNSUPPORTED_SCHEME:
                break;          // URI가 지원되지 않는 방식
        }

    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity.getApplication().getApplicationContext());
        builder.setMessage(R.string.notification_error_ssl_cert_invalid);
        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private onLoginPageListener onLoginPageListener;

    public void setOnLoginPageListener(CustomerWebViewClient.onLoginPageListener onLoginPageListener) {
        this.onLoginPageListener = onLoginPageListener;
    }


    private String pageStartUrl;

    // 로딩이 시작될 때
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d("onPageStarted :: " + url);

        if (url.endsWith("login.view")) {
            if (onLoginPageListener != null) onLoginPageListener.onLogin(pageStartUrl);
        } else if (url.endsWith("logout")) {
            if (onLoginPageListener != null) onLoginPageListener.onLogout();
        } else {
            pageStartUrl = url;
        }

        super.onPageStarted(view, url, favicon);
    }

    // 리소스를 로드하는 중 여러번 호출
    @Override
    public void onLoadResource(WebView view, String url) {
//        Log.d("onLoadResource :: " + url);
        super.onLoadResource(view, url);
    }

    // 방문 내역을 히스토리에 업데이트 할 때
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    // 로딩이 완료됬을 때 한번 호출
    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d("onPageFinished :: url => " + url);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.getInstance().sync();
        } else {
            // 롤리팝 이상에서는 CookieManager의 flush를 하도록 변경됨.
            CookieManager.getInstance().flush();
        }

        // 마지막 로딩된 url를 저장한다.
        mPageFinishedUrl = url;

    }

    @Override
    public boolean onBack() {
        Log.d(":: onBack");
        // 미자막 페이지가 brand/list.view가 포함된 상태에서 Back을 누르면 true를 리턴한다.
        if (mPageFinishedUrl.contains("brand/list.view")) {
            if (onLoadUrlListener != null) {
                // onLoadUrlListener를 통해 원하는 url을 로드한다.
                onLoadUrlListener.onLoadUrl(NetConst.host + "/home/view/home");
            }
            return true;
        }

        if (mPageFinishedUrl.contains("/order/complete")) {
            if (onLoadUrlListener != null) {
                // onLoadUrlListener를 통해 원하는 url을 로드한다.
                onLoadUrlListener.onLoadUrl(NetConst.host + "/brand/list.view?page=new");
            }
            return true;
        }

        return false;
    }

    // http 인증 요청이 있는 경우, 기본 동작은 요청 취소
    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    // 확대나 크기 등의 변화가 있는 경우
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
    }

    // 잘못된 키 입력이 있는 경우
    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.e("shouldOverrideUrlLoading1 => " + url);
        if (checkPay(url)) return true;
        return super.shouldOverrideUrlLoading(view, url);
    }

    // 새로운 URL이 webview에 로드되려 할 경우 컨트롤을 대신할 기회를 줌
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e("shouldOverrideUrlLoading2 => " + request.getUrl());
            if (checkPay(request.getUrl().toString())) return true;
        }
        return false;
    }

    public static final String INTENT_PROTOCOL_START = "intent";
    public static final String INTENT_PROTOCOL_END = ";end";
    public static final String INTENT_PROTOCOL_PACKAGE = ";package=";
    public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";
    public static final String INTENT_MARKET = "market://";
    public static final String INTENT_ISPMOBILE = "ispmobile://";

    public static final String CHECK_MVACCINE = "intent://mvaccine";

    private boolean checkPay(final String url) {

        if (url.startsWith(INTENT_PROTOCOL_START)) {
            startApp(url);
            return true;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return false;
        } else {
            startApp(url);
            return true;
        }
    }

    private void startActivity(Intent intent) {
        mActivity.startActivity(intent);
    }

    /**
     * 다른 패키지명의 앱을 실행한다
     */
    private void startApp(final String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

            // 호출대상의 패키지명을 가져온다
            String pName = intent.getPackage();
            // 호출대상이 설치되어있는지 확인한다
            if (isInstalled(intent.getPackage())) {
                // 설치되어있으면 실행한다
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    startActivity(intent);
                } else {
                    // 킷캣보다 낮은버전이면 다른 인텐트를 만들어서 호출한다
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            } else {
                // 앱이 설치되어있지않으면 마켓으로 이동한다
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + pName));
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isInstalled(String pName) {
        try {

            PackageManager pm = mActivity.getPackageManager();
            pm.getPackageInfo(pName.trim(), PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
