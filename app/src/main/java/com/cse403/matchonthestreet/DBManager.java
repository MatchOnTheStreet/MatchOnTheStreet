package com.cse403.matchonthestreet;

import android.location.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iris on 2/7/16.
 */
public class DBManager {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://c-24-16-210-83.hsd1.wa.comcast.net/ruijiw_MatchOnTheStreet";

    //  Database credentials
    static final String USER = "ruijiw@c-24-16-210-83.hsd1.wa.comcast.net";
    static final String PASS = "bZQf23xm";

    private String uid;

    private static final String USER_LOGIN_SQL =
            "SELECT * FROM Accounts WHERE uid = ?";
    private PreparedStatement userLoginStatement;

    private static final String CREATE_ACCOUNT_SQL =
            "INSERT INTO Accounts VALUES(?,?)";
    private PreparedStatement createAccountStatement;

    private static final String ADD_EVENT_SQL =
            "INSERT INTO Event VALUES(?,?,?,?,?)";
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

    private Connection conn;

    /* Connection code to MySQL.  */
    public void openConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void closeConnection() throws Exception {
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
            Time time = null;
            String description = getEventResults.getString("description");
            Event event = new Event(eid, title, loc, time, description);
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
