package base.games.admin.screens;

import base.games.AppWindow;
import base.games.admin.panels.AdmClubPanel;
import base.games.screens.BodyScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmClubScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public inputPanel inputpanel;
    public JTextField nameField = new JTextField();
    public JTextField placeField = new JTextField();
    public JButton insertButton = new JButton("Wstaw");
    public JScrollPane scrollPane;
    public JPanel scrolledPanel = new JPanel();
    public AdmClubScreen(AppWindow app, BodyScreen previous) {
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
             ResultSet rs = stmt.executeQuery("SELECT nazwa, miejsca_nazwa FROM kluby ORDER BY nazwa")) {
            while (rs.next()) {
                scrolledPanel.add(new AdmClubPanel(parent,this,
                        rs.getString(1),rs.getString(2)));
            }
        } catch (SQLException ex) {
            System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
        }
        centerPanel.add(BorderLayout.CENTER,scrollPane);
    }

    class inputPanel extends JPanel {
        public inputPanel() {
            super();
            setLayout(new GridLayout(2,3));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,60));
            add(new JLabel("Nazwa"));
            add(new JLabel("Miejsce"));
            add(new JLabel(""));
            add(nameField);
            add(placeField);
            add(insertButton);
        }

    }

    static class heading extends JPanel {
        public heading() {
            super();
            setLayout(new GridLayout(1,4));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
            add(new JLabel("Nazwa"));
            add(new JLabel("Miejsce"));
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
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("INSERT INTO kluby(nazwa,miejsca_nazwa) VALUES ('" + nameField.getText() + "','" + placeField.getText() + "')");
                System.out.println("Wstawiono "+ changes + " klubów");
                parent.switchCurrentScreenTo(new AdmClubScreen(parent,previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"niepoprawne dane"));
            }
        }
    }
}
