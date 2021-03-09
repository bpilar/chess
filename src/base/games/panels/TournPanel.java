package base.games.panels;

import base.games.AppWindow;
import base.games.screens.BodyScreen;
import base.games.screens.TemplateScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TournPanel extends JPanel implements ActionListener {
    public AppWindow parent;
    public BodyScreen previousScreen;
    public String tur_id;
    public JButton chooseButton = new JButton("SHOW");
    public TournPanel(AppWindow app, BodyScreen previous, String t_id, String t_nazwa, String t_data, String t_miejsce, String s_nazwa, String s_rok) {
        super();
        parent = app;
        previousScreen = previous;
        tur_id = t_id;
        setLayout(new GridLayout(1,6));
        setMaximumSize(new Dimension(AppWindow.width,30));
        add(new JLabel(t_nazwa));
        add(new JLabel(t_data));
        add(new JLabel(t_miejsce));
        add(new JLabel(s_nazwa));
        add(new JLabel(s_rok));
        chooseButton.addActionListener(this);
        add(chooseButton);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if (object == chooseButton)
        {
            parent.switchCurrentScreenTo(new TemplateScreen(parent,previousScreen));
            //TODO proper screen
        }
    }
}
