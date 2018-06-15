package com.fenting.app.NetworkLayer;

import com.fenting.app.LogicLayer.AccountService;
import com.fenting.app.LogicLayer.FriendService;
import com.fenting.app.LogicLayer.MessageService;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class PacketSender extends Thread {
    private DataOutputStream out;
    private String packet;
    private Lock lock;

    PacketSender(DataOutputStream out, String packet, Lock lock) {
        this.out = out;
        this.packet = packet;
        this.lock = lock;
    }

    public void run() {
        try {
            lock.lock();
            out.writeUTF(packet);
            lock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

public class Server extends Thread {
    private final Socket server;
    private String account = "";
    private Integer serverID;
    private DataOutputStream out;
    private Lock lock;

    static public Server create(Socket server, int id) {
        Server newServer = new Server(server);
        newServer.serverID = id;
        newServer.start();
        return newServer;
    }

    public Server(Socket server) {
        this.server = server;
        try {
            out = new DataOutputStream(server.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        lock = new ReentrantLock();
    }

    public void run() {

        JSONObject json = new JSONObject();
        try {
            DataInputStream in = new DataInputStream(server.getInputStream());
            String buffer = in.readUTF();
            JSONObject args = new JSONObject(buffer);
            while (!buffer.equals("close")) {
                dispatchCommand(new JSONObject(buffer));
                buffer = in.readUTF();
            }
            ServerManager.getServerManager(2202).removeServer(serverID, account);
        } catch (IOException e) {
            FriendService.getFriendService().deleteInList(account);
            try {
                JSONObject alertObject = new JSONObject();
                alertObject.put("command", "alert");
                alertObject.put("content", account + " log out!");
                ServerManager.getServerManager(8888).sendNotifications(alertObject);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dispatchCommand(JSONObject args) {
        try {
            String command = args.get("command").toString();
            switch (command) {
                case "login": {
                    AccountService accountService = AccountService.getAccountService();
                    accountService.login(args.getString("account"), args.getString("password"), (JSONObject response) -> {
                        try {
                            if (response.getString("error").equals("0")) {
                                this.account = args.getString("account");
                                ServerManager serverManager = ServerManager.getServerManager(2022);
                                serverManager.addToAccountMap(this.account, this);
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("command", "friendList");
                                    jsonObject.put("friendList", FriendService.getFriendService().getList());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ServerManager.getServerManager(8888).sendNotifications(jsonObject);
                                JSONObject alertObject = new JSONObject();
                                alertObject.put("command", "alert");
                                alertObject.put("content", account + " log in!");
                                ServerManager.getServerManager(8888).sendNotifications(alertObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sendPack(response.toString());
                    });
                    break;
                }
                case "signUp": {
                    AccountService accountService = AccountService.getAccountService();
                    accountService.signUp(args.getString("account"), args.getString("password"), (JSONObject response) -> {
                        sendPack(response.toString());
                    });
                    break;
                }
                case "groupMess": {
                    MessageService.getMessageService().sendGroupMessage(args.getString("content"), this.account);
                    break;
                }
                case "privateMess": {
                    MessageService.getMessageService().sendPrivateMessage(args.getString("content"), this.account, args.getString("to"));
                    break;
                }
                default:
                    System.out.println(args.toString());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendPack(String pack) {
        PacketSender packetSender = new PacketSender(out, pack, lock);
        packetSender.start();
    }

}
