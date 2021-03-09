package base.games.screens;

import base.games.AppWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class PlayerScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String zaw_id;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public JPanel snapCenterPanel = new JPanel();
    public JButton tournButton = new JButton("Turnieje");
    public PlayerScreen(AppWindow app, BodyScreen previous, String id) {
        parent = app;
        previousScreen = previous;
        zaw_id = id;
        displayPanel.setLayout(new BorderLayout());
        returnButton.addActionListener(this);
        displayPanel.add(BorderLayout.SOUTH,returnButton);
        displayPanel.add(BorderLayout.CENTER,centerPanel);
        snapCenterPanel.setLayout(new BoxLayout(snapCenterPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(snapCenterPanel);
        tournButton.addActionListener(this);
        try (Statement stmt = parent.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT z.imie, z.nazwisko, z.narodowosc, z.punkty, k.nazwa, k.miejsca_nazwa FROM zawodnicy z JOIN kluby k " +
                     "ON z.kluby_nazwa=k.nazwa WHERE z.zaw_id = " + zaw_id)) {
            if (rs.next()) {
                snapCenterPanel.add(new JLabel("Imię: " + rs.getString(1)));
                snapCenterPanel.add(new JLabel("Nazwisko: " + rs.getString(2)));
                snapCenterPanel.add(new JLabel("Narodowosc: " + rs.getString(3)));
                snapCenterPanel.add(new JLabel("Punkty: " + rs.getString(4)));
                snapCenterPanel.add(new JLabel("Klub: " + rs.getString(5)));
                snapCenterPanel.add(new JLabel("Lokacja klubu: " + rs.getString(6)));
                snapCenterPanel.add(tournButton);
            }
        } catch (SQLException ex) {
            System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
        }

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

        if (object == returnButton)
        {
            parent.switchCurrentScreenTo(previousScreen);
        }
        if (object == tournButton)
        {
            parent.switchCurrentScreenTo(new PlayerTournScreen(parent,this,zaw_id));
        }
    }
}
