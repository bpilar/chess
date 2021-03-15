package base.games.admin.screens;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.panels.AdmCoachPanel;
import base.games.screens.BodyScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmCoachScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public inputPanel inputpanel;
    public JTextField nameField = new JTextField();
    public JTextField surnameField = new JTextField();
    public JComboBox<Item<String>> uzyBox = new JComboBox<Item<String>>();
    public JComboBox<Item<String>> kluBox = new JComboBox<Item<String>>();
    public JButton insertButton = new JButton("Wstaw");
    public JScrollPane scrollPane;
    public JPanel scrolledPanel = new JPanel();
    public AdmCoachScreen(AppWindow app, BodyScreen previous) {
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
             ResultSet rs = stmt.executeQuery("SELECT t.tre_id, t.imie, t.nazwisko, t.uzy_id, u.nazwa_uzy, t.klu_id, k.nazwa FROM trenerzy t, uzytkownicy u, kluby k WHERE t.uzy_id=u.uzy_id(+) AND k.klu_id=t.klu_id ORDER BY t.nazwisko,t.imie")) {
            while (rs.next()) {
                scrolledPanel.add(new AdmCoachPanel(parent,this,
                        rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7)));
            }
        } catch (SQLException ex) {
            System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
        }
        centerPanel.add(BorderLayout.CENTER,scrollPane);
    }

    class inputPanel extends JPanel {
        public inputPanel() {
            super();
            setLayout(new GridLayout(2,5));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,60));
            add(new JLabel("Imię"));
            add(new JLabel("Nazwisko"));
            add(new JLabel("Użytkownik"));
            add(new JLabel("Klub"));
            add(new JLabel(""));
            add(nameField);
            add(surnameField);
            uzyBox.addItem(new Item<String>("nowrite", ""));
            uzyBox.addItem(new Item<String>("NULL", "-brak-"));
            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT uzy_id, nazwa_uzy FROM uzytkownicy WHERE typ='TRE' AND uzy_id NOT IN (SELECT uzy_id FROM trenerzy WHERE uzy_id IS NOT NULL)")) {
                while (rs.next()) {
                    uzyBox.addItem(new Item<String>(rs.getString(1), rs.getString(2)));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            add(uzyBox);
            kluBox.addItem(new Item<String>("nowrite", ""));
            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT klu_id, nazwa FROM kluby")) {
                while (rs.next()) {
                    kluBox.addItem(new Item<String>(rs.getString(1), rs.getString(2)));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            add(kluBox);
            add(insertButton);
        }
    }

    static class heading extends JPanel {
        public heading() {
            super();
            setLayout(new GridLayout(1,6));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
            add(new JLabel("Imię"));
            add(new JLabel("Nazwisko"));
            add(new JLabel("Użytkownik"));
            add(new JLabel("Klub"));
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
                Item uzy_item = (Item) uzyBox.getSelectedItem();
                String uzy_id = (String) uzy_item.getValue();
                if (uzy_id == "nowrite") uzy_id = "NULL";
                Item klu_item = (Item) kluBox.getSelectedItem();
                String klu_id = (String) klu_item.getValue();
                if (klu_id == "nowrite") klu_id = "NULL";
                int changes = stmt.executeUpdate("INSERT INTO trenerzy(imie,nazwisko,uzy_id,klu_id) VALUES ('" + nameField.getText() + "','" + surnameField.getText() + "'," + uzy_id + "," + klu_id + ")");
                System.out.println("Wstawiono "+ changes + " trenerów");
                parent.switchCurrentScreenTo(new AdmCoachScreen(parent,previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"niepoprawne dane"));
            }
        }
    }
}
