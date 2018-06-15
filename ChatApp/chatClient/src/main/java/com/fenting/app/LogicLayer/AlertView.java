package com.fenting.app.LogicLayer;

import javax.swing.*;
import java.awt.*;

class AlertControl extends Thread
{
    private JLabel target;
    private JPanel panel;
    AlertControl(JLabel target, JPanel panel) {
        this.target = target;
        this.panel = panel;
    }

    public void run() {
        for(int i=0; i<10; ++i) {
            if(i % 2 != 0)
                target.setForeground(Color.BLACK);
            else
                target.setForeground(Color.RED);
            this.panel.repaint();
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.panel.removeAll();
    }
}

public class AlertView extends JPanel {
    static private AlertView alertView = null;
    static public AlertView getAlertView() {
        if(alertView == null) {
            alertView = new AlertView();
        }
        return alertView;
    }

    public void alert(String content) {
        JLabel alertLabel = new JLabel();
        alertLabel.setLayout(null);
        alertLabel.setBounds(0, 0, 600, 30);
        alertLabel.setText(content);
        alertLabel.setForeground(Color.RED);
        this.add(alertLabel);
        AlertControl alertControl = new AlertControl(alertLabel, this);
        alertControl.start();
    }

    public void message(String content) {

    }
}
