package bodyfriend.com.customerapp.bodystory.socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

class SenderThread extends Thread {
    Socket socket;
    DataOutputStream out;
    String name;

    private Queue<String> mSendQueue;

    public SenderThread(Socket socket) {
        this.socket = socket;
        try {
            out = new DataOutputStream(this.socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("예외:" + e);
        }
        mSendQueue = new LinkedList<>();
    }

    @Override
    public void run() {

        while (out != null) {
            if (!mSendQueue.isEmpty()) {
                try {
                    String msg = mSendQueue.poll();

                    if (msg == null || msg.trim().equals("")) {
                        msg = " ";
                    }

                    if (msg.trim().startsWith("@logon")) {
                        if (!msg.trim().equals("")) {
                            name = msg.substring(msg.indexOf("|") + 1);
                            out.writeUTF(msg);
                        } else {
                            System.out.println("[##] 유저 정보(ID) 없음");
                        }
                    } else if (msg.trim().startsWith("@create")) {
                        if (!msg.trim().equals("")) {
                            out.writeUTF(msg);
                        } else {
                            System.out.println("[##] 공백을 입력할수없습니다.");
                        }
                    } else if (msg.trim().startsWith("@enter")) {
                        if (!msg.trim().equals("")) {
                            out.writeUTF(msg);
                        } else {
                            System.out.println("[##] 공백을 입력할수없습니다.");
                        }
                    } else if (msg.trim().startsWith("@up")) {
                        if (!msg.trim().equals("")) {
                            out.writeUTF(msg);
                        } else {
                            System.out.println("[##] 공백을 입력할수없습니다.");
                        }
                    } else if (msg.trim().startsWith("@down")) {
                        if (!msg.trim().equals("")) {
                            out.writeUTF(msg);
                        } else {
                            System.out.println("[##] 공백을 입력할수없습니다.");
                        }
                    } else if (msg.trim().startsWith("/")) {
                        if (msg.equalsIgnoreCase("/exit")) {
                            System.out.println("[##] 클라이언트를 종료합니다.");
//                            System.exit(0);
                            break;
                        } else {
                            out.writeUTF("command|" + name + "|" + msg);
                        }
                    } else {
//                        out.writeUTF("show|" + name + "|" + msg);
                    }

                } catch (SocketException e) {
                    System.out.println("Sender:run()예외:" + e);
                    System.out.println("##접속중인 서버와 연결이 끊어졌습니다.");
                    return;
                } catch (IOException e) {
                    System.out.println("예외:" + e);
                }
            }
        }
    }

    public void add(String msg) {
        mSendQueue.add(msg);
    }

    public void close() {
        out = null;
    }
}