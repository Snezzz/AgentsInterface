
import bronkerbosh.Clique;
import centrality.NewmanAndGirvan;
import clusters.Clusters;
import compromise.Agent;
import compromise.Algorithm;
import compromise.Compromise;
import compromise.SolutionFrame;
import core.Cores;

import database.DataBaseConnection;
import db.DBConnection;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MainPage extends JFrame {
    private JPanel mainPanel;
    private JButton saveDataButton;
    private JButton getSolutionButton;
    private JComboBox ratingBox;
    private JRadioButton coresRadioButton;
    private JRadioButton cliquesRadioButton;
    private JRadioButton clustersFirstRadioButton;
    private JTextField clustersFirstTextField;
    private JTextArea coresTextArea;

    private JPanel panel1;
    private JPanel menuPanel;
    private JPanel clustersPanel;
    private JPanel coresPanel;
    private JPanel cliquesPanel;
    private JPanel ratingPanel;
    private JButton loadDataButton;
    private JLabel helloLabel;
    private JTextField cliqueTextField;
    private JRadioButton clustersSecondRadioButton;
    private JTextField clustersSecondTextField;
    private JTextPane firstClustersPane;
    private JTextPane cliquesPane;
    private JTextPane secondClustersPane;
    private JTextPane coresPane;
    private JTextField coresTextField;
    private JLabel infoLabel;
    private JLabel helpLabel;
    private JScrollPane scrollPane;
    private Agent agent;
    private Algorithm algorithm;

    private JFrame graphFrame;
    private JFrame infoFrame;
    private JFrame buttonFrame;


    public MainPage(final Agent agent) {


        this.agent = agent;

        helloLabel.setText("Hello,"+this.agent.getFirstName()+" "+ this.agent.getSecondName());
        infoLabel.setText("Choose one of algorithms to find communities, watch results and give your mark!");
        helpLabel.setText("You have to save your mark before optimal solution will be found");
        clustersFirstRadioButton.setSelected(true);
        clustersPanel.setBackground(null);
        clustersPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        coresPanel.setBackground(null);
        cliquesPanel.setBackground(null);

        cliquesRadioButton.setBackground(null);
        coresRadioButton.setBackground(null);
        clustersFirstRadioButton.setBackground(null);
        clustersSecondRadioButton.setBackground(null);
        //cliquesPane.setSize(200,200);
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        setResizable(false);
        mainPanel.setAutoscrolls(true);

        panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scrollPane.setBackground(null);
        firstClustersPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        secondClustersPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        coresPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cliquesPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        coresPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(mainPanel);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getAlgorithmData(3);

// кнопка по загрузке графа
        loadDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(cliquesRadioButton.isSelected()){
                     getAlgorithmData(2);
                    int k = Integer.valueOf(cliqueTextField.getText());

                    Clique clique = new Clique(k);
                    try {
                        clique.run(k);
                    } catch (FileNotFoundException | URISyntaxException e1) {
                        e1.printStackTrace();
                    }

                    graphFrame = clique.graphFrame;
                    infoFrame = clique.infoFrame;
                    buttonFrame = clique.buttonFrame;
                }
                else if(coresRadioButton.isSelected()){
                    getAlgorithmData(1);
                    int k = Integer.valueOf(coresTextField.getText());

                    Cores cores = new Cores();
                    try {
                        cores.run(k);
                    } catch (InterruptedException | IOException e1) {
                        e1.printStackTrace();
                    }

                    graphFrame = cores.graphFrame;
                    infoFrame = cores.infoFrame;
                    buttonFrame = cores.buttonFrame;
             }

                else if (clustersFirstRadioButton.isSelected()){
                    getAlgorithmData(3);
                    int k = Integer.valueOf(clustersFirstTextField.getText());

                    Clusters clusters = new Clusters();
                    try {
                        clusters.run(k);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    graphFrame = clusters.graphFrame;
                    infoFrame = clusters.infoFrame;
                    buttonFrame = clusters.buttonFrame;
                }
                else if (clustersSecondRadioButton.isSelected()){
                    getAlgorithmData(4);
                    int k = Integer.valueOf(clustersSecondTextField.getText());

                    NewmanAndGirvan newmanAndGirvanClusters = new NewmanAndGirvan();
                    try {
                        newmanAndGirvanClusters.run(k);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    graphFrame = newmanAndGirvanClusters.graphFrame;
                    infoFrame = newmanAndGirvanClusters.infoFrame;
                    buttonFrame = newmanAndGirvanClusters.buttonFrame;

                }
                if (graphFrame != null)
                    graphFrame.setVisible(true);
                if (infoFrame != null)
                    infoFrame.setVisible(true);
                buttonFrame.setVisible(true);
                getContentPane().validate();
                getContentPane().repaint(2000);
             }
        });


        getSolutionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                DBConnection dbConnection = new DBConnection();
                Vector<Agent> agents = new Vector<Agent>();
                Vector<Algorithm> algorithms = new Vector<Algorithm>();
                try {
                    dbConnection.makeConnection("postgres");
                    String query = "SELECT * FROM rivalry.agent";
                    ResultSet rs = dbConnection.makeQuery(query);
                    while (rs.next()) {
                        Agent agent = new Agent(rs.getInt(1), rs.getString(4),
                                rs.getString(2), rs.getString(3));
                        agents.add(agent);
                    }

                    query = "SELECT * FROM rivalry.algorithm";
                     rs = dbConnection.makeQuery(query);
                    while (rs.next()) {
                       Algorithm algorithm = new Algorithm(rs.getInt(1), rs.getString(2));
                       algorithms.add(algorithm);
                    }

                    for (Agent agent: agents) {
                        query = "SELECT algorithm_id, rating FROM rivalry.rating as rating " +
                                "WHERE rating.agent_id = " + agent.getId();
                        rs = dbConnection.makeQuery(query);
                        Map<Integer, Integer> ratingList = new HashMap<Integer, Integer>();
                        while(rs.next()){
                            ratingList.put(rs.getInt(1), rs.getInt(2));
                        }

                        //если не все данные
                        for(Algorithm algorithm: algorithms) {
                            int id = algorithm.getId();
                            if (!ratingList.containsKey(id)){
                                ratingList.put(id, 1);
                            }
                        }
                        agent.setRatingList(ratingList);

                    }

                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                Compromise compromise = new Compromise(agents.size(), algorithms.size());
                compromise.fillH(agents, algorithms);
                compromise.getSolutions();

                new SolutionFrame(agents);
            }
        });
        saveDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DBConnection dbConnection = new DBConnection();
                int rating = Integer.valueOf((String) ratingBox.getSelectedItem());
                try {
                    dbConnection.makeConnection("postgres");
                    dbConnection.clearData();
                    String query = "SELECT algorithm_id, agent_id from rivalry.rating WHERE agent_id = "+ agent.getId()+
                            " and algorithm_id = " + algorithm.getId();
                    ResultSet rs = dbConnection.makeQuery(query);

                    if(rs.next()){
                        updateRating(rating);
                    }
                    else{
                        insertRating(rating);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
        clustersFirstRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getAlgorithmData(3);

            }
        });
        cliquesRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getAlgorithmData(2);

            }
        });
        coresRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getAlgorithmData(1);

            }
        });
        clustersSecondRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getAlgorithmData(4);

            }
        });
    }
    private void getAlgorithmData(int id){
        DBConnection dbConnection = new DBConnection();

        try {
            dbConnection.makeConnection("postgres");
            String query = "SELECT * FROM rivalry.algorithm WHERE id ="+ id;
            ResultSet rs = dbConnection.makeQuery(query);
            if(rs.next()){
                algorithm = new Algorithm(rs.getInt(1), rs.getString(2));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void updateRating(int rating){
        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            dbConnection.makeConnection("postgres");
            String query = "UPDATE rivalry.rating SET rating ="+ rating + " WHERE algorithm_id = " + algorithm.getId()+
                    " AND agent_id = " + agent.getId();
            dbConnection.makeUpdate(query);
        } catch (SQLException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private void insertRating(int rating){
        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            dbConnection.makeConnection("postgres");
            String query = "INSERT INTO rivalry.rating VALUES (" + algorithm.getId()+","+ agent.getId()+","+ rating+")";

            dbConnection.makeInsert(query);

        } catch (SQLException | IOException e1) {
            e1.printStackTrace();
        }
    }


}

