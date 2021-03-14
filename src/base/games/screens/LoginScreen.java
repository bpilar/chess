package base.games.screens;


import base.games.AppWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.sql.*;

public class LoginScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public JPanel snapCenterPanel = new JPanel();
    public JTextField loginField = new JTextField();
    public JPasswordField passwordField = new JPasswordField();
    public JButton loginButton = new JButton("Zaloguj");
    public LoginScreen(AppWindow app, BodyScreen previous) {
        parent = app;
        previousScreen = previous;
        displayPanel.setBackground(Color.lightGray);
        snapCenterPanel.setLayout(new GridLayout(3,2));
        displayPanel.add(snapCenterPanel);
        loginButton.addActionListener(this);
        snapCenterPanel.add(new JLabel("Użytkownik"));
        snapCenterPanel.add(loginField);
        snapCenterPanel.add(new JLabel("Hasło"));
        snapCenterPanel.add(passwordField);
        snapCenterPanel.add(loginButton);
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

        if (object == loginButton)
        {
            String inputLogin = loginField.getText();
            String inputPassword = "";
            char[] cPass = passwordField.getPassword();
            for (char pass : cPass) {
                inputPassword += pass;
            }

            loginField.setText("");
            passwordField.setText("");

            try (Statement stmt = parent.conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT Haslo, Typ, uzy_id FROM Uzytkownicy WHERE Nazwa_uzy='" + inputLogin + "'");) {
                if (rs.next()) {
                    if (Objects.equals(inputPassword, rs.getString(1))) {
                        switch (rs.getString(2)) {
                            case "ZAW":
                                try (Statement stmt2 = parent.conn.createStatement();
                                     ResultSet rs2 = stmt2.executeQuery("SELECT zaw_id FROM zawodnicy WHERE uzy_id=" + rs.getString(3));) {
                                    if (rs2.next()) {
                                        parent.switchCurrentScreenTo(new PlayerScreen(parent,this, rs2.getString(1)));
                                    }
                                    else
                                    {
                                        parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"brak przypisanego konta"));
                                    }
                                } catch (SQLException ex) {
                                    System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                                }
                                break;
                            case "TRE":
                                try (Statement stmt2 = parent.conn.createStatement();
                                     ResultSet rs2 = stmt2.executeQuery("SELECT tre_id FROM trenerzy WHERE uzy_id=" + rs.getString(3));) {
                                    if (rs2.next()) {
                                        parent.switchCurrentScreenTo(new CoachScreen(parent,this, rs2.getString(1)));
                                    }
                                    else
                                    {
                                        parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"brak przypisanego konta"));
                                    }
                                } catch (SQLException ex) {
                                    System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                                }
                                break;
                            case "SED":
                                try (Statement stmt2 = parent.conn.createStatement();
                                     ResultSet rs2 = stmt2.executeQuery("SELECT sed_id FROM sedziowie WHERE uzy_id=" + rs.getString(3));) {
                                    if (rs2.next()) {
                                        parent.switchCurrentScreenTo(new RefereeScreen(parent,this, rs2.getString(1)));
                                    }
                                    else
                                    {
                                        parent.switchCurrentScreenTo(new ErrorScreen(parent,this,"brak przypisanego konta"));
                                    }
                                } catch (SQLException ex) {
                                    System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                                }
                                break;
                            case "ADM":
                                parent.switchCurrentScreenTo(new AdminScreen(parent,this));
                                break;
                        }
                    }
                }
            } catch (SQLException ex) {
                System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
            }

//            if (Objects.equals(inputLogin, "tak") && Objects.equals(inputPassword, "tak")) {
//                parent.switchCurrentScreenTo(new StartScreen(parent,this));
//            }
        }
    }
}
