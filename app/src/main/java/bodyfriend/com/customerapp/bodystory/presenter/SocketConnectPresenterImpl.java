package bodyfriend.com.customerapp.bodystory.presenter;

import com.karrel.mylibrary.RLog;

import bindservice.com.karrel.net.NetManager;
import bindservice.com.karrel.net.model.Start;
import bodyfriend.com.customerapp.base.util.PP;
import bodyfriend.com.customerapp.bodystory.socket.BSSocket;
import bodyfriend.com.customerapp.bodystory.socket.RxSocketEventBus;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Rell on 2017. 11. 6..
 */

public class SocketConnectPresenterImpl implements SocketConnectPresenter {
    private SocketConnectPresenter.View view;
    private BSSocket socket = BSSocket.getInstance();
    private Subscription mSubscription;
    private String nick;
    private int count = 2000;

    public SocketConnectPresenterImpl(View view) {
        this.view = view;
        setupEventListener();
    }

    @Override
    public void connect(String nick, String host, String port) {
        RLog.d(String.format("nick : %s, host : %s, port : %s", nick, host, port));
        this.nick = nick;
        if (nick.isEmpty()) {
            view.errorNick("닉네임을 입력해주세요");
            return;
        }

        if (host.isEmpty()) {
            view.errorHost("호스트를 입력해주세요");
        }

        if (port.isEmpty()) {
            view.errorPort("포트를 입력해주세요");
        }

        socket.connect(host, Integer.parseInt(port));
    }

    @Override
    public void connectedOk() {
        view.completed();
    }

    @Override
    public void connectedFail() {
        RLog.e("connectedFail");
    }

    @Override
    public void start() {
        // 2017. 11. 16. show Progress
        view.showProgress();

        String auctionIdx = PP.NO.get();
        String id = PP.ID.get();
        NetManager.getInstance().getStart(auctionIdx, id)
                .subscribe(new Observer<Start>() {
                    @Override
                    public void onCompleted() {
                        view.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
                        view.finish();
                        e.printStackTrace();
                        view.hideProgress();
                    }

                    @Override
                    public void onNext(Start start) {
                        RLog.d(start.toString());
                        NetManager.data = start;
                        connect(PP.ID.get(), NetManager.sockt_ip, "10100");
                    }
                });
    }

    /**
     * 통신연결의 이벤트를 세팅한다.
     */
    private void setupEventListener() {
        mSubscription = RxSocketEventBus.getInstance().getSubject().subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                if (s.startsWith(RxSocketEventBus.EXTRA.PARAM_CONNECTED)) {
                    socket.loginon(nick, count);
                    view.completed();
                    mSubscription.unsubscribe();
                }
            }
        });
    }
}
