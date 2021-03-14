package base.games.admin.panels;

import base.games.AppWindow;
import base.games.admin.screens.AdmSeasonScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmSeasonPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmSeasonScreen previousScreen;
    public String sez_id;
    public String sez_nazwa;
    public String sez_rok;
    public JButton writeButton = new JButton("NADPISZ");
    public JButton deleteButton = new JButton("USUŃ");
    public AdmSeasonPanel(AppWindow app, AdmSeasonScreen previous, String s_id, String nazwa, String rok) {
        super();
        parent = app;
        previousScreen = previous;
        sez_id = s_id;
        sez_nazwa = nazwa;
        sez_rok = rok;
        setLayout(new GridLayout(1,4));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(nazwa));
        add(new JLabel(rok));
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
            if (newNazwa.length() == 0) newNazwa = sez_nazwa;
            String newRok = previousScreen.yearField.getText();
            if (newRok.length() == 0) newRok = sez_rok;
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE sezony SET nazwa='" + newNazwa + "', rok='" + newRok + "-01-01' WHERE sez_id=" + sez_id);
                System.out.println("Zmieniono "+ changes + " sezonów");
                parent.switchCurrentScreenTo(new AdmSeasonScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"niepoprawne dane"));
            }
        }
        if (object == deleteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("DELETE FROM sezony WHERE sez_id=" + sez_id);
                System.out.println("Usunięto "+ changes + " sezonów");
                parent.switchCurrentScreenTo(new AdmSeasonScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"sezon nie jest pusty"));
            }
        }
    }
}
