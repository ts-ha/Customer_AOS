package bodyfriend.com.customerapp.bodystory.socket;

import com.karrel.mylibrary.RLog;

import java.io.DataInputStream;
import java.net.Socket;
import java.net.SocketException;

class ReceiverThread extends Thread {

    Socket socket;
    DataInputStream in;

    public ReceiverThread(Socket socket) {
        this.socket = socket;
        try {
            in = new DataInputStream(this.socket.getInputStream());
        } catch (Exception e) {
            System.out.println("예외:" + e);
        }
    }

    @Override
    public void run() {

        while (in != null) {
            try {

                String msg = in.readUTF();

                if (!msg.startsWith("time")) {
                    RLog.d(String.format("receive message : %s", msg));
                }

                RxSocketEventBus.getInstance().sendEvent(msg);

            } catch (SocketException e) {
                System.out.println("예외:" + e);
                System.out.println("##접속중인 서버와 연결이 끊어졌습니다.");
                return;

            } catch (Exception e) {
                System.out.println("Receiver:run() 예외:" + e);

            }
        }
    }

    public void close() {
        in = null;
    }
}