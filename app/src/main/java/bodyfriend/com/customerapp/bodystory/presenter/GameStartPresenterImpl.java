package bodyfriend.com.customerapp.bodystory.presenter;

import com.karrel.mylibrary.RLog;

/**
 * Created by Rell on 2017. 10. 26..
 */

public class GameStartPresenterImpl implements GameStartPresenter {
    private GameStartPresenter.View view;

    public GameStartPresenterImpl(View view) {
        this.view = view;
    }

    @Override
    public void clickStart() {
        RLog.d("clickStart");
        // 2017. 11. 6. 소켓 통신 연결 팝업을 띄운다
        view.showSocketSettingPopup();
    }


    @Override
    public void connectOk() {
        // 연결 성공
        view.startGameActivity();
    }

}
