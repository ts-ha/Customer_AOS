package bodyfriend.com.customerapp.bodystory.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import bodyfriend.com.customerapp.R;


/**
 * Created by Rell on 2017. 10. 31..
 */

public class NiddleView extends View {
    public long maxSection = 400;
    public int curSection = 100;

    public interface OnNiddleChangeListener {
        void onChange(int y);
    }

    private OnNiddleChangeListener onNiddleChangeListener;

    public void setOnNiddleChangeListener(OnNiddleChangeListener onNiddleChangeListener) {
        this.onNiddleChangeListener = onNiddleChangeListener;
    }

    public NiddleView(Context context) {
        super(context);
        init();
    }

    public NiddleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NiddleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
    }

    public void setPoint(int point) {
        curSection = point;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img_niddle);
        final float ropeHeight = getHeight() - bmp.getHeight();

        int newRopeHeight = (int) (((double) ropeHeight / (double) maxSection) * (double) curSection);
//        RLog.e(String.format("%s = %s / %s * %s", newRopeHeight, ropeHeight, maxSection, curSection));

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14.2f, getContext().getResources().getDisplayMetrics());

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));
//        RLog.d("newRopeHeight : " + newRopeHeight);
        canvas.drawBitmap(bmp, getWidth() - bmp.getWidth(), newRopeHeight, paint);
        paint.setStrokeWidth(4);
//        paint.setStrokeWidth(Util.dpToPx(getContext(), 1.33f));
        float startX = getWidth() - px + 2;
        float startY = 0;
        float stopX = startX;
        float stopY = newRopeHeight;
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        if (onNiddleChangeListener != null) onNiddleChangeListener.onChange(newRopeHeight);
    }

    public void setMaxSection(long maxSection) {
        this.maxSection = maxSection;
    }
}
