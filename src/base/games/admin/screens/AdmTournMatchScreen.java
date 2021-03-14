package base.games.admin.screens;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.panels.AdmTournMatchPanel;
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

public class AdmTournMatchScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String tur_id;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public inputPanel inputpanel;
    public JTextField dateField = new JTextField();
    public JComboBox<Item<String>> sedBox = new JComboBox<Item<String>>();
    public JComboBox<Item<String>> zawBBox = new JComboBox<Item<String>>();
    public JComboBox<Item<String>> zawCBox = new JComboBox<Item<String>>();
    public JButton insertButton = new JButton("Wstaw");
    public JScrollPane scrollPane;
    public JPanel scrolledPanel = new JPanel();
    public AdmTournMatchScreen(AppWindow app, BodyScreen previous, String t_id) {
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
             ResultSet rs = stmt.executeQuery("SELECT m.id_meczu, TO_CHAR(m.data,'YYYY-MM-DD') AS dzien, " +
                     "m.sed_id, s.imie || ' ' || s.nazwisko, m.zaw_id_b, zb.imie || ' ' || zb.nazwisko, m.zaw_id_c, zc.imie || ' ' || zc.nazwisko, m.wynik_meczu " +
                     "FROM mecze m, zawodnicy zb, zawodnicy zc, sedziowie s " +
                     "WHERE m.zaw_id_b=zb.zaw_id AND m.zaw_id_c=zc.zaw_id AND m.sed_id=s.sed_id AND m.tur_id=" + tur_id + " ORDER BY dzien")) {
            while (rs.next()) {
                scrolledPanel.add(new AdmTournMatchPanel(parent,this,tur_id,
                        rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),
                        rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)));
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
            add(new JLabel("Data rozegrania"));
            add(new JLabel("Sędzia"));
            add(new JLabel("Grał białe"));
            add(new JLabel("Grał czarne"));
            add(new JLabel(""));
            add(dateField);
            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT sed_id, imie || ' ' || nazwisko FROM sedziowie " +
                         "WHERE sed_id IN (SELECT sed_id FROM turniej_sedzia WHERE tur_id=" + tur_id + ") ORDER BY nazwisko, imie")) {
                while (rs.next()) {
                    sedBox.addItem(new Item<String>(rs.getString(1), rs.getString(2)));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            add(sedBox);
            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT zaw_id, imie || ' ' || nazwisko FROM zawodnicy " +
                         "WHERE zaw_id IN (SELECT zaw_id FROM udzialy WHERE tur_id=" + tur_id + ") ORDER BY nazwisko, imie")) {
                while (rs.next()) {
                    zawBBox.addItem(new Item<String>(rs.getString(1), rs.getString(2)));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            add(zawBBox);
            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT zaw_id, imie || ' ' || nazwisko FROM zawodnicy " +
                         "WHERE zaw_id IN (SELECT zaw_id FROM udzialy WHERE tur_id=" + tur_id + ") ORDER BY nazwisko, imie")) {
                while (rs.next()) {
                    zawCBox.addItem(new Item<String>(rs.getString(1), rs.getString(2)));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            add(zawCBox);
            add(insertButton);
        }

    }

    static class heading extends JPanel {
        public heading() {
            super();
            setLayout(new GridLayout(1,6));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
            add(new JLabel("Data rozegrania"));
            add(new JLabel("Sędzia"));
            add(new JLabel("Grał białe"));
            add(new JLabel("Grał czarne"));
            add(new JLabel("Wynik"));
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
                Item zawb_item = (Item) zawBBox.getSelectedItem();
                String zawb_id = (String) zawb_item.getValue();
                Item zawc_item = (Item) zawCBox.getSelectedItem();
                String zawc_id = (String) zawc_item.getValue();
                if (!Objects.equals(zawb_id, zawc_id)) {
                    int changes = stmt.executeUpdate("INSERT INTO mecze(wynik_meczu,data,sed_id,tur_id,zaw_id_b,zaw_id_c) VALUES ('NIEROZEGRANY','" +
                            dateField.getText() + "'," + sed_id +"," + tur_id + "," + zawb_id + "," + zawc_id + ")");
                    System.out.println("Dodano "+ changes + " meczów do turnieju");
                    parent.switchCurrentScreenTo(new AdmTournMatchScreen(parent,previousScreen,tur_id));
                }
                else {
                    parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"zawodnik nie może grać ze sobą"));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"niepoprawne dane"));
            }
        }
    }
}
