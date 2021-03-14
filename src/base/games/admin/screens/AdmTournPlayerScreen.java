package base.games.admin.screens;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.panels.AdmTournPlayerPanel;
import base.games.screens.BodyScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmTournPlayerScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String tur_id;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public inputPanel inputpanel;
    public JComboBox<Item<String>> zawBox = new JComboBox<Item<String>>();
    public JButton insertButton = new JButton("Wstaw");
    public JScrollPane scrollPane;
    public JPanel scrolledPanel = new JPanel();
    public AdmTournPlayerScreen(AppWindow app, BodyScreen previous, String t_id) {
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
             ResultSet rs = stmt.executeQuery("SELECT u.tur_id, u.zaw_id, z.imie, z.nazwisko FROM udzialy u, zawodnicy z WHERE u.zaw_id=z.zaw_id AND u.tur_id=" + tur_id)) {
            while (rs.next()) {
                scrolledPanel.add(new AdmTournPlayerPanel(parent,this,
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
            add(new JLabel("Zawodnik"));
            add(new JLabel(""));
            zawBox.addItem(new Item<String>("nowrite", ""));
            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT zaw_id, imie || ' ' || nazwisko FROM zawodnicy " +
                         "WHERE zaw_id NOT IN (SELECT zaw_id FROM udzialy WHERE tur_id=" + tur_id + ") ORDER BY nazwisko, imie")) {
                while (rs.next()) {
                    zawBox.addItem(new Item<String>(rs.getString(1), rs.getString(2)));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            add(zawBox);
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
                Item zaw_item = (Item) zawBox.getSelectedItem();
                String zaw_id = (String) zaw_item.getValue();
                if (zaw_id == "nowrite") zaw_id = "NULL";
                int changes = stmt.executeUpdate("INSERT INTO udzialy(tur_id,zaw_id) VALUES (" + tur_id + "," + zaw_id + ")");
                System.out.println("Dodano "+ changes + " zawodników do turnieju");
                parent.switchCurrentScreenTo(new AdmTournPlayerScreen(parent,previousScreen,tur_id));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,this, "niepoprawne dane"));
            }
        }
    }
}
