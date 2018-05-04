package bodyfriend.com.customerapp.base;

import android.common.BaseApplication;
import android.content.Context;
import android.image.AUIL;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import bodyfriend.com.customerapp.setting.SettingsStore;
import bodyfriend.com.customerapp.base.util.PP;

public class BFApplication extends BaseApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        AUIL.CREATE(context);
        PP.CREATE(context);
        SettingsStore.init(context);


        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        cookieManager.setAcceptCookie(true);
    }
}
