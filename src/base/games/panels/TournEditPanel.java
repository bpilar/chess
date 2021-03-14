package base.games.panels;

import base.games.AppWindow;
import base.games.screens.BodyScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TournEditPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String tur_id;
    public JButton summarizeButton = new JButton("PODSUMUJ");
    public TournEditPanel(AppWindow app, BodyScreen previous, String t_id, String t_nazwa, String t_data, String t_miejsce, String s_nazwa, String s_rok) {
        super();
        parent = app;
        previousScreen = previous;
        tur_id = t_id;
        setLayout(new GridLayout(1,6));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(t_nazwa));
        add(new JLabel(t_data));
        add(new JLabel(t_miejsce));
        add(new JLabel(s_nazwa));
        add(new JLabel(s_rok));
        summarizeButton.addActionListener(this);
        add(summarizeButton);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if (object == summarizeButton)
        {
            try (Statement stmt2 = parent.conn.createStatement();
                 ResultSet rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM (SELECT * FROM udzialy WHERE wynik_w_turnieju IS NOT NULL AND tur_id=" + tur_id + ")")) {
                if (rs2.next()) {
                    if (rs2.getInt(1) == 0) {
                        try (CallableStatement stmt = parent.conn.prepareCall("{call PodsumujTurniej(?)}")){
                            stmt.setString(1, tur_id);
                            stmt.execute();
                        } catch (SQLException ex) {
                            System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                        }
                    }
                    else {
                        parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"turniej już raz podsumowano"));
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }

        }
    }
}
