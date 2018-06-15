package com.fenting.app.LogicLayer;

import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SignUpPage extends JPanel {
    static private SignUpPage signUpPage = null;

    static public SignUpPage getSignUpPage() {
        if(signUpPage == null) {
            JFrame frame = new JFrame();
            signUpPage = new SignUpPage(frame);
            signUpPage.setLayout(null);
            signUpPage.setBounds(0, 0, 300, 500);
            frame.setSize(300, 500);
            frame.setResizable(false);
            frame.setContentPane(signUpPage);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    super.windowClosed(windowEvent);
                    signUpPage = null;
                }
            });
            signUpPage.initPage();
            frame.setVisible(true);
            frame.setTitle("Kingdom of Frupal");
        }
        return signUpPage;
    }

    private JFrame frame;

    SignUpPage(JFrame frame) {
        this.frame = frame;
    }

    void initPage() {
        JLabel jLabel = new JLabel();
        jLabel.setText("Welcome to the Kingdom of Frupal");
        jLabel.setBounds(50, 100, 250, 30);
        this.add(jLabel);

        JTextField accountTextField = new JTextField("Account");
        accountTextField.setBounds(50, 150, 200, 30);
        this.add(accountTextField);

        JTextField passwordTextField = new JTextField("Password");
        passwordTextField.setBounds(50, 180, 200, 30);
        this.add(passwordTextField);

        JTextField retypePasswordTextField = new JTextField("Retype password");
        retypePasswordTextField.setBounds(50, 210, 200, 30);
        this.add(retypePasswordTextField);

        JButton signUpButton = new JButton();
        signUpButton.setText("SignUp");
        signUpButton.setBounds(100, 280, 100, 30);
        JFrame currentFrame = this.frame;
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (accountTextField.getText().equals("Account") ||
                        accountTextField.getText().equals("")) {
                    jLabel.setForeground(Color.RED);
                    jLabel.setText("Please input your account!!!");
                    return;
                }
                if (passwordTextField.getText().equals("Password") ||
                        passwordTextField.getText().equals("")) {
                    jLabel.setForeground(Color.RED);
                    jLabel.setText("Please input your password!!!");
                    return;
                }
                if (passwordTextField.getText().equals("Retype password") ||
                        passwordTextField.getText().equals("")) {
                    jLabel.setForeground(Color.RED);
                    jLabel.setText("Please retype your password!!!");
                    return;
                }
                if (!passwordTextField.getText().equals(retypePasswordTextField.getText())) {
                    jLabel.setForeground(Color.RED);
                    jLabel.setText("two passwords doesn't match!");
                    return;
                }
                jLabel.setText(" ");
                jLabel.setForeground(Color.BLACK);
                AccountService accountService = AccountService.getAccountService();
                accountService.signUp(accountTextField.getText(), passwordTextField.getText(), (response) -> {
                    try {
                        if(response.getInt("error") == 0) {
                            LoginPage loginPage = LoginPage.getLoginPage();
                            loginPage.showMessage("sign up successfully!");
                            currentFrame.dispose();
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
        this.add(signUpButton);
    }
}
