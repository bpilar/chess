package base.games.admin.panels;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.screens.AdmPlayerScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class AdmPlayerPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmPlayerScreen previousScreen;
    public String zaw_id;
    public String zaw_imie;
    public String zaw_nazw;
    public String zaw_nar;
    public String uzy_id;
    public String uzy_nazwa;
    public String klu_id;
    public String klu_nazwa;
    public String tre_id;
    public String tre_nazwa;
    public JButton writeButton = new JButton("NADPISZ");
    public JButton deleteButton = new JButton("USUŃ");
    public AdmPlayerPanel(AppWindow app, AdmPlayerScreen previous, String z_id, String imie, String nazwisko, String narod, String u_id, String uzytkownik, String k_id, String klub, String t_id, String trener) {
        super();
        parent = app;
        previousScreen = previous;
        zaw_id = z_id;
        zaw_imie = imie;
        zaw_nazw = nazwisko;
        zaw_nar = narod;
        uzy_id = u_id;
        uzy_nazwa = uzytkownik;
        klu_id = k_id;
        klu_nazwa = klub;
        tre_id = t_id;
        tre_nazwa = trener;
        setLayout(new GridLayout(1,8));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(imie));
        add(new JLabel(nazwisko));
        add(new JLabel(narod));
        add(new JLabel(uzytkownik));
        add(new JLabel(klub));
        add(new JLabel(trener));
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
            String newImie = previousScreen.nameField.getText();
            if (newImie.length() == 0) newImie = zaw_imie;
            String newNazw = previousScreen.surnameField.getText();
            if (newNazw.length() == 0) newNazw = zaw_nazw;
            String newNar = previousScreen.nationField.getText();
            if (newNar.length() == 0) newNar = zaw_nar;
            Item uzy_item = (Item) previousScreen.uzyBox.getSelectedItem();
            String newUzy_id = uzy_id;
            if (!Objects.equals(uzy_item.toString(), "")) newUzy_id = (String) uzy_item.getValue();
            Item klu_item = (Item) previousScreen.kluBox.getSelectedItem();
            String newKlu_id = klu_id;
            if (!Objects.equals(klu_item.toString(), "")) newKlu_id = (String) klu_item.getValue();
            Item tre_item = (Item) previousScreen.treBox.getSelectedItem();
            String newTre_id = tre_id;
            if (!Objects.equals(tre_item.toString(), "")) newTre_id = (String) tre_item.getValue();
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE zawodnicy SET imie='" + newImie + "', nazwisko='" + newNazw +
                        "', narodowosc='" + newNar + "', uzy_id=" + newUzy_id + ", klu_id=" + newKlu_id + ", tre_id='" + newTre_id + "' WHERE zaw_id=" + zaw_id);
                System.out.println("Zmieniono "+ changes + " zawodników");
                parent.switchCurrentScreenTo(new AdmPlayerScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"niepoprawne dane"));
            }
        }
        if (object == deleteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("DELETE FROM zawodnicy WHERE zaw_id=" + zaw_id);
                System.out.println("Usunięto "+ changes + " zawodników");
                parent.switchCurrentScreenTo(new AdmPlayerScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"zawodnik jest gdzieś przypisany"));
            }
        }
    }
}
