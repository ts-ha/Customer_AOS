package bodyfriend.com.customerapp.bodystory.presenter;

/**
 * Created by Rell on 2017. 10. 26..
 */

public interface GameStartPresenter {
    void clickStart();

    void connectOk();

    interface View {

        void startGameActivity();

        void showSocketSettingPopup();
    }
}
