package bodyfriend.com.customerapp.bodystory.socket;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Rell on 2017. 11. 7..
 */

public class RxSocketEventBus {
    private PublishSubject<String> subject = PublishSubject.create();

    private static RxSocketEventBus eventBus;

    public class EXTRA {
        public static final String PARAM_EXIT = "/exit";
        public static final String PARAM_UP = "@up";
        public static final String PARAM_DOWN = "@down";
        public static final String PARAM_POINT = "@point";
        public static final String PARAM_COUNT = "@count";
        public static final String PARAM_CONNECTED = "@connected";
    }

    public static RxSocketEventBus getInstance() {
        if (eventBus == null) eventBus = new RxSocketEventBus();
        return eventBus;
    }

    public void sendEvent(String msg) {
        subject.onNext(msg);
    }

    public Observable<String> getSubject() {
        Observable<String> observable =
                subject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onBackpressureDrop()
                ;
        return observable;
    }
}
