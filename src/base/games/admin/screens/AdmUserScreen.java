package base.games.admin.screens;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.panels.AdmUserPanel;
import base.games.screens.BodyScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class AdmUserScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public inputPanel inputpanel;
    public JTextField nameField = new JTextField();
    public JTextField passField = new JTextField();
    public JComboBox<Item<String>> typeBox = new JComboBox<Item<String>>();
    public JButton insertButton = new JButton("Wstaw");
    public JScrollPane scrollPane;
    public JPanel scrolledPanel = new JPanel();
    public AdmUserScreen(AppWindow app, BodyScreen previous) {
        parent = app;
        previousScreen = previous;
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
             ResultSet rs = stmt.executeQuery("SELECT uzy_id, nazwa_uzy, haslo, typ FROM uzytkownicy ORDER BY nazwa_uzy")) {
            while (rs.next()) {
                scrolledPanel.add(new AdmUserPanel(parent,this,
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
            setLayout(new GridLayout(2,4));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,60));
            add(new JLabel("Nazwa"));
            add(new JLabel("Hasło"));
            add(new JLabel("Typ"));
            add(new JLabel(""));
            add(nameField);
            add(passField);
            typeBox.addItem(new Item<String>("", ""));
            typeBox.addItem(new Item<String>("ZAW", "zawodnik"));
            typeBox.addItem(new Item<String>("TRE", "trener"));
            typeBox.addItem(new Item<String>("SED", "sędzia"));
            typeBox.addItem(new Item<String>("ADM", "admin"));
            add(typeBox);
            add(insertButton);
        }
    }

    static class heading extends JPanel {
        public heading() {
            super();
            setLayout(new GridLayout(1,5));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
            add(new JLabel("Nazwa"));
            add(new JLabel("Hasło"));
            add(new JLabel("Typ"));
            add(new JLabel(""));
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
            Item typ_item = (Item) typeBox.getSelectedItem();
            if (Objects.equals(typ_item.toString(), "")) {
                parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"musi mieć typ"));
            }
            else {
                try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                    int changes = stmt.executeUpdate("INSERT INTO uzytkownicy(nazwa_uzy,haslo,typ) VALUES ('" +
                            nameField.getText() + "','" + passField.getText() + "','" + typ_item.getValue() + "')");
                    System.out.println("Wstawiono "+ changes + " użytkowników");
                    parent.switchCurrentScreenTo(new AdmUserScreen(parent,previousScreen));
                } catch (SQLException ex) {
                    System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                    parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"niepoprawne dane"));
                }
            }
        }
    }
}
