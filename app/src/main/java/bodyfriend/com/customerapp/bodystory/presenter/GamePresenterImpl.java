package bodyfriend.com.customerapp.bodystory.presenter;

import com.karrel.mylibrary.RLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bindservice.com.karrel.net.NetManager;
import bindservice.com.karrel.net.model.Bidding;
import bindservice.com.karrel.net.model.End;
import bodyfriend.com.customerapp.base.util.PP;
import bodyfriend.com.customerapp.bodystory.model.AreaValue;
import bodyfriend.com.customerapp.bodystory.socket.BSSocket;
import bodyfriend.com.customerapp.bodystory.socket.RxSocketEventBus;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rell on 2017. 10. 30..
 */

public class GamePresenterImpl implements GamePresenter {
    private GamePresenter.View view;
    private BSSocket socket;
    private StringBuilder builder;

    // 이동가능 떡밥수
    private int moveCount = 0;
    // 이동가는 영역의 최대치
    private long maxLocation;
    private long beforePoint;
    private boolean isExit = false;

    public GamePresenterImpl(GamePresenter.View view) {
        this.view = view;
        builder = new StringBuilder();
        setupMinMax();
        socketConnect();
//        testCode();
    }

    // TODO: 2017. 11. 30. 삭제해야할 테스트 코드
    private void testCode() {
        if (true) {
            Observable.just("test")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .delay(3, TimeUnit.SECONDS)
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(String s) {
                            RLog.d("onNext");
                            StringBuilder builder = new StringBuilder();
                            builder.append("경매가 종료되었습니다.");
                            builder.append("\n\n");

                            view.showWinner(builder.toString());
                            view.hideProgress();
                        }
                    });
            return;
        }
    }

    private void setupBiding() {
        try {
            List<Integer> list = new ArrayList<>();
            for (Bidding bidding : NetManager.data.bidding) {
                list.add(bidding.scope);
            }

            Collections.sort(list);

            int min = Integer.parseInt(NetManager.data.game.min);
            int max = Integer.parseInt(NetManager.data.game.max);
            int standard = max / min;

            List<AreaValue> values = new ArrayList<>();
            AreaValue value;
            for (int i = 0; i < list.size(); i++) {
                value = new AreaValue();
                values.add(value);

                int startValue = list.get(i);
                value.start = startValue - min;
                value.end = startValue;

                if (i + 1 >= list.size()) break;
                int nextValue = list.get(i + 1);
                while (startValue + min == nextValue) {
                    startValue = nextValue;
                    value.end = nextValue;

                    i++;
                    if (i + 1 >= list.size()) break;
                    nextValue = list.get(i + 1);
                }
            }

            for (AreaValue value1 : values) {
                view.addArea(value1.start / min, value1.end / min, standard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupMinMax() {
        try {
            long min = Long.parseLong(NetManager.data.game.min);
            long max = Long.parseLong(NetManager.data.game.max);
            // 영역 카운트
            maxLocation = max / min;
            view.setAreaCnt(maxLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void socketConnect() {
        socket = BSSocket.getInstance();
        initEventBus();

        try {
            socket.enter(PP.NO.get(), NetManager.data.count + "");
        } catch (Exception e) {

        }
    }

    private void initEventBus() {
        RxSocketEventBus.getInstance().getSubject().subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                initEventBus();
            }

            @Override
            public void onNext(String msg) {
//                RLog.d("recieve message : " + msg);

                view.printMsg(builder.toString());

                String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|") + 1));
                if (msg.startsWith("logon#yes")) {
                    // 2017. 11. 17. 로그인성공
                    onLogonSuccess();
                } else if (msg.startsWith("logon#no")) {
                    // 2017. 11. 17. 로그인실패
                    onLogonFail();
                } else if (msg.startsWith("enterRoom#yes")) {
                    // 2017. 11. 17. 경매입장
                    enterRoom(msgArr);
                } else if (msg.startsWith("enterRoom#no")) { // 경매입장 실패
                    // 2017. 11. 17. 경매입장실패
                    enterRoomFail(msgArr);
                } else if (msg.startsWith("up#yes") | msg.startsWith("down#yes")) { // 위로|아래로
                    // 2017. 11. 17. 포인트 이동
                    movePoint(msgArr);
                } else if (msg.startsWith("up#no") | msg.startsWith("down#no")) { // 위로|아래로
                    // 2017. 11. 17. 포인트 이동 실패
                    movePointFail(msgArr[0]);
                } else if (msg.startsWith("show")) { //서버에서전달하고자하는 메시지
                    // 2017. 11. 17. 메시지 보여주기
                    showMessage(msgArr[0]);
                } else if (msg.startsWith("time")) { //서버에서 보내주는 시간
                    // 2017. 11. 17. 서버 시간 표기
                    setTime(msgArr[0]);
                } else if (msg.startsWith("exit")) { // 종료
                    // 2017. 11. 17. 종료
                    exit();
                } else if (msg.startsWith("end")) {
                    // 2017. 11. 17. 게임종료
                    exit(msgArr[0]);
                } else if (msg.startsWith("move")) {
                    // 2017. 11. 17. 이동
                    movePoint(msgArr[0]);
//                    RLog.e("recieve message : " + msg);
                }
            }
        });
    }

    private void movePoint(String s) {
        try {
            final String point = s;
            final long lPoint = Long.parseLong(point);
            final int pointLocation = (int) (lPoint / Long.parseLong(NetManager.data.game.min));
            view.setPoint(point, pointLocation);
            view.catAnim(lPoint < beforePoint);


            view.updateAreaViews(pointLocation);
//            RLog.d(String.format("lPoint : %s, beforePoint : %s", lPoint, beforePoint));
            beforePoint = lPoint;
        } catch (Exception e) {

        }

//        view.setSpeechBubble(pointLocation, maxLocation);
    }

    private void exit() {
//        view.showNotice("종료요청.");
//        System.out.println("종료요청.");
    }

    private void setTime(String s) {
        view.setTime(s);
//        System.out.println(s);
    }

    private void showMessage(String x) {
        // show|메시지내용
//                view.showNotice(msgArr[0]);
//        System.out.println(x);
        view.showMessage(x);
    }

    private void movePointFail(String s) {
        // up#yes|잔여횟수|현재위치
//        view.showNotice("[##] 잔여횟수가 부족합니다. 현재위치 :" + s);
//        System.out.println("[##] 잔여횟수가 부족합니다. 현재위치 :" + s);
    }

    private void movePoint(String[] msgArr) {
        // up#yes|잔여횟수|현재위치
        moveCount = Integer.parseInt(msgArr[0]);

        final String point = msgArr[1];
        final int pointLocation = (int) (Long.parseLong(point) / Long.parseLong(NetManager.data.game.min));

        view.setPoint(point, pointLocation);
//        view.setSpeechBubble(pointLocation, maxLocation);

        view.setMoveCount(Integer.parseInt(msgArr[0]));
//        System.out.println("[##] 잔여횟수 :" + msgArr[0] + ", 현재위치 :" + msgArr[1]);
    }

    private void enterRoomFail(String[] msgArr) {
//        view.showNotice("[##] [" + msgArr[0] + "]는 존재하지않는 경매입니다.");
//        System.out.println("[##] [" + msgArr[0] + "]는 존재하지않는 경매입니다.");
    }

    private void enterRoom(String[] msgArr) {
        // enterRoom#yes|잔여횟수
        view.setMoveCount(Integer.parseInt(msgArr[0]));
//        view.showMessage("[##] 경매 에 입장하였습니다. 잔여횟수 :" + msgArr[0]);
//        System.out.println("[##] 경매 에 입장하였습니다. 잔여횟수 :" + msgArr[0]);
        moveCount = Integer.parseInt(msgArr[0]);

        final String point = msgArr[1];
        final int pointLocation = (int) (Long.parseLong(point) / Long.parseLong(NetManager.data.game.min));
        view.setPoint(point, pointLocation);
//        view.setSpeechBubble(pointLocation, maxLocation);

        view.updateAreaViews(pointLocation);
    }

    // 로그인실패
    private void onLogonFail() {
//        view.showNotice("[##] 중복된 아이디가 존재합니다");
//        System.out.println("[##] 중복된 아이디가 존재합니다");
    }

    // 로그인 성공
    private void onLogonSuccess() {
        getCount();
        socket.getPoint();
    }

    private void exit(String winpoint) {
        if (isExit) return;
        isExit = true;
        socket.exit();

        view.showProgress();

        NetManager.getInstance().getEnd(PP.NO.get(), winpoint)
                .delay(3, TimeUnit.SECONDS)
                .subscribe(new Observer<End>() {
                    @Override
                    public void onCompleted() {
                        RLog.d("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        RLog.d("onError");
                        RLog.e(e.getMessage());
                        e.printStackTrace();
                        view.hideProgress();

                    }

                    @Override
                    public void onNext(End end) {
                        RLog.d("onNext");
                        StringBuilder builder = new StringBuilder();
                        builder.append("경매가 종료되었습니다.");
                        builder.append("\n\n");
                        if (end.winner.isEmpty()) {
                            builder.append("당첨자가 없습니다.");
                        } else if (end.winner.equals(PP.ID.get())) {
                            builder.append("축하합니다. 당첨되셨어요!!");
                        } else {
                            builder.append(String.format("%s님이 당첨되었습니다.", end.winner));
                        }

                        view.showWinner(builder.toString());
                        view.hideProgress();
                    }
                });

    }

    public String[] getMsgParse(String msg) {
        String[] tmpArr = msg.split("[|]");
        return tmpArr;
    }

    @Override
    public void clickDown() {
//        RLog.d("clickDown");
        if (moveCount == 0) {
            view.showNotice("사용 가능한 떡밥이 없습니다.");
            return;
        }
        view.setMoveCount(--moveCount);
        // 화면상에서는 내려감이지만 실제 데이터 갑은 up이므로 up을 요청한다
        socket.up();
        getCount();
    }

    @Override
    public void clickUp() {
//        RLog.d("clickUp");
        if (moveCount == 0) {
            view.showNotice("사용 가능한 떡밥이 없습니다.");
            return;
        }
        view.setMoveCount(--moveCount);
        // 화면상에서는 올라감이지만 실제 데이터 값은 down 이므로 down을 요청한다.
        socket.down();
        getCount();
    }

    @Override
    public void shutDownSocket() {
//        RLog.d("");
        socket.clear();
    }

    @Override
    public void uiReady() {
        // ui가 그려졌다.
        setupBiding();
        startBuoyAnim();
        startWaterDropAnim();
    }

    private void startWaterDropAnim() {
        view.startWaterDropAnim();
    }

    private void startBuoyAnim() {
        view.startBuoyAnim();
    }

    @Override
    public void dismissWinnerPopup() {
        view.finish();
//        view.startWebActivity();
    }

    @Override
    public void onBackPressed() {
        if (!view.isProgress()) {
            view.showFinishPopup();
        }
    }

    @Override
    public void changeNiddlePoint(int y) {
        view.setSpeechBubble(y);
    }

    @Override
    public void finish() {
        socket.exit();
        view.finish();
    }

    @Override
    public void clickFinish() {
        view.showFinishPopup();
    }

    // 이동가능 횟수 요청
    private void getCount() {
        socket.getCount();
    }
}
