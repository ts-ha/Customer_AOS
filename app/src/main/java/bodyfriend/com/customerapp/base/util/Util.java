package bodyfriend.com.customerapp.base.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Point;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.view.Display;

import java.text.DecimalFormat;

public class Util {
    /**
     * This method converts dp unit to equivalent pixels, depending on device
     * density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need
     *                to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on
     * device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent
     * pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static final String getDeviceId(Context c) {
        String androID = Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
        return androID;
    }

    public static int getAppVersion(Context c) {
        try {
            PackageInfo packageInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static double getDistance(int x0, int y0, int x1, int y1) {
        return Math.sqrt(Math.pow(Math.abs(x1 - x0), 2) + Math.pow(Math.abs(y1 - y0), 2));
    }

    public static double getAngle(int x0, int y0, float x1, float y1) {
        int dx = (int) (x1 - x0);
        int dy = (int) (y1 - y0);

        double rad = Math.atan2(dx, dy);
        double degree = (rad * 180) / Math.PI;

        if (degree < 0) {
            degree = Math.abs(degree) + 180;
        } else {
            degree = 180 - degree;
        }

        return degree;
    }

    public static String getComma(String str) {
        if (str.length() == 0)
            return "";
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }

}
