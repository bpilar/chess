package base.games.admin.panels;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.screens.AdmClubScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class AdmClubPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmClubScreen previousScreen;
    public String klu_id;
    public String klu_nazwa;
    public String mie_id;
    public String mie_nazwa;
    public JButton writeButton = new JButton("NADPISZ");
    public JButton deleteButton = new JButton("USUŃ");
    public AdmClubPanel(AppWindow app, AdmClubScreen previous, String k_id, String klub, String m_id, String miejsce) {
        super();
        parent = app;
        previousScreen = previous;
        klu_id = k_id;
        klu_nazwa = klub;
        mie_id = m_id;
        mie_nazwa = miejsce;
        setLayout(new GridLayout(1,4));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(klub));
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
            String newNazwa = previousScreen.nameField.getText();
            if (newNazwa.length() == 0) newNazwa = klu_nazwa;
            Item mie_item = (Item) previousScreen.mieBox.getSelectedItem();
            String newMie_id = mie_id;
            if (!Objects.equals(mie_item.toString(), "")) newMie_id = (String) mie_item.getValue();
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE kluby SET nazwa='" + newNazwa + "', mie_id=" + newMie_id + " WHERE klu_id=" + klu_id);
                System.out.println("Zmieniono "+ changes + " klubów");
                parent.switchCurrentScreenTo(new AdmClubScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"niepoprawne dane"));
            }
        }
        if (object == deleteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("DELETE FROM kluby WHERE klu_id=" + klu_id);
                System.out.println("Usunięto "+ changes + " klubów");
                parent.switchCurrentScreenTo(new AdmClubScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"klub jest gdzieś przypisany"));
            }
        }
    }
}
