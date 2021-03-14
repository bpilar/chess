package base.games.screens;

import base.games.AppWindow;
import base.games.admin.screens.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public JPanel snapCenterPanel = new JPanel();
    public JButton seasonButton = new JButton("Sezony");
    public JButton tournButton = new JButton("Turnieje");
    public JButton clubButton = new JButton("Kluby");
    public JButton placeButton = new JButton("Miejsca");
    public JButton playersButton = new JButton("Zawodnicy");
    public JButton coachButton = new JButton("Trenerzy");
    public JButton refereeButton = new JButton("Sędziowie");
    public JButton userButton = new JButton("Użytkownicy");
    public AdminScreen(AppWindow app, BodyScreen previous) {
        parent = app;
        previousScreen = previous;
        displayPanel.setLayout(new BorderLayout());
        returnButton.addActionListener(this);
        displayPanel.add(BorderLayout.SOUTH,returnButton);
        displayPanel.add(BorderLayout.CENTER,centerPanel);
        snapCenterPanel.setLayout(new BoxLayout(snapCenterPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(snapCenterPanel);
        seasonButton.addActionListener(this);
        tournButton.addActionListener(this);
        clubButton.addActionListener(this);
        placeButton.addActionListener(this);
        playersButton.addActionListener(this);
        coachButton.addActionListener(this);
        refereeButton.addActionListener(this);
        userButton.addActionListener(this);
        snapCenterPanel.add(new JLabel("wybierz funkcjonalność"));
        snapCenterPanel.add(seasonButton);
        snapCenterPanel.add(tournButton);
        snapCenterPanel.add(clubButton);
        snapCenterPanel.add(placeButton);
        snapCenterPanel.add(playersButton);
        snapCenterPanel.add(coachButton);
        snapCenterPanel.add(refereeButton);
        snapCenterPanel.add(userButton);

    }

    @Override
    public JPanel getDisplayPanel() {
        return displayPanel;
    }

    @Override
    public BodyScreen getPreviousScreen() {
        return previousScreen;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if (object == returnButton)
        {
            parent.switchCurrentScreenTo(previousScreen);
        }
        if (object == seasonButton)
        {
            parent.switchCurrentScreenTo(new AdmSeasonScreen(parent,this));
        }
        if (object == tournButton)
        {
            parent.switchCurrentScreenTo(new AdmTournScreen(parent,this));
        }
        if (object == clubButton)
        {
            parent.switchCurrentScreenTo(new AdmClubScreen(parent,this));
        }
        if (object == placeButton)
        {
            parent.switchCurrentScreenTo(new AdmPlaceScreen(parent,this));
        }
        if (object == playersButton)
        {
            parent.switchCurrentScreenTo(new AdmPlayerScreen(parent,this));
        }
        if (object == coachButton)
        {
            parent.switchCurrentScreenTo(new AdmCoachScreen(parent,this));
        }
        if (object == refereeButton)
        {
            parent.switchCurrentScreenTo(new AdmRefereeScreen(parent,this));
        }
        if (object == userButton)
        {
            parent.switchCurrentScreenTo(new AdmUserScreen(parent,this));
        }
    }
}
