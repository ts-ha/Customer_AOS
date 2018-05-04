package bodyfriend.com.customerapp.bodystory.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;

import com.karrel.mylibrary.RLog;

import bindservice.com.karrel.net.NetManager;
import bodyfriend.com.customerapp.R;
import bodyfriend.com.customerapp.base.BFDialog;
import bodyfriend.com.customerapp.base.util.PP;
import bodyfriend.com.customerapp.bodystory.presenter.GameStartPresenter;
import bodyfriend.com.customerapp.bodystory.presenter.GameStartPresenterImpl;
import bodyfriend.com.customerapp.bodystory.presenter.WebPresenter;
import bodyfriend.com.customerapp.bodystory.presenter.WebPresenterImpl;
import bodyfriend.com.customerapp.bodystory.util.AndroidBridge;
import bodyfriend.com.customerapp.bodystory.util.CustomerWebCromeClient;
import bodyfriend.com.customerapp.bodystory.util.CustomerWebViewClient;
import bodyfriend.com.customerapp.databinding.ActivityWebBinding;

public class WebActivity extends BaseActivity implements WebPresenter.View, AndroidBridge.BridgeListener, CustomerWebCromeClient.OnDialogListener, GameStartPresenter.View {

    private static final int CONNECT_CODE = 1001;

    private WebPresenter presenter;
    private ActivityWebBinding binding;
    private GameStartPresenter gameStartPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        presenter = new WebPresenterImpl(this);
        gameStartPresenter = new GameStartPresenterImpl(this);

        setupWebView();
        RLog.d("onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        RLog.d("onStart");
        binding.webView.reload();
    }

    private void setupWebView() {
        RLog.d("setupWebView");
        // 안드로이드 브릿지를 세팅한다.
        AndroidBridge bridge = new AndroidBridge(this);
        binding.webView.addJavascriptInterface(bridge, "Android");

        binding.webView.setWebViewClient(new CustomerWebViewClient());

        binding.webView.setWebChromeClient(new CustomerWebCromeClient(this));

        // local storage enable
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            binding.webView.getSettings().setDatabasePath("/data/data/" + binding.webView.getContext().getPackageName() + "/databases/");
        }

        WebSettings settings = binding.webView.getSettings();
        settings.setJavaScriptEnabled(true);


        binding.webView.loadUrl(String.format("%s/login/tempLogin", NetManager.url));
    }

    @Override
    public void onConnect(String id, String no) {
        PP.ID.set(id);
        PP.NO.set(no);
        gameStartPresenter.clickStart();
        RLog.d("onConnect");
    }

    @Override
    public Dialog showDialog(Object message, Object positiveButtonText, DialogInterface.OnClickListener positiveListener) {
        return BFDialog.newInstance(this).showDialog(message, positiveButtonText, positiveListener);
    }

    @Override
    public Dialog showDialog(Object message, Object positiveButtonText, DialogInterface.OnClickListener positiveListener, Object negativeButtonText, DialogInterface.OnClickListener negativeListener) {
        return BFDialog.newInstance(this).showDialog(message, positiveButtonText, positiveListener, negativeButtonText, negativeListener);
    }

    @Override
    public void onBackPressed() {
        presenter.goBackWebPage();
    }

    @Override
    public void webGoBack() {
        binding.webView.goBack();
    }

    @Override
    public void showFishDialog() {
        BFDialog.newInstance(this).showDialog("바디이야기를 종료하시겠습니까?",
                "종료", (dialog, which) -> presenter.finish(),
                "취소", null).setCancelable(false);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CONNECT_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            gameStartPresenter.connectOk();
        }
    }
}
