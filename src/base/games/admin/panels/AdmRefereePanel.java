package base.games.admin.panels;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.screens.AdmRefereeScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class AdmRefereePanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmRefereeScreen previousScreen;
    public String sed_id;
    public String sed_imie;
    public String sed_nazw;
    public String uzy_id;
    public String uzy_nazwa;
    public JButton writeButton = new JButton("NADPISZ");
    public JButton deleteButton = new JButton("USUŃ");
    public AdmRefereePanel(AppWindow app, AdmRefereeScreen previous, String s_id, String imie, String nazwisko, String u_id, String uzytkownik) {
        super();
        parent = app;
        previousScreen = previous;
        sed_id = s_id;
        sed_imie = imie;
        sed_nazw = nazwisko;
        uzy_id = u_id;
        uzy_nazwa = uzytkownik;
        setLayout(new GridLayout(1,5));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(imie));
        add(new JLabel(nazwisko));
        add(new JLabel(uzytkownik));
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
            if (newImie.length() == 0) newImie = sed_imie;
            String newNazw = previousScreen.surnameField.getText();
            if (newNazw.length() == 0) newNazw = sed_nazw;
            Item uzy_item = (Item) previousScreen.uzyBox.getSelectedItem();
            String newUzy_id = uzy_id;
            if (!Objects.equals(uzy_item.toString(), "")) newUzy_id = (String) uzy_item.getValue();
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE sedziowie SET imie='" + newImie + "', nazwisko='" + newNazw + "', uzy_id=" + newUzy_id + " WHERE sed_id=" + sed_id);
                System.out.println("Zmieniono "+ changes + " sędziów");
                parent.switchCurrentScreenTo(new AdmRefereeScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"niepoprawne dane"));
            }
        }
        if (object == deleteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("DELETE FROM sedziowie WHERE sed_id=" + sed_id);
                System.out.println("Usunięto "+ changes + " sędziów");
                parent.switchCurrentScreenTo(new AdmRefereeScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"sędzia jest gdzieś przypisany"));
            }
        }
    }
}
