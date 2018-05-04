package bodyfriend.com.customerapp.login.intro;

import android.animation.Animator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import bodyfriend.com.customerapp.R;
import bodyfriend.com.customerapp.base.BFActivity;
import bodyfriend.com.customerapp.databinding.ActivityIntroBinding;

public class IntroActivity extends BFActivity {

    boolean isStartAnim = false; // 인트로 애니메이션이 중복해서 실행되지 않기위한 플래그이다.

    private AnimationManager mAnimationManager = AnimationManager.newInstance();

    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
    }

    @Override
    protected void onLoadOnce() {
        super.onLoadOnce();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        startIntroAnim();
    }

    /**
     * 인트로 애니메이션을 시작한다.
     */
    private void startIntroAnim() {
        if (isStartAnim) {
            return;
        }
        isStartAnim = true;

        // 아이템들의 위치를 초기화한다.
        binding.item1.setX(binding.frame.getWidth());
        binding.item2.setX(binding.frame.getWidth());
        binding.item3.setX(binding.frame.getWidth());
        binding.item4.setX(binding.frame.getWidth());
        binding.item5.setX(binding.frame.getWidth());
        binding.item6.setX(binding.frame.getWidth());

        animToCenter(binding.item1, listener1);
    }

    private Animator.AnimatorListener listener1 = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animToCenter(binding.item2, listener2);
            animToLeft(binding.item1);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private Animator.AnimatorListener listener2 = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animToCenter(binding.item3, listener3);
            animToLeft(binding.item2);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private Animator.AnimatorListener listener3 = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            finish();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private Animator.AnimatorListener listener4 = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animToCenter(binding.item5, listener5);
            animToLeft(binding.item4);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private Animator.AnimatorListener listener5 = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animToCenter(binding.item6, listener6);
            animToLeft(binding.item5);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private Animator.AnimatorListener listener6 = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

            finish();

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private void animToCenter(View view, Animator.AnimatorListener listener) {
        int rawX = mAnimationManager.getCenterX(binding.frame) - mAnimationManager.getCenterX(view);
        int rawY = mAnimationManager.getCenterY(binding.frame) - mAnimationManager.getCenterY(view);
        mAnimationManager.startLocation(view, rawX, rawY, 1000l, listener, 0);
    }

    private void animToLeft(View view) {
        int rawX = -view.getRight();
        int rawY = (int) view.getY();
        mAnimationManager.startLocation(view, rawX, rawY, 1000l, null, 0);
    }
}
