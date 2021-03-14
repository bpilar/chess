package base.games.admin.panels;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.screens.AdmTournScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class AdmTournPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmTournScreen previousScreen;
    public String tur_id;
    public String tur_nazwa;
    public String tur_data;
    public String mie_id;
    public String mie_nazwa;
    public String sez_id;;
    public String sez_nazwa;
    public JButton coachButton = new JButton("Dodaj sędziego");
    public JButton playerButton = new JButton("Dodaj zawodnika");
    public JButton writeButton = new JButton("NADPISZ");
    public JButton deleteButton = new JButton("USUŃ");
    public AdmTournPanel(AppWindow app, AdmTournScreen previous, String t_id, String t_nazwa, String t_data, String m_id, String miejsce, String s_id, String sezon) {
        super();
        parent = app;
        previousScreen = previous;
        tur_id = t_id;
        tur_nazwa = t_nazwa;
        tur_data = t_data;
        mie_id = m_id;
        mie_nazwa = miejsce;
        sez_id = s_id;
        sez_nazwa = sezon;
        setLayout(new GridLayout(1,6));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(t_nazwa));
        add(new JLabel(t_data));
        add(new JLabel(miejsce));
        add(new JLabel(sezon));
        coachButton.addActionListener(this);
        playerButton.addActionListener(this);
        writeButton.addActionListener(this);
        deleteButton.addActionListener(this);
        add(coachButton);
        add(playerButton);
        add(writeButton);
        add(deleteButton);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if (object == coachButton)
        {
            //TODO
        }
        if (object == playerButton)
        {
            //TODO
        }
        if (object == writeButton)
        {
            String newNazwa = previousScreen.nameField.getText();
            if (newNazwa.length() == 0) newNazwa = tur_nazwa;
            String newData = previousScreen.dateField.getText();
            if (newData.length() == 0) newData = tur_data;
            Item mie_item = (Item) previousScreen.mieBox.getSelectedItem();
            String newMie_id = mie_id;
            if (!Objects.equals(mie_item.toString(), "")) newMie_id = (String) mie_item.getValue();
            Item sez_item = (Item) previousScreen.sezBox.getSelectedItem();
            String newSez_id = sez_id;
            if (!Objects.equals(sez_item.toString(), "")) newSez_id = (String) sez_item.getValue();
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE turnieje SET nazwa='" + newNazwa +
                        "', data_roz='" + newData + "', miejsca_nazwa='" + newMie_id + "', sez_id=" + newSez_id + " WHERE tur_id=" + tur_id);
                System.out.println("Zmieniono "+ changes + " turniejów");
                parent.switchCurrentScreenTo(new AdmTournScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen));
            }
        }
        if (object == deleteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("DELETE FROM turnieje WHERE tur_id=" + tur_id);
                System.out.println("Usunięto "+ changes + " turniejów");
                parent.switchCurrentScreenTo(new AdmTournScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen));
            }
        }
    }
}
