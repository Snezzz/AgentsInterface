
import compromise.Agent;
import database.DataBaseConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class GUI extends JFrame{
    private JPanel panel1;
    private JButton loginButton;
    private JButton registrationButton;
    private JTextField newLoginField;
    private JPasswordField newPasswordField;
    private JLabel newLoginLabel;
    private JLabel newPasswordLabel;
    private JPanel mainPanel;
    private JLabel successLabel;
    private JPanel registrationPanel;
    private JPanel loginPanel;
    private JPasswordField passwordRepeatField;
    private JTextField lodinField;
    private JPasswordField passwordField;
    private JLabel passwordRepeatLabel;
    private JTextField firstNameField;
    private JTextField secondNameField;
    private JLabel firstNameLabel;
    private JLabel secondNameLabel;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JLabel errorLabel;
    private JLabel errorLabel1;


    GUI(){
        setTitle("Hidden Community");
        loginPanel.setVisible(true);
        registrationPanel.setVisible(false);
        errorLabel1.setVisible(false);
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(panel1);


        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (registrationPanel.isVisible()){
                    setSize(250,200);
                    registrationPanel.setVisible(false);
                    loginPanel.setVisible(true);
                    return;
                }

                String login = lodinField.getText();
                char [] password = passwordField.getPassword();

                Agent agent = login(login, password);
                if(agent != null) {
                    errorLabel.setVisible(false);
                    MainPage mainPage = null;
                    mainPage = new MainPage(agent);
                    mainPage.setSize(700, 650);
                    mainPage.setLocation(400, 10);
                    mainPage.setVisible(true);
                    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    setVisible(false);
                }
                else{
                    errorLabel.setVisible(true);
                }

            }
        });

        newLoginField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                errorLabel.setVisible(false);
            }
        });

        newPasswordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                errorLabel.setVisible(false);
            }
        });

        registrationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (registrationPanel.isVisible()){
                    successLabel.setVisible(false);
                    String login = newLoginField.getText();
                    char [] password =  newPasswordField.getPassword();
                    char [] passwordRepeat = passwordRepeatField.getPassword();
                    String firstName = firstNameField.getText();
                    String secondName = secondNameField.getText();

                    Random rand = new Random();
                    Agent currentAgent = new Agent(rand.nextInt(),login,firstName,secondName);
                    if (login.length() > 0 && password.length > 0 && passwordRepeat.length > 0) {
                        errorLabel1.setVisible(false);

                        if(checkLogin(login)) {
                            if (registration(currentAgent, password, passwordRepeat)) {
                                successLabel.setVisible(true);
                            } else {
                                errorLabel1.setVisible(true);
                            }
                        }
                        else{
                            errorLabel1.setVisible(true);
                            errorLabel1.setText("This login exists!");
                        }

                    }
                    else  errorLabel1.setVisible(true);
                }
                else {
                    setSize(300, 350);
                    loginPanel.setVisible(false);
                    registrationPanel.setVisible(true);
                }

            }
        });


    }

    private Agent login(String login, char[] password){
        DataBaseConnection dbConnection = new DataBaseConnection();
        Agent currentAgent = null;
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        try {
            dbConnection.makeConnection("postgres");
            String query = "SELECT * FROM rivalry.agent as agent" +
                    " where agent.login = '" + login + "'" ;
            ResultSet rs = dbConnection.makeQuery(query);
            if (rs.next()) {
                String code = rs.getString(5);
                if(bCryptPasswordEncoder.matches(String.valueOf(password),code)) {
                    currentAgent = new Agent(rs.getInt(1),
                            rs.getString(4),
                            rs.getString(2),
                            rs.getString(3));
                    dbConnection.c.close();
                    return currentAgent;
                }
                else {
                    System.out.println("Проверьте пароль");
                    return null;
                }

            }
            else {
                System.out.println("Проверьте логин");
                return null;
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkLogin(String login){
        DataBaseConnection dbConnection = new DataBaseConnection();
        try {
            dbConnection.makeConnection("postgres");
            String query = "SELECT count(*) from postgres.rivalry.agent WHERE login = '" + login + "'";
            ResultSet rs = dbConnection.makeQuery(query);
            if(rs.next()) {
                int count = rs.getInt(1);
                if(count > 0)
                    return false;
            }
            dbConnection.c.close();
            return true;
        } catch (SQLException | IOException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    private boolean registration(Agent agent, char [] password, char [] passwordRepeat) {

        if (String.valueOf(password).equals(String.valueOf(passwordRepeat))) {
            DataBaseConnection dbConnection = new DataBaseConnection();
            int id = agent.getId();
            String login = agent.getLogin();
            String firstName = agent.getFirstName();
            String secondName = agent.getSecondName();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String bCryptedPassword = bCryptPasswordEncoder.encode(String.valueOf(password));
             try {
                dbConnection.makeConnection("postgres");
                String query = "INSERT INTO postgres.rivalry.agent VALUES (" + id + ",'" + firstName + "','" + secondName + "','" +
                        login + "','" + bCryptedPassword + "')";
                dbConnection.makeUpdate(query);
                dbConnection.c.close();
                return true;
            } catch (SQLException | IOException e1) {
                e1.printStackTrace();
            }
        } else return false;
        return false;
    }
}
