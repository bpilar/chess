package base.games.panels;

import base.games.AppWindow;
import base.games.screens.BodyScreen;
import base.games.screens.PlayerTournScreen;
import base.games.screens.TournMatchScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String zaw_id;
    public JButton chooseButton = new JButton("SHOW");
    public PlayerPanel(AppWindow app, BodyScreen previous, String z_id, String imie, String nazw, String narodow, String punkty) {
        super();
        parent = app;
        previousScreen = previous;
        zaw_id = z_id;
        setLayout(new GridLayout(1,5));
        setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        add(new JLabel(imie));
        add(new JLabel(nazw));
        add(new JLabel(narodow));
        add(new JLabel(punkty));
        chooseButton.addActionListener(this);
        add(chooseButton);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if (object == chooseButton)
        {
            parent.switchCurrentScreenTo(new PlayerTournScreen(parent,previousScreen,zaw_id));
        }
    }
}
