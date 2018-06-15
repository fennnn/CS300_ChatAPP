package com.fenting.app.LogicLayer;

import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginPage extends JPanel {
    static private LoginPage loginPage = null;

    static public LoginPage getLoginPage() {
        if(loginPage == null) {
            JFrame frame = new JFrame();
            loginPage = new LoginPage(frame);
            loginPage.setLayout(null);
            loginPage.setBounds(0, 0, 300, 500);
            frame.setSize(300, 500);
            frame.setResizable(false);
            frame.setContentPane(loginPage);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    super.windowClosed(windowEvent);
                    System.exit(0);
                }
            });
            loginPage.initPage();
            frame.setVisible(true);
            frame.setTitle("CS300 Chat App");
        }
        return loginPage;
    }

    private JFrame frame;
    private JLabel jLabel;

    LoginPage(JFrame frame) {
        this.frame = frame;
    }

    void initPage() {

        jLabel = new JLabel();
        jLabel.setText("");
        jLabel.setBounds(50, 100, 250, 30);
        this.add(jLabel);

        JTextField accountTextField = new JTextField("Account");
        accountTextField.setBounds(50, 200, 200, 30);
        this.add(accountTextField);

        JTextField passwordTextField = new JTextField("Password");
        passwordTextField.setBounds(50, 230, 200, 30);
        this.add(passwordTextField);


        JButton logInButton = new JButton();
        logInButton.setText("Login");
        logInButton.setBounds(100, 300, 100, 30);
        JFrame currentFrame = this.frame;
        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(accountTextField.getText().equals("Account") ||
                        accountTextField.getText().equals("")) {
                    jLabel.setForeground(Color.RED);
                    jLabel.setText("Please input your account!!!");
                    return;
                }
                if(passwordTextField.getText().equals("Password") ||
                        passwordTextField.getText().equals("")) {
                    jLabel.setForeground(Color.RED);
                    jLabel.setText("Please input your password!!!");
                    return;
                }
                jLabel.setText(" ");
                jLabel.setForeground(Color.BLACK);
                AccountService accountService = AccountService.getAccountService();
                accountService.login(accountTextField.getText(), passwordTextField.getText(), (response) -> {
                    try {
                        if(response.getInt("error") == 0) {
                            accountService.myAccount = accountTextField.getText();
                            currentFrame.dispose();
                            ChatPage chatPage = ChatPage.getChatPage(accountService.myAccount);
                        }
                    } catch (JSONException e1) {
                        try {
                            jLabel.setText(response.getString("error"));
                            jLabel.setForeground(Color.RED);
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }
                    }
                });
            }
        });
        this.add(logInButton);

        JButton signUpButton = new JButton();
        signUpButton.setText("Sign Up");
        signUpButton.setBounds(100, 330, 100, 30);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SignUpPage signUpPage = SignUpPage.getSignUpPage();
            }
        });
        this.add(signUpButton);
    }

    public void showMessage(String message) {
        jLabel.setForeground(Color.BLACK);
        jLabel.setText(message);
    }
}
