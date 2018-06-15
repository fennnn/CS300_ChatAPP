package com.fenting.app.LogicLayer;

import com.fenting.app.NetworkLayer.ServerManager;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageService {
    static private MessageService messageService = null;
    static synchronized public MessageService getMessageService() {
        if(messageService == null)
            messageService = new MessageService();
        return messageService;
    }

    public void sendGroupMessage(String message, String from) {
        ServerManager serverManager = ServerManager.getServerManager(2022);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "message");
            jsonObject.put("content", from + ": " + message);
            serverManager.sendNotifications(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendPrivateMessage(String message, String from, String to) {
        ServerManager serverManager = ServerManager.getServerManager(2022);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "message");
            jsonObject.put("content", "(private)" + from + ": " + message);
            serverManager.pushMessageTo(to, jsonObject);
            serverManager.pushMessageTo(from, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
