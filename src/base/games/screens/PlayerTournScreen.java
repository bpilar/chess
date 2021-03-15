package base.games.screens;

import base.games.AppWindow;
import base.games.panels.TournPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PlayerTournScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String zaw_id;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public JScrollPane scrollPane;
    public JPanel scrolledPanel = new JPanel();
    public PlayerTournScreen(AppWindow app, BodyScreen previous, String id) {
        parent = app;
        previousScreen = previous;
        zaw_id = id;
        displayPanel.setLayout(new BorderLayout());
        returnButton.addActionListener(this);
        displayPanel.add(BorderLayout.SOUTH,returnButton);
        displayPanel.add(BorderLayout.CENTER,centerPanel);
        centerPanel.setLayout(new BorderLayout());
        scrollPane = new JScrollPane(scrolledPanel);
        scrolledPanel.setLayout(new BoxLayout(scrolledPanel, BoxLayout.PAGE_AXIS));
        scrolledPanel.add(new heading());
        try (Statement stmt = parent.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT t.tur_id, t.nazwa, TO_CHAR(t.data_roz,'YYYY-MM-DD'), m.nazwa, s.nazwa, TO_CHAR(s.rok,'YYYY'), u.wynik_w_turnieju, u.miejsce_w_turnieju " +
                     "FROM udzialy u, turnieje t, sezony s, miejsca m " +
                     "WHERE u.tur_id=t.tur_id AND t.sez_id=s.sez_id(+) AND t.mie_id=m.mie_id AND u.zaw_id=" + zaw_id + " ORDER BY s.rok, t.data_roz")) {
            while (rs.next()) {
                scrolledPanel.add(new TournPanel(parent,this,
                        rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),
                        rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8)));
            }
        } catch (SQLException ex) {
            System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
        }
        centerPanel.add(BorderLayout.CENTER,scrollPane);
    }

    static class heading extends JPanel {
        public heading() {
            super();
            setLayout(new GridLayout(1,8));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
            add(new JLabel("Turniej"));
            add(new JLabel("Rozpoczęcie"));
            add(new JLabel("Miejsce"));
            add(new JLabel("Sezon"));
            add(new JLabel("Rok"));
            add(new JLabel("Wynik"));
            add(new JLabel("Miejsce"));
            add(new JLabel(""));
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
    }
}
