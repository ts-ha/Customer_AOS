package bodyfriend.com.customerapp.bodystory.presenter;

/**
 * Created by Rell on 2017. 11. 16..
 */

public class WebPresenterImpl implements WebPresenter {
    private WebPresenter.View view;

    public WebPresenterImpl(View view) {
        this.view = view;
    }

    @Override
    public void goBackWebPage() {
        view.webGoBack();
    }

    @Override
    public void showFinishPopup() {
        view.showFishDialog();
    }

    @Override
    public void finish() {
        view.finish();
    }
}
