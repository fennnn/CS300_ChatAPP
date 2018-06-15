package com.fenting.app.LogicLayer;

import com.fenting.app.NetworkLayer.CompletionHandler;
import com.fenting.app.NetworkLayer.NetworkService;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageService {
    static private MessageService messageService = null;
    static synchronized public MessageService getMessageService() {
        if(messageService == null)
            messageService = new MessageService();
        return messageService;
    }

    public void sendGroupMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "groupMess");
            jsonObject.put("content", message);
            NetworkService.getNetworkService().PUSHRequest(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendPrivateMessage(String message, String to) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", "privateMess");
            jsonObject.put("content", message);
            jsonObject.put("to", to);
            NetworkService.getNetworkService().PUSHRequest(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setNewMessageHandler(CompletionHandler handler) {
        NetworkService.getNetworkService().SetSyncRequest("message", handler);
    }

    public void setClueHandler(CompletionHandler handler) {
        NetworkService.getNetworkService().SetSyncRequest("clue", handler);
    }
}
