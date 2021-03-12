package base.games.admin.panels;

import base.games.AppWindow;
import base.games.admin.screens.AdmClubScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmClubPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmClubScreen previousScreen;
    public String pla_nazwa;
    public String pla_miejsce;
    public JButton writeButton = new JButton("NADPISZ");
    public JButton deleteButton = new JButton("USUŃ");
    public AdmClubPanel(AppWindow app, AdmClubScreen previous, String nazwa, String miejsce) {
        super();
        parent = app;
        previousScreen = previous;
        pla_nazwa = nazwa;
        pla_miejsce = miejsce;
        setLayout(new GridLayout(1,4));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(nazwa));
        add(new JLabel(miejsce));
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
            String newMiejsce = previousScreen.placeField.getText();
            if (newMiejsce.length() == 0) newMiejsce = pla_miejsce;
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE kluby SET miejsca_nazwa='" + newMiejsce + "' WHERE nazwa='" + pla_nazwa + "'");
                System.out.println("Zmieniono "+ changes + " klubów");
                parent.switchCurrentScreenTo(new AdmClubScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen));
            }
        }
        if (object == deleteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("DELETE FROM kluby WHERE nazwa='" + pla_nazwa + "'");
                System.out.println("Usunięto "+ changes + " klubów");
                parent.switchCurrentScreenTo(new AdmClubScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen));
            }
        }
    }
}
