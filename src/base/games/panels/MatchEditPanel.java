package base.games.panels;

import base.games.AppWindow;
import base.games.screens.BodyScreen;
import base.games.screens.RefereeMatchScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MatchEditPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public BodyScreen currentScreen;
    String id_meczu;
    public JButton setWhiteButton= new JButton("BIAŁE");
    public JButton setBlackButton= new JButton("CZARNE");
    public MatchEditPanel(AppWindow app, BodyScreen previous, String data, String b_imie, String b_nazw, String c_imie, String c_nazw, String id, String wynik) {
        super();
        parent = app;
        currentScreen = previous;
        id_meczu = id;
        setLayout(new GridLayout(1,8));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(data));
        add(new JLabel(b_imie));
        add(new JLabel(b_nazw));
        add(new JLabel(c_imie));
        add(new JLabel(c_nazw));
        add(new JLabel(wynik));
        setWhiteButton.addActionListener(this);
        setBlackButton.addActionListener(this);
        add(setWhiteButton);
        add(setBlackButton);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if (object == setWhiteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE mecze SET wynik_meczu='BIALE' WHERE id_meczu=" + id_meczu);
                System.out.println("Zmieniono "+ changes + " meczy.");
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            parent.switchCurrentScreenTo(new RefereeMatchScreen(parent, currentScreen.getPreviousScreen(), ((RefereeMatchScreen)currentScreen).sed_id));
        }
        if (object == setBlackButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE mecze SET wynik_meczu='CZARNE' WHERE id_meczu=" + id_meczu);
                System.out.println("Zmieniono "+ changes + " meczy.");
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }
            parent.switchCurrentScreenTo(new RefereeMatchScreen(parent, currentScreen.getPreviousScreen(), ((RefereeMatchScreen)currentScreen).sed_id));
        }
    }
}
