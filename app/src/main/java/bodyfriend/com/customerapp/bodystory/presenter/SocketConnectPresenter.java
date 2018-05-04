package bodyfriend.com.customerapp.bodystory.presenter;

/**
 * Created by Rell on 2017. 11. 6..
 */

public interface SocketConnectPresenter {
    void connect(String nick, String host, String port);

    void connectedOk();

    void connectedFail();

    void start();

    interface View {

        void errorNick(String s);

        void errorHost(String s);

        void errorPort(String s);

        void showError(String message);

        void completed();

        void showProgress();

        void hideProgress();

        void finish();
    }
}
