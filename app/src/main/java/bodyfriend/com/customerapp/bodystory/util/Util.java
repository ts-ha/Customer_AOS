package bodyfriend.com.customerapp.bodystory.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;

/**
 * Created by Rell on 2017. 11. 15..
 */

public class Util {
    /**
     * 픽셀을 DP 로 변환하는 메소드.
     *
     * @param px 픽셀
     * @return 픽셀에서 dp 로 변환된 값.
     */
    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    /**
     * DP 를 픽셀로 변환하는 메소드.
     *
     * @param dp dp
     * @return dp 에서 변환된 픽셀 값.
     */
    public static int dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static Pair<Integer, Integer> getScreenSize(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new Pair<>(size.x, size.y);
    }
}
