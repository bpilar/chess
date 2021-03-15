package base.games.screens;

import base.games.AppWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CoachScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String tre_id;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public JPanel snapCenterPanel = new JPanel();
    public JButton playersButton = new JButton("Zawodnicy");
    public CoachScreen(AppWindow app, BodyScreen previous, String id) {
        parent = app;
        previousScreen = previous;
        tre_id = id;
        displayPanel.setLayout(new BorderLayout());
        returnButton.addActionListener(this);
        displayPanel.add(BorderLayout.SOUTH,returnButton);
        displayPanel.add(BorderLayout.CENTER,centerPanel);
        snapCenterPanel.setLayout(new BoxLayout(snapCenterPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(snapCenterPanel);
        playersButton.addActionListener(this);
        try (Statement stmt = parent.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT t.imie, t.nazwisko, k.nazwa, m.nazwa " +
                     "FROM trenerzy t,kluby k, miejsca m WHERE t.klu_id=k.klu_id AND k.mie_id=m.mie_id AND t.tre_id=" + tre_id)) {
            if (rs.next()) {
                snapCenterPanel.add(new JLabel("Imię: " + rs.getString(1)));
                snapCenterPanel.add(new JLabel("Nazwisko: " + rs.getString(2)));
                snapCenterPanel.add(new JLabel("Klub: " + rs.getString(3)));
                snapCenterPanel.add(new JLabel("Lokacja klubu: " + rs.getString(4)));
                snapCenterPanel.add(playersButton);
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
        if (object == playersButton)
        {
            parent.switchCurrentScreenTo(new CoachPlayerScreen(parent,this,tre_id));
        }
    }
}
