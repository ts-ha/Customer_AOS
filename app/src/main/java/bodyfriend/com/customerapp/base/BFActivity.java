package bodyfriend.com.customerapp.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.common.BaseActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.image.AUIL;
import android.miscellaneous.Log;
import android.net.Net;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.karrel.mylibrary.RLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import bodyfriend.com.customerapp.base.util.OH;

public class BFActivity extends BaseActivity implements Observer {

    private int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    protected ProgressDialog mWebProgressDlg;

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setRequestedOrientation(orientation);
        super.onCreate(savedInstanceState);

        OH.c().addObserver(this);

        handler = new Handler(callback);
        mHandler = new Handler(mCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OH.c().deleteObserver(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        debugSet();
        parseExtra();
        loadOnce();
        reload();
        updateUI();
    }

    /**
     * //dj 중요함수임 꼭 달아줘야 함
     */
    private void debugSet() {
    }

    /**
     * onParseExtra()를 호출한다.
     */
    final public void parseExtra() {
        try {
            onParseExtra();
        } catch (Exception e) {
        }
    }

    /**
     * onLoadOnce()를 호출한다.
     */
    final public void loadOnce() {
        // Log.l("loadOnce", this);
        onLoadOnce();
    }

    /**
     * onReload()를 호출한다.
     */
    final public void reload() {
        onReload();
    }

    /**
     * onClear()를 호출한다.
     */
    final public void clear() {
        try {
            onClear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 통신 요청이 가능한 상태일때 onLoad()를 호출한다.
     */
    final public void load() {

//		if (isLoading)
//			return;

        try {
            onLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ui갱신작업이 필요할때 해당 메소드가 호출된다.
     */
    final public void updateUI() {
        try {
            onUpdateUI();
        } catch (Exception e) {
        }
    }

    /**
     * 해당 메소드에서 액티비티가 호출될때에 전달된 extra를 파싱한다.
     */
    protected void onParseExtra() {
    }

    /**
     * 액티비티가 실행되고 ui이 가능한 상태에 호출된다.<br>
     * ui작업과 인스턴스 생성등을 해당 메소드에서 한다.
     */
    protected void onLoadOnce() {
    }

    /**
     * 통신이 재요청될때 호출된다.
     */
    protected void onReload() {
        clear();
        load();
    }

    /**
     * 요청된 통신으로 받은 데이터를 clear해줘야 한다.
     */
    protected void onClear() {
    }

    /**
     * 해당 메소드내에서 화면에 출력할 전문을 요청한다.
     */
    protected void onLoad() {
    }

    /**
     * 주로 전문 요청이 완료된 이후에 호출되며, 해당 메소드 내에서 전문요청 후 수신된 값을 ui에 출력한다.
     */
    protected void onUpdateUI() {
    }

    /**
     * 주로 전문 요청시 필요한 전제조건들이 만족하는지에 대한 여부를 판단하는 메소드로 사용된다.
     *
     * @return 전문 요청 가능여부
     */
    public boolean check() {
        return true;
    }

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void showProgress() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);
        else
            super.showProgress();

    }

    @Override
    public void hideProgress() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
        else
            super.hideProgress();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 해당 플래그먼트를 리로드한다.
     *
     * @return true: 리로드 성공, false: 해당 플래그먼트가 없다.
     */
    protected boolean reloadFragment(String tag) {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            return false;
        } else {

            return true;
        }
    }

    private Class<?>[] mReloadType;

    @Override
    public void update(Observable observable, Object data) {
        if (OH.c() != observable)
            return;

        if (data instanceof OH.TYPE) {
            OH.TYPE type = (OH.TYPE) data;
            switch (type) {
                case EXIT:
                    finish();
                    break;
            }
        }

    }

    /**
     * 종료<br>
     * 호출시 종료된다는 노티를 옵저버를 통해서 보냄. <br>
     * 다른곳에서 EXIT가 발생할 시 해야할 일이 있으면 옵저버를 등록해서 처리함.
     */
    protected void exit() {
        OH.c().notifyObservers(OH.TYPE.EXIT);
    }

    @SuppressLint("CommitTransaction")
    public boolean removeFragment(String tag) {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment != null && fragment.isVisible()) {
            fragmentTransaction.remove(fragment).commit();

            for (int i = 0; i < arrFragementTag.size(); i++) {
                if (arrFragementTag.get(i).equals(tag)) {
                    arrFragementTag.remove(i);
                    break;
                }
            }

            return true;
        }
        return false;
    }

    protected boolean isShowFragment(String tag) {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        return fragment != null && fragment.isVisible();
    }

    private ArrayList<String> arrFragementTag = new ArrayList<String>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RLog.e("BFActivity :: showFinishPopup");
    }

    protected boolean isNotNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return false;
            }
        }
        return true;
    }

    public void toast(Object foramt, Object... args) {
        String text;
        if (foramt instanceof Integer)
            text = mContext.getString((Integer) foramt, args);
        else if (foramt instanceof String)
            text = String.format((String) foramt, args);
        else
            text = foramt.toString();

        Message message = new Message();
        message.what = 0;
        message.obj = text;
        handler.sendMessage(message);
    }

    private Handler handler;
    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast toast = Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    break;
            }
            return false;
        }
    };


    /**
     * AUIL
     */
    protected void setImage(int resid, String image) {
        setImage(resid, image, AUIL.options);
    }

    protected void setImage(int resid, String image, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(image, (ImageView) findViewById(resid), options);
    }

    protected static void setImage(ImageView imageview, String image, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(image, imageview, options);
    }

    protected static void setImage(ImageView imageview, String image) {
        ImageLoader.getInstance().displayImage(image, imageview, AUIL.options);
    }

    protected final boolean checkAndRequestPermissions(int requestCode, String... permissions) {
        ArrayList<String> strings = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                strings.add(permission);
            }
        }

        if (strings.isEmpty()) {
            return false;
        } else {
            String[] strings1 = new String[strings.size()];
            strings.toArray(strings1);
            ActivityCompat.requestPermissions(this, strings1, requestCode);

            return true;
        }
    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    protected final boolean isEmpty(Object text) {
        if (text == null)
            return true;
        if (text instanceof String) {
            if (((String) text).trim().length() == 0)
                return true;
        }
        return false;
    }

    private Handler mHandler;

    private final int WHAT_SHOW_PROGRESS = 0;
    private final int WHAT_HIDE_PROGRESS = 1;

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SHOW_PROGRESS:
                    showProgress();
                    break;
                case WHAT_HIDE_PROGRESS:
                    hideProgress();
                    break;
            }
            return false;
        }
    };

    protected void setNetListener(int resId, final BFEnty enty, final Net.OnNetResponse onNetResponse) {
        findViewById(resId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Net.async(enty).setOnNetResponse(onNetResponse);
            }
        });
    }

    public void showWebProgress() {
        if (mWebProgressDlg != null && mWebProgressDlg.isShowing()) {
            return;
        }
        createWebProgressDialog();
        mWebProgressDlg.show();
    }

    protected void createWebProgressDialog() {
        mWebProgressDlg = new BFProgressDlg(mContext, bodyfriend.com.customerapp.R.style.AppProgressTheme);

    }

    public void hideWebProgress() {
        removeWebProgressDialog();
    }

    protected void removeWebProgressDialog() {
        if (mWebProgressDlg != null)
            mWebProgressDlg.cancel();
    }

    @Override
    protected void createProgressDialog() {
        mProgressDlg = new BFProgressDlg(mContext, bodyfriend.com.customerapp.R.style.AppProgressTheme);

    }
}
