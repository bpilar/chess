package base.games.admin.panels;

import base.games.AppWindow;
import base.games.admin.screens.AdmTournMatchScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class AdmTournMatchPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmTournMatchScreen previousScreen;
    public String tur_id;
    public String mec_id;
    public String mec_dzien;
    public String sed_id;
    public String sed_nazwa;
    public String zawb_id;
    public String zawb_nazwa;
    public String zawc_id;
    public String zawc_nazwa;
    public String wynik;
    public JButton deleteButton = new JButton("USUŃ");
    public AdmTournMatchPanel(AppWindow app, AdmTournMatchScreen previous, String t_id, String m_id, String m_dzien,
                              String s_id, String sedzia, String zb_id, String biale, String zc_id, String czarne, String wyn) {
        super();
        parent = app;
        previousScreen = previous;
        tur_id = t_id;
        mec_id = m_id;
        mec_dzien = m_dzien;
        sed_id = s_id;
        sed_nazwa = sedzia;
        zawb_id = zb_id;
        zawb_nazwa = biale;
        zawc_id = zc_id;
        zawc_nazwa = czarne;
        wynik = wyn;
        setLayout(new GridLayout(1,6));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(m_dzien));
        add(new JLabel(sedzia));
        add(new JLabel(biale));
        add(new JLabel(czarne));
        add(new JLabel(wyn));
        deleteButton.addActionListener(this);
        add(deleteButton);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if (object == deleteButton)
        {
            try (Statement stmt2 = parent.conn.createStatement();
                 ResultSet rs2 = stmt2.executeQuery("SELECT wynik_meczu FROM mecze WHERE id_meczu=" + mec_id )) {
                if (rs2.next()) {
                    if (Objects.equals(rs2.getString(1), "NIEROZEGRANY")) {
                        try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                            int changes = stmt.executeUpdate("DELETE FROM mecze WHERE id_meczu=" + mec_id);
                            System.out.println("Usunięto "+ changes + " meczów z turnieju");
                            parent.switchCurrentScreenTo(new AdmTournMatchScreen(parent,previousScreen.previousScreen,tur_id));
                        } catch (SQLException ex) {
                            System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                            parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"we just don't know"));
                        }
                    }
                    else {
                        parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"mecz został rozegrany"));
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }

        }
    }
}
