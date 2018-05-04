package bodyfriend.com.customerapp.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsStore {


    // 푸시
    public static final String PREF_IS_PUSH_USING = "PREF_IS_PUSH_USING";

    private static final String TAG = "SettingsStore";
    private static final String MSG_TOKEN = "PREF_MSG_TOKEN";

    private final SharedPreferences mPrefs;
    private final Context mContext;

    private static SettingsStore mInstance;

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new SettingsStore(context);
        }
    }

    public static SettingsStore getInstance() {
        return mInstance;
    }

    private SettingsStore(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context.getApplicationContext();

    }

    public static void commit() {
        if (mInstance != null) {
            boolean result = mInstance.mPrefs.edit().commit();
//      Log.i("SettingsStore", "commit=" + result);
        }
    }

    public void reset() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();
    }

    public SharedPreferences getPreferences() {
        return mPrefs;
    }


    public String getMsgToken() {
        return mPrefs.getString(MSG_TOKEN, "");
    }

    public void putMsgToken(String token) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(MSG_TOKEN, token);
        editor.apply();
    }

}

