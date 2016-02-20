package com.cse403.matchonthestreet;

import android.location.Location;
import android.os.AsyncTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iris on 2/7/16.
 */
public class DBManager {
    // Database Credentials
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://matchonthestreetdb.crqizzvrxges.us-east-1.rds.amazonaws.com:3306/motsdb";
    static final String USER = "larioj";
    static final String PASS = "motspassword";

    private Connection conn;

    // SQL Queries
    private static final String GET_ACCOUNT_BY_UID =
            "select * from Accounts where uid = ?;";
    private static final String GET_EVENT_BY_ID =
            "select * from Events where eid = ?;";
    private static final String ADD_EVENT =
            "INSERT INTO Events (title, longitude, latitude, time, description) VALUES (?,?,?,?,?);";
    private  static final String ADD_ACCOUNT =
            "insert into Accounts (uid, name) values (?, ?);";


    public void addEventWithParams(String title, Double longitude,
                                   Double latitude, Timestamp datetime, String description)
            throws SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement addEventStatement = conn.prepareStatement(ADD_EVENT);
        addEventStatement.clearParameters();
        addEventStatement.setString(1, title);
        addEventStatement.setDouble(2, longitude);
        addEventStatement.setDouble(3, latitude);
        addEventStatement.setTimestamp(4, datetime);
        addEventStatement.setString(5, description);
        addEventStatement.executeUpdate();
        closeConnection();
    }

    public void addAccountWithParams(String uid, String name)
            throws SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement addEventStatement = conn.prepareStatement(ADD_ACCOUNT);
        addEventStatement.clearParameters();
        addEventStatement.setString(1, uid);
        addEventStatement.setString(2, name);
        addEventStatement.executeUpdate();
        closeConnection();
    }

    public Event getEventWithParams(int eid)
            throws SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement addEventStatement = conn.prepareStatement(GET_EVENT_BY_ID);
        addEventStatement.clearParameters();
        addEventStatement.setInt(1, eid);
        ResultSet eventSet = addEventStatement.executeQuery();
        closeConnection();

        String title;
        Double longitude;
        Double latitude;
        Timestamp datetime;
        String description;

        if (eventSet.next()) {
            title = eventSet.getString("title");
            longitude= eventSet.getDouble("logitude");
            latitude = eventSet.getDouble("latitude");
            datetime = eventSet.getTimestamp("time");

        }
        return null;
    }




    private String uid;

    private static final String USER_LOGIN_SQL =
            "SELECT * FROM Accounts WHERE uid = ?";
    private PreparedStatement userLoginStatement;

    private static final String CREATE_ACCOUNT_SQL =
            "INSERT INTO Accounts VALUES(?,?)";
    private PreparedStatement createAccountStatement;

    private static final String ADD_EVENT_SQL =
            "INSERT INTO Events VALUES(?,?,?,?,?)";
    private PreparedStatement addEventStatement;

    private static final String GET_EVENT_SQL =
            "SELECT * FROM Events WHERE ";
    private PreparedStatement getEventStatement;
    // transactions
    private static final String BEGIN_TRANSACTION_SQL =
            "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION";
    private PreparedStatement beginTransactionStatement;

    private static final String COMMIT_SQL = "COMMIT TRANSACTION";
    private PreparedStatement commitTransactionStatement;

    private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";
    private PreparedStatement rollbackTransactionStatement;



    /* Connection code to MySQL.  */
    public void openConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting to database...");
        conn=DriverManager.getConnection(DB_URL,USER,PASS);
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }

    /* prepare all the SQL statements in this method.
      "preparing" a statement is almost like compiling it. */

    public void prepareStatements() throws Exception {
        beginTransactionStatement = conn.prepareStatement(BEGIN_TRANSACTION_SQL);
        commitTransactionStatement = conn.prepareStatement(COMMIT_SQL);
        rollbackTransactionStatement = conn.prepareStatement(ROLLBACK_SQL);
        userLoginStatement = conn.prepareStatement(USER_LOGIN_SQL);
        createAccountStatement = conn.prepareStatement(CREATE_ACCOUNT_SQL);
        addEventStatement = conn.prepareStatement(ADD_EVENT_SQL);
        getEventStatement = conn.prepareStatement(GET_EVENT_SQL);

    }


    public void transaction_login(String uid, String name) throws Exception {
        userLoginStatement.clearParameters();
        userLoginStatement.setString(1,uid);
        ResultSet uid_set = userLoginStatement.executeQuery();
        if (uid_set.next()){
            this.uid = uid_set.getString(1);
            System.out.println("Successfully login.");
        } else {
            beginTransaction();
            try {
                createAccountStatement.clearParameters();
                createAccountStatement.setString(1, uid);
                createAccountStatement.setString(2, name);
                createAccountStatement.execute();
                this.uid = uid;
                System.out.println("Create a new account.");
                commitTransaction();
            } catch (SQLException e) {
                try {
                    rollbackTransaction();
                } catch (Exception rollbackE) {
                    System.out.println("Error when roll back!");
                }
            }
        }
        uid_set.close();
    }

    public void transaction_addEvent(Event event) throws Exception {
        if (this.uid == null) {
            System.out.println("You need to log in.");
            return;
        }
        beginTransaction();
        try {
            addEventStatement.clearParameters();
            addEventStatement.setInt(1, event.eid);
            addEventStatement.setString(2, event.title);
            addEventStatement.setString(3, null);
            addEventStatement.setString(4, null);
            addEventStatement.setString(5, event.description);
            addEventStatement.execute();
            commitTransaction();
        } catch (SQLException e) {
            try {
                rollbackTransaction();
            } catch (Exception rollbackE) {
                System.out.println("Error when roll back!");
            }
        }
    }

    public List<Event> transaction_getEvent(Location location, int radius) throws Exception {
        getEventStatement.clearParameters();
        getEventStatement.setString(1, null);
        ResultSet getEventResults = getEventStatement.executeQuery();
        List<Event> list = new ArrayList<Event>();
        while (getEventResults.next()){
            int eid = getEventResults.getInt("eid");
            String title = getEventResults.getString("title");
            Location loc = null;
            Date time = null;
            int duration = 60;
            Date timeCreated = null;
            String description = getEventResults.getString("description");
            Event event = new Event(eid, title, loc, time, duration, timeCreated, description);
            list.add(event);
        }
        return list;
    }

    public void beginTransaction() throws Exception {
        conn.setAutoCommit(false);
        beginTransactionStatement.executeUpdate();
    }

    public void commitTransaction() throws Exception {
        commitTransactionStatement.executeUpdate();
        conn.setAutoCommit(true);
    }
    public void rollbackTransaction() throws Exception {
        rollbackTransactionStatement.executeUpdate();
        conn.setAutoCommit(true);
    }
}
