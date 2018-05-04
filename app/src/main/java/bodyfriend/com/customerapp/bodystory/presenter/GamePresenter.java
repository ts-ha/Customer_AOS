package bodyfriend.com.customerapp.bodystory.presenter;

/**
 * Created by Rell on 2017. 10. 30..
 */

public interface GamePresenter {
    void socketConnect();

    void clickDown();

    void clickUp();

    void shutDownSocket();

    void uiReady();

    void dismissWinnerPopup();

    void onBackPressed();

    void changeNiddlePoint(int y);

    void finish();

    void clickFinish();

    interface View {

        void printMsg(String msg);

        void showNotice(String s);

        void setMoveCount(int moveCount);

        void setPoint(String point, int pointLocation);

        void setTime(String s);

        void setAreaCnt(long areaCnt);

        void setSpeechBubble(double point);

        void showWinner(String format);

        void finish();

        void startWebActivity();

        void showMessage(String s);

        void startBuoyAnim();

        void addArea(int i, int i1, int standard);

        void catAnim(boolean isUp);

        void updateAreaViews(int pointLocation);

        void startWaterDropAnim();

        void showFinishPopup();

        void showProgress();

        void hideProgress();

        boolean isProgress();
    }
}
