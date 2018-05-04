package bodyfriend.com.customerapp.bodystory.presenter;

/**
 * Created by Rell on 2017. 11. 16..
 */

public interface WebPresenter {
    void goBackWebPage();

    void showFinishPopup();

    void finish();

    interface View {

        void webGoBack();

        void showFishDialog();

        void finish();
    }
}
