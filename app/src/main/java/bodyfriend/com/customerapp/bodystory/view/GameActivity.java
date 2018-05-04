package bodyfriend.com.customerapp.bodystory.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.Random;

import bodyfriend.com.customerapp.R;
import bodyfriend.com.customerapp.base.BFDialog;
import bodyfriend.com.customerapp.bodystory.customview.AreaView;
import bodyfriend.com.customerapp.bodystory.customview.StrokeTextView;
import bodyfriend.com.customerapp.bodystory.presenter.GamePresenter;
import bodyfriend.com.customerapp.bodystory.presenter.GamePresenterImpl;
import bodyfriend.com.customerapp.bodystory.util.Util;
import bodyfriend.com.customerapp.databinding.ActivityGame2Binding;

public class GameActivity extends BaseActivity implements GamePresenter.View {

    private ActivityGame2Binding binding;
    private GamePresenter presenter;
    private int changeCatCount = Integer.MIN_VALUE;
    private Toast toast;
    private Dialog winnerDialog;
    private Dialog finishPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game2);
        presenter = new GamePresenterImpl(this);

        setupButtonEvents();

        // ui가 그려질때까지 기다린다.
        uiReady();
        binding.niddle.niddle.setOnNiddleChangeListener(y -> presenter.changeNiddlePoint(y));

        setupStrokeColor();
    }

    private void uiReady() {
        // 레이아웃이 그려지면 다른 작업을 시작한다.
        binding.bg.buoy.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.bg.buoy.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                // 프로그래스가 뜨면 화면조작을 막기위해 이벤트를 먹인다..
                binding.progress.setOnClickListener(v -> {
                });
                presenter.uiReady();
                setupFishAnim();

            }
        });
    }

    private void setupButtonEvents() {
        binding.buttons.down.setOnClickListener(v -> presenter.clickDown());

        binding.buttons.up.setOnClickListener(v -> presenter.clickUp());

        binding.close.setOnClickListener(v -> presenter.clickFinish());
    }

    @Override
    public void printMsg(String msg) {
//        RLog.d(msg);
//        binding.log.setText(msg);
    }

    @Override
    public void showNotice(String s) {
        BFDialog.newInstance(this).showSimpleDialog(s).setCancelable(false);
//        binding.log.setText(s);
    }

    @Override
    public void setMoveCount(int moveCount) {
        binding.buttons.moveCount.setText(moveCount + "");
//        binding.buttons.moveCount.setText("999999");
    }

    @Override
    public void setPoint(String point, int pointLocation) {
        // 현재위치 표기
        binding.nums.txtCurPosition.setText(point);
        // 낚시 바늘의 위치
        binding.niddle.niddle.setPoint(pointLocation);
        // 현재위치 말풍선
        binding.niddle.txtNiddleLocation.setText(point);

        binding.niddle.niddlePointLayout.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setTime(String s) {
        long time = Long.parseLong(s);
        long sec = time / 1000 % 60;
        time -= sec * 1000;
        long min = time / 1000 / 60 % 60;
        time -= min * 1000 * 60;
        long hour = time / 1000 / 60 / 60 % 24;
        time -= hour * 1000 * 60 * 60;
        long day = time / 1000 / 60 / 60 / 24;

        // 시간이 없으면 하나씩 지운다
        binding.nums.day.setVisibility(View.VISIBLE);
        binding.nums.imgDay.setVisibility(View.VISIBLE);
        binding.nums.hour.setVisibility(View.VISIBLE);
        binding.nums.imgHour.setVisibility(View.VISIBLE);
        binding.nums.minute.setVisibility(View.VISIBLE);
        binding.nums.imgMinute.setVisibility(View.VISIBLE);
        if (day == 0) {
            binding.nums.day.setVisibility(View.GONE);
            binding.nums.imgDay.setVisibility(View.GONE);
            if (hour == 0) {
                binding.nums.hour.setVisibility(View.GONE);
                binding.nums.imgHour.setVisibility(View.GONE);
                if (min == 0) {
                    binding.nums.minute.setVisibility(View.GONE);
                    binding.nums.imgMinute.setVisibility(View.GONE);
                }
            }
        }

        // 시간을 적용한다
        binding.nums.day.setText(day + "");
        binding.nums.hour.setText(hour + "");
        binding.nums.minute.setText(min + "");
        binding.nums.sec.setText(sec + "");

    }

    @Override
    public void setAreaCnt(long areaCnt) {
        binding.niddle.niddle.setMaxSection(areaCnt);
    }

    @Override
    public void addArea(int start, int end, int standard) {
        binding.area.areaLayout.addView(new AreaView(binding.area.areaLayout, standard, start, end));
    }

    @Override
    public void catAnim(boolean isUp) {
//        RLog.e("isUp : " + isUp);
        // 고양이 얼굴 바꾸는거
        //         찌가 움직였다. 일정초동안 고양이 얼굴을 바꿔주자.

        binding.bg.cat.setImageResource(isUp ? R.drawable.img_boat_up : R.drawable.img_boat_down);
        //         일정시간이 지나면 원래 얼굴로 돌려준다.
        //         그 사이에 보낸 메세지가 있으면 없애자
        handler.removeMessages(changeCatCount);
        if (changeCatCount == Integer.MAX_VALUE) changeCatCount = Integer.MIN_VALUE;
        handler.sendEmptyMessageDelayed(++changeCatCount, 100);
    }

    @Override
    public void updateAreaViews(int pointLocation) {
        // AreaView가 해당 로케이션이면 색상을 변경한다.
        final int cnt = binding.area.areaLayout.getChildCount();

        for (int i = 0; i < cnt; i++) {
            if (binding.area.areaLayout.getChildAt(i) instanceof AreaView) {
                AreaView view = (AreaView) binding.area.areaLayout.getChildAt(i);
                view.updatePosition(pointLocation);
            }
        }

    }

    @Override
    public void startWaterDropAnim() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.water_anim);
        binding.waters.water1.startAnimation(animation);
        binding.waters.water1.setVisibility(View.VISIBLE);
        animation.setAnimationListener(animationListener);
    }

    @Override
    public void showFinishPopup() {
        if (finishPopup != null && finishPopup.isShowing()) return;
        finishPopup = BFDialog.newInstance(this).showDialog("게임을 종료하시겠습니까?", "종료", (dialog, which) -> presenter.finish(), "취소", null);
    }

    @Override
    public void showProgress() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        hideProgress.sendEmptyMessage(0);
    }

    private Handler hideProgress = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            binding.progress.setVisibility(View.GONE);
            return false;
        }
    });

    @Override
    public boolean isProgress() {
        // 프로그래스를 보여주는중인가?
        return binding.progress.getVisibility() == View.VISIBLE;
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            binding.waters.water1.setVisibility(View.GONE);
            binding.waters.water2.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.water_anim);
            binding.waters.water2.startAnimation(animation);
            animation.setAnimationListener(animationListener2);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    private Animation.AnimationListener animationListener2 = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            binding.waters.water2.setVisibility(View.GONE);
            binding.waters.water1.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.water_anim);
            binding.waters.water1.startAnimation(animation);
            animation.setAnimationListener(animationListener);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == changeCatCount) {
                binding.bg.cat.setImageResource(R.drawable.img_boat);
            }
            return false;
        }
    });

    @Override
    public void setSpeechBubble(double niddleY) {
//        RLog.e("niddleY : " + niddleY);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) binding.niddle.niddlePointLayout.getLayoutParams();
        params.leftMargin = binding.niddle.niddle.getRight() - Util.dpToPx(this, 25);
        // 상단갭
        int skyGap = -(int) (Util.getScreenSize(this).second * 0.05d);

        params.topMargin = (int) (skyGap + niddleY + binding.niddle.guide1.getTop()) - Util.dpToPx(this, 4);
        binding.niddle.niddlePointLayout.setLayoutParams(params);
    }

    @Override
    public void showWinner(String format) {
//        RLog.e(format);
        if (winnerDialog != null && winnerDialog.isShowing()) return;
        Message message = new Message();
        message.obj = format;
        messagePopup.sendMessage(message);
    }

    private Handler messagePopup = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (isFinishing()) return false;
            winnerDialog = BFDialog.newInstance(GameActivity.this).showDialog(msg.obj, "확인", (dialog, which) -> presenter.dismissWinnerPopup());
            winnerDialog.setCancelable(false);
            return true;
        }
    });

    @Override
    public void finish() {
        super.finish();
        presenter.shutDownSocket();
//        RLog.d("");
    }

    @Override
    public void startWebActivity() {
        Intent intent = new Intent(this, WebActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void showMessage(String s) {
        if (toast == null) {
            toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);
        } else {
            toast.setText(s);
        }

        toast.show();
    }

    @Override
    public void startBuoyAnim() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.buoy_anim);
        binding.bg.buoy.startAnimation(animation);
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    private void setupFishAnim() {
        moveFishAnim(binding.fishes.fish01, true);
        moveFishAnim(binding.fishes.fish02, true);
        moveFishAnim(binding.fishes.fish03, true);
        moveFishAnim(binding.fishes.fish04, true);
        moveFishAnim(binding.fishes.fish05, true);
        moveFishAnim(binding.fishes.fish06, true);
        moveFishAnim(binding.fishes.fish07, true);
        moveFishAnim(binding.fishes.fish08, true);
        moveFishAnim(binding.fishes.fish09, true);
        moveFishAnim(binding.fishes.fish10, true);
        moveFishAnim(binding.fishes.fish11, true);
        moveFishAnim(binding.fishes.fish12, true);
    }

    private void moveFishAnim(final ImageView view, boolean isToRight) {
        Drawable d = view.getDrawable();
        Bitmap b = ((BitmapDrawable) d).getBitmap();

        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setScale(-1, 1);

        Bitmap sideInversionImg = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), rotateMatrix, false);
        view.setImageBitmap(sideInversionImg);

        float fromXDelta;
        float toXDelta;
        if (isToRight) {
            fromXDelta = -view.getWidth() / 2f;
            toXDelta = view.getWidth() / 2f;
        } else {
            fromXDelta = view.getWidth() / 2f;
            toXDelta = -view.getWidth() / 2f;
        }

        TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);
        long duration = new Random().nextInt(6000);
        if (duration < 2000) duration = 2000;

        animation.setDuration(duration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveFishAnim(view, !isToRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    private void setupStrokeColor() {
        // 상단의 남은시간표기
        setStroke(binding.nums.day, 6f, Color.parseColor("#000000"));
        setStroke(binding.nums.hour, 6f, Color.parseColor("#000000"));
        setStroke(binding.nums.minute, 6f, Color.parseColor("#000000"));
        setStroke(binding.nums.sec, 6f, Color.parseColor("#000000"));
        setStroke(binding.nums.txtCurPosition, 4f, Color.parseColor("#000000"));

        setStroke(binding.nums.imgDay, 6f, Color.parseColor("#FB993F"));
        setStroke(binding.nums.imgHour, 6f, Color.parseColor("#FB993F"));
        setStroke(binding.nums.imgMinute, 6f, Color.parseColor("#FB993F"));
        setStroke(binding.nums.imgSec, 6f, Color.parseColor("#FB993F"));

        // 떡밥과 갯수
        setStroke(binding.buttons.txtMoveCount, 4f, Color.parseColor("#000000"));
        setStroke(binding.buttons.moveCount, 4f, Color.parseColor("#000000"));

        // 말풍선
        setStroke(binding.niddle.txtNiddleLocation, 4f, Color.parseColor("#000000"));

    }

    private void setStroke(StrokeTextView stroke, float strokeWidth, int color) {
        stroke.setStrokeWidth(strokeWidth);
        stroke.setStrokeColor(color);
    }
}
