package com.cse403.matchonthestreet.backend;

import android.location.Location;
import android.util.Log;

import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;

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
            "INSERT INTO Accounts (uid, name) VALUES(?,?);";

    private  static final String ADD_ACCOUNT_TO_EVENT_SQL =
            "INSERT INTO Attending (uid, eid) VALUES (?, ?);";

    private static final String ADD_EVENT_SQL =
            "INSERT INTO Events "
                + "(eid, title, longitude, latitude, start_time, duration, time_created, description) "
                + "VALUES (?,?,?,?,?,?,?,?);";

    private static final String GET_EVENTS_IN_RADIUS_SQL =
            "SELECT * FROM Events e WHERE e.latitude < ? AND e.latitude > ? "
            + "AND e.longitude < ? AND e.longitude > ?;";

    private static final String GET_EVENT_BY_ID_SQL =
            "SELECT * FROM Events WHERE eid = ?;";

    private static final String GET_EVENTS_ATTENDED_BY_ACCOUNT_SQL
            = "SELECT e.eid AS eid, e.title AS title, e.longitude AS longitude, "
                    + "e.latitude AS latitude, e.start_time AS start_time, "
                    + "e.duration AS duration, e.time_created AS time_created, "
                    + "e.description AS description "
            + "FROM Events e, Attending a"
            + "WHERE a.uid = ? AND a.eid = e.eid";

    private static final String GET_ACCOUNTS_ATTENDING_EVENT_SQL
            = "SELECT a.uid AS uid, a.name AS name "
            + "FROM Accounts a, Attending at "
            + "WHERE at.eid = ? AND at.uid = a.uid;";

    private static final String GET_COUNT_OF_ACCOUNTS_ATTENDING_EVENT_SQL
        = "SELECT COUNT(*) "
        + "FROM Accounts a, Attending at "
        + "WHERE at.eid = ? AND at.uid = a.uid;";

    /* transactions */
    private static final String BEGIN_TRANSACTION_SQL =
            "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION";

    private static final String COMMIT_SQL = "COMMIT TRANSACTION";

    private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";

    private DBManager() {
        // Nothing
    }

   // Methods

    public static void addEvent(Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement addEventStatement = conn.prepareStatement(ADD_EVENT_SQL);
        addEventStatement.clearParameters();
        addEventStatement.setInt(1, event.eid);
        addEventStatement.setString(2, event.title);
        addEventStatement.setDouble(3, event.location.getLongitude());
        addEventStatement.setDouble(4, event.location.getLatitude());
        addEventStatement.setLong(5, event.time.getTime());
        addEventStatement.setInt(6, event.duration);
        addEventStatement.setLong(7, event.timeCreated.getTime());
        addEventStatement.setString(8, event.description);
        addEventStatement.executeUpdate();
        closeConnection(conn);
    }

    public static Event getEventById(int eid) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement getEventByIdStatement = conn.prepareStatement(GET_EVENT_BY_ID_SQL);
        getEventByIdStatement.clearParameters();
        getEventByIdStatement.setInt(1, eid);
        ResultSet rs = getEventByIdStatement.executeQuery();
        closeConnection(conn);
        if (rs.next()) {
            String title = rs.getString("title");
            Location loc = new Location("new location");
            loc.setLongitude(rs.getDouble("longitude"));
            loc.setLatitude(rs.getDouble("latitude"));
            Date time = new java.util.Date(rs.getLong("start_time"));
            int duration = rs.getInt("duration");
            Date timeCreated = new java.util.Date(rs.getLong("time_created"));
            String description = rs.getString("description");
            return new Event(eid, title, loc, time, duration, timeCreated, description);
        }
        return null;
    }

    public static List<Event> getEventsInRadius(Location location, double radius) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement getEventByRadiusStatement = conn.prepareStatement(GET_EVENTS_IN_RADIUS_SQL);
        getEventByRadiusStatement.clearParameters();
        getEventByRadiusStatement.setDouble(1, location.getLatitude() + radius);
        getEventByRadiusStatement.setDouble(2, location.getLatitude() - radius);
        getEventByRadiusStatement.setDouble(3, location.getLongitude() + radius);
        getEventByRadiusStatement.setDouble(4, location.getLongitude() - radius);
        ResultSet rs = getEventByRadiusStatement.executeQuery();
        closeConnection(conn);
        List<Event> list = new ArrayList<>();
        while (rs.next()){
            int eid = rs.getInt("eid");
            String title = rs.getString("title");
            Location loc = new Location("new location");
            loc.setLongitude(rs.getDouble("longitude"));
            loc.setLatitude(rs.getDouble("latitude"));
            Date time = new java.util.Date(rs.getLong("start_time"));
            int duration = rs.getInt("duration");
            Date timeCreated = new java.util.Date(rs.getLong("time_created"));
            String description = rs.getString("description");
            Event event = new Event(eid, title, loc, time, duration, timeCreated, description);
            list.add(event);
        }
        return list;
    }

    public static void addAccountToEvent(Account account, Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement st = conn.prepareStatement(ADD_ACCOUNT_TO_EVENT_SQL);
        st.clearParameters();
        st.setInt(1, account.getUid());
        st.setInt(2, event.eid);
        st.executeUpdate();
        closeConnection(conn);
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

    public static int getCountOfAccountsAttendingEvent(Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement st = conn.prepareStatement(GET_COUNT_OF_ACCOUNTS_ATTENDING_EVENT_SQL);
        int count = 0;
        st.clearParameters();
        st.setInt(1, event.eid);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }

    public static List<Account> getAccountsAttendingEvent(Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement st = conn.prepareStatement(GET_ACCOUNTS_ATTENDING_EVENT_SQL);
        List<Account> list = new ArrayList<>();
        st.clearParameters();
        st.setInt(1, event.eid);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            int uid = rs.getInt("uid");
            String name = rs.getString("name");
            Account account = new Account(uid, name);
            list.add(account);
        }
        return list;
    }

    public static List<Event> getEventsAttendedByAccount(Account account) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement getUserEventStatement = conn.prepareStatement(GET_EVENTS_ATTENDED_BY_ACCOUNT_SQL);
        List<Event> list = new ArrayList<Event>();
        getUserEventStatement.clearParameters();
        getUserEventStatement.setInt(1, account.getUid());
        ResultSet rs = getUserEventStatement.executeQuery();
        while (rs.next()) {
            int eid = rs.getInt("eid");
            String title = rs.getString("title");
            Location loc = new Location("new location");
            loc.setLongitude(rs.getDouble("longitude"));
            loc.setLatitude(rs.getDouble("latitude"));
            Date time = new java.util.Date(rs.getLong("start_time"));
            int duration = rs.getInt("duration");
            Date timeCreated = new java.util.Date(rs.getLong("time_created"));
            String description = rs.getString("description");
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
