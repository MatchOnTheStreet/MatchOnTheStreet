package com.cse403.matchonthestreet;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by Iris on 2/7/16.
 */
public final class DBManager {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://matchonthestreetdb.crqizzvrxges.us-east-1.rds.amazonaws.com:3306/motsdb";

    //  Database credentials
    static final String USER = "larioj";
    static final String PASS = "motspassword";

    // Queries
    private static final String USER_LOGIN_SQL =
            "SELECT * FROM Accounts, WHERE uid = ?";

    private static final String CREATE_ACCOUNT_SQL =
            "INSERT INTO Accounts (uid, name) VALUES(?,?)";

    private static final String ADD_EVENT_SQL =
            "INSERT INTO Events (eid, title, longitude, latitude, time, duration, timecreated, description) VALUES(?,?,?,?,?,?,?,?)";

    private static final String GET_EVENT_SQL_BY_RADIUS =
            "SELECT * FROM Events e WHERE e.latitude < ? AND e.latitude > ? "
            + "AND e.logitude < ? AND e.longitude > ?;";

    private static final String GET_USER_EVENT_SQL =
            "SELECT e.eid, e.title, e.longitude, e.latitude, e.time, e.duration, e.timecreated, e.description "
            + "FROM Events e, Attending a"
            + "WHERE a.uid = ? AND a.eid = e.eid";;

    /* transactions */
    private static final String BEGIN_TRANSACTION_SQL =
            "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION";

    private static final String COMMIT_SQL = "COMMIT TRANSACTION";

    private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";

    private DBManager() {
        // Nothing
    }

    public static List<Event> getUserEvent(Account account) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement getUserEventStatement = conn.prepareStatement(GET_USER_EVENT_SQL);
        List<Event> list = new ArrayList<Event>();
        getUserEventStatement.clearParameters();
        getUserEventStatement.setInt(1, account.getUid());
        ResultSet getUserEventResults = getUserEventStatement.executeQuery();
        while (getUserEventResults.next()) {
            int eid = getUserEventResults.getInt(1);
            String title = getUserEventResults.getString(2);
            Location loc = new Location("new location");
            loc.setLongitude(getUserEventResults.getDouble(3));
            loc.setLatitude(getUserEventResults.getDouble(4));
            Date time = getUserEventResults.getTimestamp(5);
            int duration = getUserEventResults.getInt(6);
            Date timeCreated = getUserEventResults.getTimestamp(7);
            String description = getUserEventResults.getString(8);
            Event event = new Event(eid, title, loc, time, duration, timeCreated, description);
            list.add(event);
        }
        return list;
    }

    public static void addAccount(Account account) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement createAccountStatement = conn.prepareStatement(CREATE_ACCOUNT_SQL);
        createAccountStatement.clearParameters();
        createAccountStatement.setInt(1, account.getUid());
        createAccountStatement.setString(2, account.getName());
        createAccountStatement.executeUpdate();
        closeConnection(conn);
    }

    public static void addEvent(Event event) throws SQLException, ClassNotFoundException {
        Log.d("DBManager", "addEvent");
        Connection conn = openConnection();
        PreparedStatement addEventStatement = conn.prepareStatement(ADD_EVENT_SQL);
        addEventStatement.clearParameters();
        addEventStatement.setInt(1, event.eid);
        addEventStatement.setString(2, event.title);
        addEventStatement.setDouble(3, event.location.getLongitude());
        addEventStatement.setDouble(4, event.location.getLatitude());
        addEventStatement.setString(5, event.time.toString());
        addEventStatement.setInt(6, event.duration);
        addEventStatement.setString(7, event.timeCreated.toString());
        addEventStatement.setString(8, event.description);
        addEventStatement.executeUpdate();
        closeConnection(conn);
    }

    public static List<Event> getEventByRadius(Location location, int radius) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement getEventByRadiusStatement = conn.prepareStatement(GET_EVENT_SQL_BY_RADIUS);
        getEventByRadiusStatement.clearParameters();
        getEventByRadiusStatement.setDouble(1, location.getLatitude() + radius);
        getEventByRadiusStatement.setDouble(1, location.getLatitude() - radius);
        getEventByRadiusStatement.setDouble(1, location.getLongitude() + radius);
        getEventByRadiusStatement.setDouble(1, location.getLongitude() + radius);
        ResultSet getEventResults = getEventByRadiusStatement.executeQuery();
        closeConnection(conn);
        List<Event> list = new ArrayList<Event>();
        while (getEventResults.next()){
            int eid = getEventResults.getInt("eid");
            String title = getEventResults.getString("title");
            Location loc = new Location("new location");
            loc.setLongitude(getEventResults.getDouble("longitude"));
            loc.setLatitude(getEventResults.getDouble("latitude"));
            Date time = getEventResults.getTimestamp("time");
            int duration = getEventResults.getInt("duration");
            Date timeCreated = getEventResults.getTimestamp("timecreated");;
            String description = getEventResults.getString("description");
            Event event = new Event(eid, title, loc, time, duration, timeCreated, description);
            list.add(event);
        }
        return list;
    }

    /* Connection code to MySQL.  */
    public static Connection openConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting to database...");
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    private void beginTransaction(Connection conn) throws Exception {
        conn.setAutoCommit(false);
        PreparedStatement beginTransactionStatement = conn.prepareStatement(BEGIN_TRANSACTION_SQL);
        beginTransactionStatement.executeUpdate();
    }

    private void commitTransaction(Connection conn) throws Exception {
        PreparedStatement commitTransactionStatement = conn.prepareStatement(COMMIT_SQL);
        commitTransactionStatement.executeUpdate();
        conn.setAutoCommit(true);
    }

    private void rollbackTransaction(Connection conn) throws Exception {
        PreparedStatement rollbackTransactionStatement = conn.prepareStatement(ROLLBACK_SQL);
        rollbackTransactionStatement.executeUpdate();
        conn.setAutoCommit(true);
    }
}
