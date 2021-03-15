package base.games.admin.screens;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.panels.AdmTournPanel;
import base.games.screens.BodyScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmTournScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public inputPanel inputpanel;
    public JTextField nameField = new JTextField();
    public JTextField dateField = new JTextField();
    public JComboBox<Item<String>> mieBox = new JComboBox<Item<String>>();
    public JComboBox<Item<String>> sezBox = new JComboBox<Item<String>>();
    public JButton insertButton = new JButton("Wstaw");
    public JScrollPane scrollPane;
    public JPanel scrolledPanel = new JPanel();
    public AdmTournScreen(AppWindow app, BodyScreen previous) {
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
             ResultSet rs = stmt.executeQuery("SELECT t.tur_id, t.nazwa, TO_CHAR(t.data_roz,'YYYY-MM-DD'), m.mie_id ,m.nazwa || ' ' || m.adres, s.sez_id, s.nazwa || ' ' || TO_CHAR(s.rok,'YYYY') " +
                     "FROM turnieje t, sezony s, miejsca m WHERE t.mie_id=m.mie_id AND t.sez_id=s.sez_id(+) ORDER BY s.rok, t.data_roz")) {
            while (rs.next()) {
                scrolledPanel.add(new AdmTournPanel(parent,this,
                        rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),
                        rs.getString(5),rs.getString(6),rs.getString(7)));
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
            add(new JLabel("Nazwa"));
            add(new JLabel("Data rozpoczęcia"));
            add(new JLabel("Miejsce"));
            add(new JLabel("Sezon"));
            add(new JLabel(""));
            add(nameField);
            add(dateField);
            mieBox.addItem(new Item<String>("nowrite", ""));
            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT mie_id, nazwa || ' ' || adres FROM miejsca ORDER BY nazwa")) {
                while (rs.next()) {
                    mieBox.addItem(new Item<String>(rs.getString(1), rs.getString(2)));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            add(mieBox);
            sezBox.addItem(new Item<String>("nowrite", ""));
            sezBox.addItem(new Item<String>("NULL", "-brak-"));
            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT sez_id, nazwa || ' ' || TO_CHAR(rok,'YYYY') FROM sezony ORDER BY rok, nazwa")) {
                while (rs.next()) {
                    sezBox.addItem(new Item<String>(rs.getString(1), rs.getString(2)));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            add(sezBox);
            add(insertButton);
        }
    }

    static class heading extends JPanel {
        public heading() {
            super();
            setLayout(new GridLayout(1,9));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
            add(new JLabel("Nazwa"));
            add(new JLabel("Data rozpoczęcia"));
            add(new JLabel("Miejsce"));
            add(new JLabel("Sezon"));
            add(new JLabel(""));
            add(new JLabel(""));
            add(new JLabel(""));
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
                Item mie_item = (Item) mieBox.getSelectedItem();
                String mie_id = (String) mie_item.getValue();
                if (mie_id == "nowrite") mie_id = "NULL";
                Item sez_item = (Item) sezBox.getSelectedItem();
                String sez_id = (String) sez_item.getValue();
                if (sez_id == "nowrite") sez_id = "NULL";
                int changes = stmt.executeUpdate("INSERT INTO turnieje(nazwa,data_roz,mie_id,sez_id) VALUES ('" +
                        nameField.getText() + "','" + dateField.getText() + "'," + mie_id + "," + sez_id + ")");
                System.out.println("Wstawiono "+ changes + " turniejów");
                parent.switchCurrentScreenTo(new AdmTournScreen(parent,previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"niepoprawne dane"));
            }
        }
    }
}
