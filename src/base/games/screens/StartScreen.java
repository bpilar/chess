package base.games.screens;

import base.games.AppWindow;
import base.games.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel;
    public AppWindow parent;
    public BodyScreen previousScreen;
    public JPanel snapCenterPanel;
    public JButton startButton = new JButton("Continue");
    public JButton reconnectButton = new JButton("Reconnect");
    public StartScreen(AppWindow app, BodyScreen previous) {
        parent = app;
        previousScreen = previous;
        displayPanel = new JPanel();
        displayPanel.setBackground(Color.GRAY);
        snapCenterPanel = new JPanel();
        snapCenterPanel.setLayout(new BoxLayout(snapCenterPanel, BoxLayout.PAGE_AXIS));
        displayPanel.add(snapCenterPanel);
        startButton.addActionListener(this);
        reconnectButton.addActionListener(this);


        ConnectOracle();
    }

    public void ConnectOracle() {
        snapCenterPanel.add(new JLabel("Connecting:"));
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                String connectionString = "jdbc:oracle:thin:@//admlab2.cs.put.poznan.pl:1521/"
                        +"dblab02_students.cs.put.poznan.pl";
                Properties connectionProps = new Properties();
                connectionProps.put("user", "inf141300");
                connectionProps.put("password", "inf141300");
                try {
                    parent.conn = DriverManager.getConnection(connectionString,connectionProps);
                    parent.connected = true;
                    snapCenterPanel.add(new JLabel("connected"));
                    snapCenterPanel.add(startButton);
                    System.out.println("Połączono z bazą danych");
                } catch (SQLException ex) {
                    snapCenterPanel.add(new JLabel("failed"));
                    snapCenterPanel.add(reconnectButton);
                    System.out.println("Nie połączono z bazą danych");
//                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Nie udało się połączyć z bazą danych", ex);
                    //System.exit(-1);
                }
                parent.body.updateUI();
            }
        });
    }

    @Override
    public JPanel getDisplayPanel() {
        return displayPanel;
    }

    @Override
    public BodyScreen getPreviousScreen() {
        return previousScreen;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();
        if(object == startButton)
        {
            parent.switchCurrentScreenTo(new LoginScreen(parent, null));
        }
        if(object == reconnectButton)
        {
            parent.switchCurrentScreenTo(new StartScreen(parent, null));
        }
    }
}
