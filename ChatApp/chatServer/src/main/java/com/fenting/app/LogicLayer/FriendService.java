package com.fenting.app.LogicLayer;

import com.fenting.app.NetworkLayer.ServerManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FriendService {
    static private FriendService friendService = null;
    static synchronized public FriendService getFriendService() {
        if(friendService == null) {
            friendService = new FriendService();
            friendService.friendList = new LinkedList<>();
            friendService.lock = new ReentrantLock();
        }
        return friendService;
    }

    private LinkedList<String> friendList;
    private Lock lock;

    public void addIntoList(String name) {
        lock.lock();
        friendList.add(name);
        lock.unlock();
    }

    public void deleteInList(String name) {
        lock.lock();
        friendList.remove(name);
        lock.unlock();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", "friendList");
            jsonObject.put("friendList", friendList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerManager.getServerManager(8888).sendNotifications(jsonObject);
    }

    public final LinkedList<String> getList() {
        return this.friendList;
    }
}
