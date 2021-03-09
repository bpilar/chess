package base.games.panels;

import base.games.AppWindow;
import base.games.screens.BodyScreen;
import base.games.screens.PlayerTournScreen;
import base.games.screens.TournMatchScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MatchPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public MatchPanel(AppWindow app, String data, String b_imie, String b_nazw, String c_imie, String c_nazw, String wynik, String s_imie, String s_nazw) {
        super();
        parent = app;
        setLayout(new GridLayout(1,8));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(data));
        add(new JLabel(b_imie));
        add(new JLabel(b_nazw));
        add(new JLabel(c_imie));
        add(new JLabel(c_nazw));
        add(new JLabel(wynik));
        add(new JLabel(s_imie));
        add(new JLabel(s_nazw));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
