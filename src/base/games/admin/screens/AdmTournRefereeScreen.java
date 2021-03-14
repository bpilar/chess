package base.games.admin.screens;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.panels.AdmTournRefereePanel;
import base.games.screens.BodyScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmTournRefereeScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String tur_id;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public inputPanel inputpanel;
    public JComboBox<Item<String>> sedBox = new JComboBox<Item<String>>();
    public JButton insertButton = new JButton("Wstaw");
    public JScrollPane scrollPane;
    public JPanel scrolledPanel = new JPanel();
    public AdmTournRefereeScreen(AppWindow app, BodyScreen previous, String t_id) {
        parent = app;
        previousScreen = previous;
        tur_id = t_id;
        displayPanel.setLayout(new BorderLayout());
        returnButton.addActionListener(this);
        insertButton.addActionListener(this);
        displayPanel.add(BorderLayout.SOUTH,returnButton);
        displayPanel.add(BorderLayout.CENTER,centerPanel);
        centerPanel.setLayout(new BorderLayout());
        inputpanel = new inputPanel();
        centerPanel.add(BorderLayout.NORTH,inputpanel);


        scrollPane = new JScrollPane(scrolledPanel);
        scrolledPanel.setLayout(new BoxLayout(scrolledPanel, BoxLayout.PAGE_AXIS));
        scrolledPanel.add(new heading());
        try (Statement stmt = parent.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ts.tur_id, ts.sed_id, s.imie, s.nazwisko FROM turniej_sedzia ts, sedziowie s WHERE ts.sed_id=s.sed_id AND ts.tur_id=" + tur_id)) {
            while (rs.next()) {
                scrolledPanel.add(new AdmTournRefereePanel(parent,this,
                        rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)));
            }
        } catch (SQLException ex) {
            System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
        }
        centerPanel.add(BorderLayout.CENTER,scrollPane);
    }

    class inputPanel extends JPanel {
        public inputPanel() {
            super();
            setLayout(new GridLayout(2,2));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,60));
            add(new JLabel("Sędzia"));
            add(new JLabel(""));
            sedBox.addItem(new Item<String>("nowrite", ""));
            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT sed_id, imie || ' ' || nazwisko FROM sedziowie " +
                         "WHERE sed_id NOT IN (SELECT sed_id FROM turniej_sedzia WHERE tur_id=" + tur_id + ") ORDER BY nazwisko, imie")) {
                while (rs.next()) {
                    sedBox.addItem(new Item<String>(rs.getString(1), rs.getString(2)));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            add(sedBox);
            add(insertButton);
        }

    }

    static class heading extends JPanel {
        public heading() {
            super();
            setLayout(new GridLayout(1,3));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
            add(new JLabel("Imię"));
            add(new JLabel("Nazwisko"));
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
        if (object == insertButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                Item sed_item = (Item) sedBox.getSelectedItem();
                String sed_id = (String) sed_item.getValue();
                if (sed_id == "nowrite") sed_id = "NULL";
                int changes = stmt.executeUpdate("INSERT INTO turniej_sedzia(tur_id,sed_id) VALUES (" + tur_id + "," + sed_id + ")");
                System.out.println("Dodano "+ changes + " sędziów do turnieju");
                parent.switchCurrentScreenTo(new AdmTournRefereeScreen(parent,previousScreen,tur_id));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"niepoprawne dane"));
            }
        }
    }
}
