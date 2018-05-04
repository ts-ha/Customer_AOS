package bodyfriend.com.customerapp.login.intro;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Created by 이주영 on 2016-12-30.
 */

public class AnimationManager {

    public static AnimationManager newInstance() {

        return new AnimationManager();
    }

    public void startAlphaAnim(View view, Animator.AnimatorListener listener, long duration, Property<View, Float> alpha, float v, float v2, long startDelay) {
        ObjectAnimator animator = new ObjectAnimator();
        animator.setDuration(duration);
        animator.setProperty(alpha);
        animator.setFloatValues(v, v2);
        animator.setTarget(view);
        animator.setStartDelay(startDelay);
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.start();
    }

    /**
     * 특정 x,y좌표로 이동하는 애니메이션이다.
     */
    public void startLocation(View view, int rawX, int rawY, long duration, Animator.AnimatorListener listener, long startDelay) {
        startLocation(view, rawX, rawY, duration, listener, new OvershootInterpolator(2.5f), startDelay);
    }

    /**
     * 특정 x,y좌표로 이동하는 애니메이션이다.
     */
    public void startLocation(View view, int rawX, int rawY, long duration, Animator.AnimatorListener listener, TimeInterpolator value, long startDelay) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.X, rawX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.Y, rawY);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY);
        animator.setDuration(duration);
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.setStartDelay(startDelay);
        if (value != null) {
            animator.setInterpolator(value);
        }
        animator.start();
    }


    /**
     * 뷰의 중심 X를 구한다.
     */
    public int getCenterX(View view) {
        return view.getMeasuredWidth() / 2;
    }

    /**
     * 뷰의 중심 Y를 구한다
     */
    public int getCenterY(View view) {
        return view.getMeasuredHeight() / 2;
    }
}
