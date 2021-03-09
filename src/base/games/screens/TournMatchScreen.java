package base.games.screens;

import base.games.AppWindow;
import base.games.panels.MatchPanel;
import base.games.panels.TournPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TournMatchScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String tur_id;
    public String zaw_id;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public JScrollPane scrollPane;
    public JPanel scrolledPanel = new JPanel();
    public TournMatchScreen(AppWindow app, BodyScreen previous, String t_id, String z_id) {
        parent = app;
        previousScreen = previous;
        tur_id = t_id;
        zaw_id = z_id;
        displayPanel.setLayout(new BorderLayout());
        returnButton.addActionListener(this);
        displayPanel.add(BorderLayout.SOUTH,returnButton);
        displayPanel.add(BorderLayout.CENTER,centerPanel);
        centerPanel.setLayout(new BorderLayout());
        scrollPane = new JScrollPane(scrolledPanel);
        scrolledPanel.setLayout(new BoxLayout(scrolledPanel, BoxLayout.PAGE_AXIS));
        scrolledPanel.add(new heading());
        try (Statement stmt = parent.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TO_CHAR(m.data,'YYYY-MM-DD'), zb.imie, zb.nazwisko, zc.imie, zc.nazwisko, m.wynik_meczu, s.imie, s.nazwisko " +
                     "FROM mecze m, zawodnicy zb, zawodnicy zc, sedziowie s " +
                     "WHERE m.zaw_id_b=zb.zaw_id AND m.zaw_id_c=zc.zaw_id AND m.sed_id=s.sed_id AND m.tur_id=" + tur_id + " AND m.zaw_id_b=" + zaw_id +
                     " UNION ALL SELECT " +
                     "TO_CHAR(m.data,'YYYY-MM-DD'), zb.imie, zb.nazwisko, zc.imie, zc.nazwisko, m.wynik_meczu, s.imie, s.nazwisko " +
                     "FROM mecze m, zawodnicy zb, zawodnicy zc, sedziowie s " +
                     "WHERE m.zaw_id_b=zb.zaw_id AND m.zaw_id_c=zc.zaw_id AND m.sed_id=s.sed_id AND m.tur_id=" + tur_id + " AND m.zaw_id_c=" + zaw_id)) {
            while (rs.next()) {
                scrolledPanel.add(new MatchPanel(parent,
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
            add(new JLabel("Data"));
            add(new JLabel("Białe"));
            add(new JLabel(""));
            add(new JLabel("Czarne"));
            add(new JLabel(""));
            add(new JLabel("Wynik"));
            add(new JLabel("Sędzia"));
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
