package base.games.screens;

import base.games.AppWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RefereeScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String sed_id;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public JPanel snapCenterPanel = new JPanel();
    public JButton matchButton = new JButton("Mecze");
    public JButton tournButton = new JButton("Turnieje");
    public RefereeScreen(AppWindow app, BodyScreen previous, String id) {
        parent = app;
        previousScreen = previous;
        sed_id = id;
        displayPanel.setLayout(new BorderLayout());
        returnButton.addActionListener(this);
        displayPanel.add(BorderLayout.SOUTH,returnButton);
        displayPanel.add(BorderLayout.CENTER,centerPanel);
        snapCenterPanel.setLayout(new BoxLayout(snapCenterPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(snapCenterPanel);
        matchButton.addActionListener(this);
        tournButton.addActionListener(this);
        try (Statement stmt = parent.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT imie, nazwisko FROM sedziowie " +
                     "WHERE sed_id = " + sed_id)) {
            if (rs.next()) {
                snapCenterPanel.add(new JLabel("Imię: " + rs.getString(1)));
                snapCenterPanel.add(new JLabel("Nazwisko: " + rs.getString(2)));
                snapCenterPanel.add(matchButton);
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
        if (object == matchButton)
        {
            parent.switchCurrentScreenTo(new RefereeMatchScreen(parent,this,sed_id));
        }
        if (object == tournButton)
        {
            parent.switchCurrentScreenTo(new RefereeTournScreen(parent,this,sed_id));
        }
    }
}
