package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DataBaseConnection {
    public Connection c;
    private Statement stmt;
    public void makeConnection(String db) throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("src/main/resources/db.properties")));
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(url+"/"+db, user, password);
            c.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        stmt = c.createStatement();
    }

    public ResultSet makeQuery(String sql) throws SQLException {
        return stmt.executeQuery(sql);
    }
    public void makeUpdate(String sql) throws SQLException {
         stmt.executeUpdate(sql);
    }

    public void makeInsert(String sql) throws SQLException {
        stmt.execute(sql);
    }
}
