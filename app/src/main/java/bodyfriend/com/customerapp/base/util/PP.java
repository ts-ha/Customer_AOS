package bodyfriend.com.customerapp.base.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * <pre>
 * 아래와 같이 사용하세요
 * 변수 타입을 혼용하여 사용하면 죽음!
 * PP.sample.is();
 * PP.sample.set(true);
 *
 * PP.sample.getInt();
 * PP.sample.set(1);
 *
 * PP.sample.getLong();
 * PP.sample.set(1L);
 *
 * PP.sample.get();
 * PP.sample.getString();
 * PP.sample.set(&quot;text&quot;);
 *
 * </pre>
 */
public enum PP {

    AUTO_LOGIN
    , LOGIN_ID
    , LOGIN_PW
    , ALARM
    , SID
    // 아래부터 바디이야기
    , ID // 로그인 아이디
    , NO // 방번호
    , fcmToken
    ;

    private static final String DEFVALUE_STRING = "";
    private static final float DEFVALUE_FLOAT = -1f;
    private static final int DEFVALUE_INT = -1;
    private static final long DEFVALUE_LONG = -1L;
    private static final boolean DEFVALUE_BOOLEAN = false;

    public static void CREATE(Context context) {
        PREFERENCES = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences PREFERENCES;

    private String s() {
        return name();
    }

    public void set(boolean v) {
        PREFERENCES.edit().putBoolean(s(), v).commit();
    }

    public void set(int v) {
        PREFERENCES.edit().putInt(s(), v).commit();
    }

    public void set(long v) {
        PREFERENCES.edit().putLong(s(), v).commit();
    }

    public void set(float v) {
        PREFERENCES.edit().putFloat(s(), v).commit();
    }

    public void set(String v) {
        PREFERENCES.edit().putString(s(), v).commit();
    }

    public void set(Set<String> v) {
        PREFERENCES.edit().putStringSet(s(), v).commit();
    }

    public void toggle() {
        set(!is());
    }

    public boolean is() {
        return PREFERENCES.getBoolean(s(), DEFVALUE_BOOLEAN);
    }

    public boolean is(boolean DEFVALUE_BOOLEAN) {
        return PREFERENCES.getBoolean(s(), DEFVALUE_BOOLEAN);
    }

    public int getInt() {
        return PREFERENCES.getInt(s(), DEFVALUE_INT);
    }

    public int getInt(int DEFVALUE_INT) {
        return PREFERENCES.getInt(s(), DEFVALUE_INT);
    }

    public long getLong() {
        return PREFERENCES.getLong(s(), DEFVALUE_LONG);
    }

    public long getLong(long DEFVALUE_LONG) {
        return PREFERENCES.getLong(s(), DEFVALUE_LONG);
    }

    public float getFloat() {
        return PREFERENCES.getFloat(s(), DEFVALUE_FLOAT);
    }

    public float getFloat(float DEFVALUE_FLOAT) {
        return PREFERENCES.getFloat(s(), DEFVALUE_FLOAT);
    }

    public String get() {
        return PREFERENCES.getString(s(), DEFVALUE_STRING);
    }

    public String get(String DEFVALUE_STRING) {
        return PREFERENCES.getString(s(), DEFVALUE_STRING);
    }

    public String getString() {
        return PREFERENCES.getString(s(), DEFVALUE_STRING);
    }

    public String getString(String DEFVALUE_STRING) {
        return PREFERENCES.getString(s(), DEFVALUE_STRING);
    }

    public Set<String> getStringSet() {
        return PREFERENCES.getStringSet(s(), null);
    }

    public Set<String> getStringSet(Set<String> DEFVALUE_SET) {
        return PREFERENCES.getStringSet(s(), DEFVALUE_SET);
    }

    public boolean remove() {
        return PREFERENCES.edit().remove(s()).commit();
    }

    public boolean contain() {
        return PREFERENCES.contains(s());
    }

    public static boolean clear() {
        return PREFERENCES.edit().clear().commit();
    }
}
