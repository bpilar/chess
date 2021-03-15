package base.games.admin.panels;

import base.games.AppWindow;
import base.games.Item;
import base.games.admin.screens.AdmCoachScreen;
import base.games.screens.ErrorScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class AdmCoachPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public AdmCoachScreen previousScreen;
    public String tre_id;
    public String tre_imie;
    public String tre_nazw;
    public String uzy_id;
    public String uzy_nazwa;
    public String klu_id;
    public String klu_nazwa;
    public JButton writeButton = new JButton("NADPISZ");
    public JButton deleteButton = new JButton("USUŃ");
    public AdmCoachPanel(AppWindow app, AdmCoachScreen previous, String t_id, String imie, String nazwisko, String u_id, String uzytkownik, String k_id, String klub) {
        super();
        parent = app;
        previousScreen = previous;
        tre_id = t_id;
        tre_imie = imie;
        tre_nazw = nazwisko;
        uzy_id = u_id;
        uzy_nazwa = uzytkownik;
        klu_id = k_id;
        klu_nazwa = klub;
        setLayout(new GridLayout(1,6));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(imie));
        add(new JLabel(nazwisko));
        add(new JLabel(uzytkownik));
        add(new JLabel(klub));
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
            String newImie = previousScreen.nameField.getText();
            if (newImie.length() == 0) newImie = tre_imie;
            String newNazw = previousScreen.surnameField.getText();
            if (newNazw.length() == 0) newNazw = tre_nazw;
            Item uzy_item = (Item) previousScreen.uzyBox.getSelectedItem();
            String newUzy_id = uzy_id;
            if (!Objects.equals(uzy_item.toString(), "")) newUzy_id = (String) uzy_item.getValue();
            Item klu_item = (Item) previousScreen.kluBox.getSelectedItem();
            String newKlu_id = klu_id;
            if (!Objects.equals(klu_item.toString(), "")) newKlu_id = (String) klu_item.getValue();
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("UPDATE trenerzy SET imie='" + newImie + "', nazwisko='" + newNazw + "', uzy_id=" + newUzy_id + ", klu_id=" + newKlu_id + " WHERE tre_id=" + tre_id);
                System.out.println("Zmieniono "+ changes + " trenerów");
                parent.switchCurrentScreenTo(new AdmCoachScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"niepoprawne dane"));
            }
        }
        if (object == deleteButton)
        {
            try (Statement stmt = parent.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);) {
                int changes = stmt.executeUpdate("DELETE FROM trenerzy WHERE tre_id=" + tre_id);
                System.out.println("Usunięto "+ changes + " trenerów");
                parent.switchCurrentScreenTo(new AdmCoachScreen(parent,previousScreen.previousScreen));
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                parent.switchCurrentScreenTo(new ErrorScreen(parent,previousScreen,"trener jest gdzieś przypisany"));
            }
        }
    }
}
