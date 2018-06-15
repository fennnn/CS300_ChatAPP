package com.fenting.app.LogicLayer;

import com.fenting.app.NetworkLayer.NetworkService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

public class ChatPage extends JPanel {
    static private ChatPage chatPage = null;

    static synchronized public ChatPage getChatPage(String content) {
        if(chatPage == null) {
            chatPage = new ChatPage();
            JFrame frame = new JFrame();
            chatPage.setLayout(null);
            chatPage.setBounds(0, 30, 800, 600);
            frame.setSize(800, 600);
            frame.setResizable(false);
            frame.setContentPane(chatPage);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    super.windowClosed(windowEvent);
                    System.exit(0);
                }
            });
            chatPage.initPage();
            chatPage.initActions();
            frame.setVisible(true);
            frame.setTitle(content);
        }
        return chatPage;
    }

    private BPosTextArea friendList = null;
    private BPosTextArea messageList = null;
    private BPosTextArea inputText = null;
    private JButton messageButton = null;
    private LinkedList<JButton> buttonList = null;
    private LinkedList<String> onLineUserList = null;
    private AlertView alertView = null;

    private void initPage() {
        this.friendList = new BPosTextArea();
        this.friendList.setBounds(0, 0, 120, 600);
        this.friendList.setEditable(false);
        this.friendList.setRadius(20);
        this.add(friendList);


        this.messageList = new BPosTextArea();
        this.messageList.setBounds(0, 0,600, 370);
        this.messageList.setEditable(false);
        this.messageList.setRadius(20);
        JScrollPane panel1 = new JScrollPane(this.messageList);
        panel1.setBounds(200, 30, 600, 370);
        this.add(panel1);

        this.messageButton = new JButton("send");
        this.messageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject jsonObject = new JSONObject();
                try {
                    if(inputText.getText().equals("")) {
                        return;
                    }
                    jsonObject.put("command", "groupMess");
                    jsonObject.put("content", inputText.getText());
                    inputText.setText("");
                    NetworkService.getNetworkService().PUSHRequest(jsonObject);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.messageButton.setBounds(700, 400, 100, 177);
        this.messageButton.setBackground(Color.gray);
        this.add(this.messageButton);

        this.inputText = new BPosTextArea();
        this.inputText.setBounds(200, 400, 500, 177);
        this.inputText.setRadius(20);
        this.add(this.inputText);

        this.onLineUserList = new LinkedList<>();
        this.buttonList = new LinkedList<>();

        this.alertView = AlertView.getAlertView();
        this.alertView.setBounds(200, 0, 600, 30);
        this.add(alertView);
    }

    private void initActions() {

        NetworkService.getNetworkService().SetSyncRequest("alert", (response) -> {
            try {
                this.alertView.alert(response.getString("content"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        NetworkService.getNetworkService().SetSyncRequest("message", (response) -> {
            try {
                this.messageList.setText(this.messageList.getText() + "\n" + response.getString("content"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        NetworkService.getNetworkService().SetSyncRequest("friendList", (response) -> {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Online users: ");
            for(int i=0; i<this.buttonList.size(); ++i) {
                this.remove(this.buttonList.get(i));
            }
            this.buttonList = new LinkedList<>();
            this.onLineUserList = new LinkedList<>();
            try {
                JSONArray jsonObject = response.getJSONArray("friendList");
                for(int i =0; i<jsonObject.length(); ++i) {
                    stringBuffer.append("\n" + jsonObject.getString(i));
                    this.onLineUserList.add(jsonObject.getString(i));
                }
                this.friendList.setText(stringBuffer.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i=0; i<this.onLineUserList.size(); ++i) {
                JButton jButton = new JButton("send");
                final int finalI = i;
                jButton.setBounds(130, 25 + finalI * 17, 60, 20);
                jButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if(inputText.getText().equals("")) {
                                return;
                            }
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("command", "privateMess");
                            jsonObject.put("content", inputText.getText());
                            jsonObject.put("to", onLineUserList.get(finalI));
                            inputText.setText("");
                            NetworkService.getNetworkService().PUSHRequest(jsonObject);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                this.add(jButton);
                this.buttonList.add(jButton);
            }
        });
    }
}
