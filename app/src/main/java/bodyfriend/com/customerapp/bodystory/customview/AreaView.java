package bodyfriend.com.customerapp.bodystory.customview;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by Rell on 2017. 11. 22..
 */

public class AreaView extends View {
    private double max, startValue, endValue;
    private View parent;
    private int bgColor = Color.parseColor("#809BDAEA");
    private int selectedBgColor = Color.parseColor("#80C72526");

    public AreaView(View parent, double max, double startValue, double endValue) {
        super(parent.getContext());

        this.max = max;
        this.startValue = startValue;
        this.endValue = endValue;
        this.parent = parent;

        init();
    }

    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                drawUi();
            }
        });

        // 2017. 11. 22. 색상설정
        setBackgroundColor();
    }

    private void setBackgroundColor() {
        setBackgroundColor(bgColor);
    }

    /**
     * ui를 그려라
     */
    private void drawUi() {
        // 2017. 11. 22. 최대값에 비례해서
        double parentHeight = parent.getHeight();

        // 한 포인트당 기준이되는 높이
        double standardValue = parentHeight / max;

        // 시작값 설정
        int startY = (int) (standardValue * startValue);
        int endY = (int) (standardValue * endValue);

        // 높이 설정
        final int height = endY - startY;

        // 레이아웃의 설정
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
        params.topMargin = startY;
        setLayoutParams(params);
    }

    /**
     * 선택영역에 찌가 이동했을 경우 선택영역의 색상을 변경한다.
     */
    public void updatePosition(int pointLocation) {
        boolean isSelected;
        if (startValue == endValue) {
            isSelected = pointLocation == startValue;
        } else {
            isSelected = startValue < pointLocation && pointLocation <= endValue;
        }

//        RLog.d(String.format("updatePosition :: pointLocation : %s, startValue : %s, endValue : %s", pointLocation, startValue, endValue));

        setBackgroundColor(isSelected ? selectedBgColor : bgColor);
    }
}
