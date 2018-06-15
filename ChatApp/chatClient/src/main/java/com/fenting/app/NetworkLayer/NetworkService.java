package com.fenting.app.NetworkLayer;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class SyncChecker extends Thread
{
    static private SyncChecker syncChecker;

    static SyncChecker getSyncChecker(HashMap<String, JSONObject> source, Lock lock) {
        if(syncChecker == null) {
            syncChecker = new SyncChecker(source, lock);
            syncChecker.start();
        }
        return syncChecker;
    }

    void registerSyncHandler(String command, CompletionHandler handler) {
        this.handlerLock.lock();
        this.handlerBuffer.put(command, handler);
        this.handlerLock.unlock();
    }

    private HashMap<String, JSONObject> source;
    private Lock sourceLock;
    private HashMap<String, CompletionHandler> handlerBuffer;
    private Lock handlerLock;

    private SyncChecker(HashMap<String, JSONObject> source, Lock lock) {
        this.source = source;
        this.sourceLock = lock;
        this.handlerBuffer = new HashMap<>();
        this.handlerLock = new ReentrantLock();
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
                this.handlerLock.lock();
                Iterator it = handlerBuffer.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry)it.next();
                    String command = entry.getKey().toString();
                    CompletionHandler handler = (CompletionHandler) entry.getValue();
                    sourceLock.lock();
                    JSONObject response = source.get(command);
                    if(response != null) {
                        handler.response(response);
                        source.put(command, null);
                        System.out.println("take out packet" + response);
                    }
                    sourceLock.unlock();
                }
                this.handlerLock.unlock();
            } catch (InterruptedException e) {
                sourceLock.unlock();
                e.printStackTrace();
            }
        }
    }
}

public class NetworkService {
    static private NetworkService networkService = null;
    static public NetworkService getNetworkService() {
        if(networkService == null) {
            networkService = new NetworkService();
            networkService.setReceiveService();
        }
        return networkService;
    }

    private HashMap<String, JSONObject> packetBuffer = new HashMap<>();
    private Lock lock = new ReentrantLock();

    public void CGIRequest(JSONObject args, CompletionHandler handler) {
        SocketService socketService = SocketService.getSocketService();
        socketService.SendPacket(args);
        try {
            lock.lock();
            JSONObject response = packetBuffer.get(args.get("command").toString());
            lock.unlock();
            while (response == null) {
                Thread.sleep(100);
                response = packetBuffer.get(args.get("command").toString());
            }
            handler.response(response);
            System.out.println("take out packet" + response);
            packetBuffer.put(args.get("command").toString(), null);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void PUSHRequest(JSONObject args) {
        SocketService socketService = SocketService.getSocketService();
        socketService.SendPacket(args);
    }

    public void SetSyncRequest(String command, CompletionHandler handler) {
        SyncChecker syncChecker = SyncChecker.getSyncChecker(packetBuffer, lock);
        syncChecker.registerSyncHandler(command, handler);
    }

    public void closeConnection() {
        SocketService socketService = SocketService.getSocketService();
        socketService.closeConnection();
    }

    private void setReceiveService() {
        SocketService socketService = SocketService.getSocketService();
        socketService.receivePacketService((response) -> {
            try {
                String command = response.get("command").toString();
                lock.lock();
                packetBuffer.put(command, response);
                lock.unlock();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
