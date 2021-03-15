package base.games.admin.panels;

import base.games.AppWindow;
import base.games.admin.screens.AdmPlaceScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmPlacePanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmPlaceScreen previousScreen;
    public String pla_id;
    public String pla_nazwa;
    public String pla_adres;
    public JButton writeButton = new JButton("NADPISZ");
    public JButton deleteButton = new JButton("USUŃ");
    public AdmPlacePanel(AppWindow app, AdmPlaceScreen previous, String m_id, String nazwa, String adres) {
        super();
        parent = app;
        previousScreen = previous;
        pla_id = m_id;
        pla_nazwa = nazwa;
        pla_adres = adres;
        setLayout(new GridLayout(1,4));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(nazwa));
        add(new JLabel(adres));
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
            if (newNazwa.length() == 0) newNazwa = pla_nazwa;
            String newAdres = previousScreen.addresField.getText();
            if (newAdres.length() == 0) newAdres = pla_adres;
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE miejsca SET nazwa='" + newNazwa + "', adres='" + newAdres + "' WHERE mie_id='" + pla_id + "'");
                System.out.println("Zmieniono "+ changes + " miejsc");
                parent.switchCurrentScreenTo(new AdmPlaceScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"niepoprawne dane"));
            }
        }
        if (object == deleteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("DELETE FROM miejsca WHERE nazwa='" + pla_nazwa + "'");
                System.out.println("Usunięto "+ changes + " miejsc");
                parent.switchCurrentScreenTo(new AdmPlaceScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"miejsce jest gdzieś przypisane"));
            }
        }
    }
}
