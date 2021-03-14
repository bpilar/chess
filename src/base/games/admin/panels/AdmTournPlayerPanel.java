package base.games.admin.panels;

import base.games.AppWindow;
import base.games.admin.screens.AdmTournPlayerScreen;
import base.games.admin.screens.AdmTournRefereeScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmTournPlayerPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmTournPlayerScreen previousScreen;
    public String tur_id;
    public String zaw_id;
    public String zaw_imie;
    public String zaw_nazm;
    public JButton deleteButton = new JButton("USUŃ");
    public AdmTournPlayerPanel(AppWindow app, AdmTournPlayerScreen previous, String t_id, String z_id, String imie, String nazwisko) {
        super();
        parent = app;
        previousScreen = previous;
        tur_id = t_id;
        zaw_id = z_id;
        zaw_imie = imie;
        zaw_nazm = nazwisko;
        setLayout(new GridLayout(1,3));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(imie));
        add(new JLabel(nazwisko));
        deleteButton.addActionListener(this);
        add(deleteButton);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if (object == deleteButton)
        {
            try (Statement stmt2 = parent.conn.createStatement();
                 ResultSet rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM mecze WHERE tur_id=" + tur_id + " AND (zaw_id_b=" + zaw_id + " OR zaw_id_c=" + zaw_id + ")")) {
                if (rs2.next()) {
                    if (rs2.getInt(1) == 0) {
                        try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                            int changes = stmt.executeUpdate("DELETE FROM udzialy WHERE tur_id=" + tur_id + " AND zaw_id=" + zaw_id);
                            System.out.println("Usunięto "+ changes + " zawodników z turnieju");
                            parent.switchCurrentScreenTo(new AdmTournRefereeScreen(parent,previousScreen.previousScreen,tur_id));
                        } catch (SQLException ex) {
                            System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                            parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"we just don't know"));
                        }
                    }
                    else {
                        parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"ten zawodnik ma przypisane mecze w tym turnieju"));
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }

        }
    }
}
