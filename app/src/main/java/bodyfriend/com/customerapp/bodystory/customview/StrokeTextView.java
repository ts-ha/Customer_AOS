package bodyfriend.com.customerapp.bodystory.customview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by Rell on 2017. 10. 30..
 */

public class StrokeTextView extends android.support.v7.widget.AppCompatTextView {

    private boolean isStroke = true;
    private float strokeWidth = 8.0f;
    private int strokeColor = Color.parseColor("#FC8736");

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public StrokeTextView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isStroke) {
            ColorStateList states = getTextColors();
            getPaint().setStyle(Paint.Style.STROKE);
            getPaint().setStrokeWidth(strokeWidth);
            setTextColor(strokeColor);
            super.onDraw(canvas);

            getPaint().setStyle(Paint.Style.FILL);
            setTextColor(states);
        }

        super.onDraw(canvas);
    }

    public void setStroke(boolean stroke) {
        isStroke = stroke;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
