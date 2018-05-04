package bodyfriend.com.customerapp.bodystory.socket;

import android.util.Log;

import com.karrel.mylibrary.RLog;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Rell on 2017. 11. 7..
 */

public class BSSocket {
    private static BSSocket instance;

    private Socket mSocket;
    private PrintWriter mWriter;
    private String mHost;
    private int mPort;

    private Queue<String> mSendQueue;
    private RxSocketEventBus eventBus;
    private String nick; // 닉네임
    private String auctionNum; // 입장방 번호
    private SenderThread senderThread;
    private ReceiverThread receiverThread;

    public static BSSocket getInstance() {
        if (instance == null) instance = new BSSocket();
        return instance;
    }

    public BSSocket() {
        eventBus = RxSocketEventBus.getInstance();
        mSendQueue = new LinkedList<>();
    }

    public void connect(String host, int port) {
        RLog.d(String.format("host : %s, port : %s", host, port));
        mHost = host;
        mPort = port;

        if (connectThread != null && connectThread.isAlive()) {
            clear();
        }
        connectThread = new Thread(runnable);
        // 통신연결
        connectThread.start();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final String host = mHost;
            int port = mPort;
            Log.e("", String.format("handleActionConnect, host : %s, port : %d", host, port));
            try {
                // 소켓 생성
                mSocket = new Socket(host, port);

                senderThread = new SenderThread(mSocket);
                senderThread.start();

                receiverThread = new ReceiverThread(mSocket);
                receiverThread.start();

                eventBus.sendEvent(RxSocketEventBus.EXTRA.PARAM_CONNECTED);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("", "connect fail!!");
            }
        }
    };

    private Thread connectThread;


    private void sendMessage(String msg) {
//        RLog.e(String.format("sendMessage(%s)", msg));
        if (mSocket == null) return;
        if (mSocket.isClosed()) {
            try {
                throw new IOException("소켓이 연결되지 않았습니다.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            senderThread.add(msg);
        }
    }

    /**
     * 서버로 로그인한다
     *
     * @param nick  로그인할 닉네임
     * @param count 이용횟수 정의;
     */
    public void loginon(String nick, int count) {
        this.nick = nick;
        sendMessage(String.format("@logon|%s", nick));
    }

    /**
     * 경매 참석
     *
     * @param auctionNum 경매번호
     */
    public void enter(String auctionNum, String count) {
        this.auctionNum = auctionNum;
        String msg = String.format("@enter|%s|%s|%s", nick, auctionNum, count);
        sendMessage(msg);
    }

    /**
     *
     */
    public void up() {
        String msg = String.format("@up|%s|%s", nick, auctionNum);
        sendMessage(msg);
    }

    public void down() {
        String msg = String.format("@down|%s|%s", nick, auctionNum);
        sendMessage(msg);
    }

    // 남은횟수를 확인
    public void getCount() {
//        sendMessage("@count");
    }

    // 현재 위치 확인
    public void getPoint() {
//        sendMessage("@point");
    }

    public void clear() {
        try {
            receiverThread.close();
            senderThread.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        sendMessage("/exit");
    }
}
