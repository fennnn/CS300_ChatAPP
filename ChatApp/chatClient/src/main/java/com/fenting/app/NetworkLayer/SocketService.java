package com.fenting.app.NetworkLayer;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ReceiveService extends Thread {
    private DataInputStream in;
    private CompletionHandler handler;
    private Lock inLock = new ReentrantLock();

    ReceiveService(DataInputStream inputStream, CompletionHandler handler) {
        this.in = inputStream;
        this.handler = handler;
    }

    public void run() {
        String result = null;
        while (true) {
            try {
                inLock.lock();
                result = in.readUTF();
                System.out.println("recieved packet: " + result);
                handler.response(new JSONObject(result));
                inLock.unlock();
            } catch (IOException e) {
                inLock.unlock();
                System.out.println("Server connection aborted!");
                System.exit(-1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

public class SocketService {
    static private SocketService socketService = null;
    static SocketService getSocketService() {
        if(socketService == null) {
            socketService = new SocketService();
        }
        return socketService;
    }

    private DataOutputStream out = null;
    private Lock outLock = new ReentrantLock();
    private DataInputStream in = null;

    SocketService() {
        try {
            Socket socket = new Socket("localhost", 8888);
            System.out.println("long connection built!");
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void SendPacket(JSONObject pack) {
        try {
            outLock.lock();
            out.writeUTF(pack.toString());
            outLock.unlock();
            System.out.println("sent packet: " + pack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void receivePacketService(CompletionHandler handler) {
        ReceiveService receiveService = new ReceiveService(in, handler);
        receiveService.start();
    }

    void closeConnection() {
        try {
            outLock.lock();
            out.writeUTF("close");
            outLock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
