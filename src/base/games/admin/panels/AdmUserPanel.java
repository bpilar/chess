package base.games.admin.panels;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.screens.AdmUserScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Objects;

public class AdmUserPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmUserScreen previousScreen;
    public String uzy_id;
    public String uzy_nazwa;
    public String uzy_haslo;
    public String uzy_typ;
    public JButton writeButton = new JButton("NADPISZ");
    public JButton deleteButton = new JButton("USUŃ");
    public AdmUserPanel(AppWindow app, AdmUserScreen previous, String u_id, String nazwa, String haslo, String typ) {
        super();
        parent = app;
        previousScreen = previous;
        uzy_id = u_id;
        uzy_nazwa = nazwa;
        uzy_haslo = haslo;
        uzy_typ = typ;
        setLayout(new GridLayout(1,5));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(nazwa));
        add(new JLabel(haslo));
        add(new JLabel(typ));
        writeButton.addActionListener(this);
        deleteButton.addActionListener(this);
        add(writeButton);
        add(deleteButton);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if (object == writeButton)
        {
            String newNazwa = previousScreen.nameField.getText();
            if (newNazwa.length() == 0) newNazwa = uzy_nazwa;
            String newHaslo = previousScreen.passField.getText();
            if (newHaslo.length() == 0) newHaslo = uzy_haslo;
            Item typ_item = (Item) previousScreen.typeBox.getSelectedItem();
            if (Objects.equals(typ_item.toString(), "")) {
                try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                    int changes = stmt.executeUpdate("UPDATE uzytkownicy SET nazwa_uzy='" + newNazwa + "', haslo='" + newHaslo + "' WHERE uzy_id=" + uzy_id);
                    System.out.println("Zmieniono "+ changes + " użytkowników");
                    parent.switchCurrentScreenTo(new AdmUserScreen(parent,previousScreen.previousScreen));
                } catch (SQLException ex) {
                    System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                    parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"niepoprawne dane"));
                }
            }
            else {
                int count = 1;
                try (CallableStatement stmt = parent.conn.prepareCall("{? = call PoliczUzy(?)}")){
                    stmt.setString(2, uzy_id);
                    stmt.registerOutParameter(1, Types.INTEGER);
                    stmt.execute();
                    count = stmt.getInt(1);
                    if (Objects.equals(uzy_typ, "ADM")) {
                        try (Statement stmt2 = parent.conn.createStatement();
                             ResultSet rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM uzytkownicy WHERE typ='ADM'")) {
                            if (rs2.next()) {
                                if (rs2.getInt(1) == 1) {
                                    count++;
                                }
                            }
                        } catch (SQLException ex) {
                            System.out.println("Błąd wykonania polecenia: " + ex.getMessage());
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                    parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"we just don't know"));
                }
                if(count == 0) {
                    try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                        int changes = stmt.executeUpdate("UPDATE uzytkownicy SET nazwa_uzy='" + newNazwa + "', haslo='" + newHaslo + "', typ='" + typ_item.getValue() + "' WHERE uzy_id=" + uzy_id);
                        System.out.println("Zmieniono "+ changes + " użytkowników");
                        parent.switchCurrentScreenTo(new AdmUserScreen(parent,previousScreen.previousScreen));
                    } catch (SQLException ex) {
                        System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                        parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"niepoprawne dane"));
                    }
                }
                else {
                    parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"można zmienić typ tylko nieprzypisanym kontom"));
                }
            }
        }
        if (object == deleteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                if (Objects.equals(uzy_typ, "ADM")) {
                    try (Statement stmt2 = parent.conn.createStatement();
                         ResultSet rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM uzytkownicy WHERE typ='ADM'")) {
                        if (rs2.next()) {
                            if (rs2.getInt(1) != 1) {
                                int changes = stmt.executeUpdate("DELETE FROM uzytkownicy WHERE uzy_id=" + uzy_id);
                                System.out.println("Usunięto "+ changes + " użytkowników");
                                parent.switchCurrentScreenTo(new AdmUserScreen(parent,previousScreen.previousScreen));
                            }
                            else {
                                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"nie można usunąć wszystkich administratorów"));
                            }
                        }
                    } catch (SQLException ex) {
                        System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                    }
                }
                else {

                    int changes = stmt.executeUpdate("UPDATE zawodnicy SET uzy_id=NULL WHERE uzy_id=" + uzy_id);
                    System.out.println("Zmieniono "+ changes + " zawodników");
                    changes = stmt.executeUpdate("UPDATE trenerzy SET uzy_id=NULL WHERE uzy_id=" + uzy_id);
                    System.out.println("Zmieniono "+ changes + " trenerów");
                    changes = stmt.executeUpdate("UPDATE sedziowie SET uzy_id=NULL WHERE uzy_id=" + uzy_id);
                    System.out.println("Zmieniono "+ changes + " sędziów");
                    changes = stmt.executeUpdate("DELETE FROM uzytkownicy WHERE uzy_id=" + uzy_id);
                    System.out.println("Usunięto "+ changes + " użytkowników");
                    parent.switchCurrentScreenTo(new AdmUserScreen(parent,previousScreen.previousScreen));
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"we just don't know"));
            }
        }
    }
}
