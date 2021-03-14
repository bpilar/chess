package base.games.admin.panels;

import base.games.AppWindow;
import base.games.admin.screens.AdmTournRefereeScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdmTournRefereePanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmTournRefereeScreen previousScreen;
    public String tur_id;
    public String sed_id;
    public String sed_imie;
    public String sed_nazm;
    public JButton deleteButton = new JButton("USUŃ");
    public AdmTournRefereePanel(AppWindow app, AdmTournRefereeScreen previous, String t_id, String s_id, String imie, String nazwisko) {
        super();
        parent = app;
        previousScreen = previous;
        tur_id = t_id;
        sed_id = s_id;
        sed_imie = imie;
        sed_nazm = nazwisko;
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
                 ResultSet rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM mecze WHERE tur_id=" + tur_id + " AND sed_id=" + sed_id)) {
                if (rs2.next()) {
                    if (rs2.getInt(1) == 0) {
                        try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                            int changes = stmt.executeUpdate("DELETE FROM turniej_sedzia WHERE tur_id=" + tur_id + " AND sed_id=" + sed_id);
                            System.out.println("Usunięto "+ changes + " sędziów z turnieju");
                            parent.switchCurrentScreenTo(new AdmTournRefereeScreen(parent,previousScreen.previousScreen,tur_id));
                        } catch (SQLException ex) {
                            System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                            parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"we just don't know"));
                        }
                    }
                    else {
                        parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"ten sędzia ma przypisane mecze w tym turnieju"));
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }

        }
    }
}
