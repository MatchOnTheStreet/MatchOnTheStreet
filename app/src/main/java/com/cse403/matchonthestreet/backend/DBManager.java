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
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://matchonthestreetdb.crqizzvrxges.us-east-1.rds.amazonaws.com:3306/motsdb";

    //  Database credentials
    private static final String USER = "larioj";
    private static final String PASS = "motspassword";

    // Queries
    private static final String CREATE_ACCOUNT_SQL =
            "INSERT INTO Accounts (uid, name) VALUES(?,?);";

    private static final String ADD_ACCOUNT_TO_EVENT_SQL =
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

    private static final String GET_ACCOUNT_BY_ID_SQL =
            "SELECT * FROM Accounts WHERE uid = ?;";

    private static final String GET_EVENTS_ATTENDED_BY_ACCOUNT_SQL
            = "SELECT e.eid AS eid, e.title AS title, e.longitude AS longitude, "
            + "e.latitude AS latitude, e.start_time AS start_time, "
            + "e.duration AS duration, e.time_created AS time_created, "
            + "e.description AS description "
            + "FROM Events e, Attending a "
            + "WHERE a.uid = ? AND a.eid = e.eid";

    private static final String GET_ACCOUNTS_ATTENDING_EVENT_SQL
            = "SELECT a.uid AS uid, a.name AS name "
            + "FROM Accounts a, Attending at "
            + "WHERE at.eid = ? AND at.uid = a.uid;";

    private static final String GET_COUNT_OF_ACCOUNTS_ATTENDING_EVENT_SQL
            = "SELECT COUNT(*) "
            + "FROM Accounts a, Attending at "
            + "WHERE at.eid = ? AND at.uid = a.uid;";

    private static final String REMOVE_EVENT_SQL
            = "DELETE FROM Events WHERE eid=?;";

    private static final String REMOVE_ACCOUNT_SQL
            = "DELETE FROM Accounts WHERE uid=?;";

    private static final String REMOVE_ATTENDANCE_SQL
            = "DELETE FROM Attending WHERE uid=? AND eid=?";

    // For testing.
    private static final String CHECK_ATTENDANCE_SQL
            = "SELECT * From Attending WHERE uid=? AND eid=?";

    // May Implement Later.
    private static final String REMOVE_EVENT_ATTENDANCE_SQL
            = "DELETE FROM Attending WHERE eid=?";

    private static final String REMOVE_ACCOUNT_ATTENDANCE_SQL
            = "DELETE FROM Attending WHERE uid=?";

    /* transactions */
    private static final String BEGIN_TRANSACTION_SQL =
            "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION";

    private static final String COMMIT_SQL = "COMMIT TRANSACTION";

    private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";

    private DBManager() {
        // Nothing
    }


    /*
     * Adds event to the database, it does not add the attending relationships
     * in events.attending to the database.
     *
     * @requires event must not already be in the database.
     *
     * @param event event to be added to the database.
     *
     */
    public static void addEvent(Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        addEvent(conn, event);
        closeConnection(conn);
    }


    /*
     * Adds event to the database, it also add the attending relationships
     * in events.attending to the database.
     *
     * @requires event must not already be in the database.
     *
     * @param event event to be added to the database.
     *
     */
    public static void addEventWithAttendance(Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        addEvent(conn, event);
        for (Account account: event.attending) {
            addAccountToEvent(conn, account, event);
        }
        closeConnection(conn);
    }

    private static void addEvent(Connection conn, Event event) throws SQLException, ClassNotFoundException {
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
    }

    /*
     * Returns an Event object, whose eid is parameter eid. It does not populate the
     * attending field of event with the attending relationships in database.
     * Returns null in the case the event does not exist in the database.
     *
     * @param eid the eid of the event to be retrieved.
     *
     */
    public static Event getEventById(int eid) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        Event event = getEventById(conn, eid);
        closeConnection(conn);
        return event;
    }

    /*
     * Returns an Event object, whose eid is parameter eid. It also populates the
     * attending field of event with the attending relationships in database.
     * Returns null in the case the event does not exist in the database.
     *
     * @param eid the eid of the event to be retrieved.
     *
     */
    public static Event getEventByIdWithAttendance(int eid) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        Event event = getEventById(conn, eid);
        if (event == null) {
            return null;
        }
        event.attending = getAccountsAttendingEvent(conn, event);
        closeConnection(conn);
        return event;
    }


    private static Event getEventById(Connection conn, int eid) throws SQLException, ClassNotFoundException {
        PreparedStatement getEventByIdStatement = conn.prepareStatement(GET_EVENT_BY_ID_SQL);
        getEventByIdStatement.clearParameters();
        getEventByIdStatement.setInt(1, eid);
        ResultSet rs = getEventByIdStatement.executeQuery();
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

    /*
     * Removes Event event from the database. Does not remove the attending relations in
     * event.
     *
     * @param event event to be removed.
     *
     */
    public static void removeEvent(Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        removeEvent(conn, event);
        closeConnection(conn);
    }

    private static void removeEvent(Connection conn, Event event) throws SQLException, ClassNotFoundException {
        PreparedStatement getEventByIdStatement = conn.prepareStatement(REMOVE_EVENT_SQL);
        getEventByIdStatement.clearParameters();
        getEventByIdStatement.setInt(1, event.eid);
        getEventByIdStatement.executeUpdate();
    }


    /*
     * removes Account account from database. Does not remove attending relations in account.
     *
     * @param account account to be removed.
     *
     */
    public static void removeAccount(Account account) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        removeAccount(conn, account);
        closeConnection(conn);
    }

    private static void removeAccount(Connection conn, Account account) throws SQLException, ClassNotFoundException {
        PreparedStatement getEventByIdStatement = conn.prepareStatement(REMOVE_ACCOUNT_SQL);
        getEventByIdStatement.clearParameters();
        getEventByIdStatement.setInt(1, account.getUid());
        getEventByIdStatement.executeUpdate();
    }

    /*
     * Removes attendance relationship between Account account and Event event.
     *
     */
    public static void removeAttendance(Account account, Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        removeAttendance(conn, account, event);
        closeConnection(conn);
    }

    private static void removeAttendance(Connection conn, Account account, Event event) throws SQLException, ClassNotFoundException {
        PreparedStatement getEventByIdStatement = conn.prepareStatement(REMOVE_ATTENDANCE_SQL);
        getEventByIdStatement.clearParameters();
        getEventByIdStatement.setInt(1, account.getUid());
        getEventByIdStatement.setInt(2, event.eid);
        getEventByIdStatement.executeUpdate();
    }

    /*
     * Removes all attendance relationships in which Event event is contained.
     *
     */
    public static void removeEventAttendance(Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        removeEventAttendance(conn, event);
        closeConnection(conn);
    }

    private static void removeEventAttendance(Connection conn, Event event) throws SQLException, ClassNotFoundException {
        PreparedStatement getEventByIdStatement = conn.prepareStatement(REMOVE_EVENT_ATTENDANCE_SQL);
        getEventByIdStatement.clearParameters();
        getEventByIdStatement.setInt(1, event.eid);
        getEventByIdStatement.executeUpdate();
    }

    /*
     * Removes all attendance relationships in which Account account is contained.
     *
     */
    public static void removeAccountAttendance(Account account) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        removeAccountAttendance(conn, account);
        closeConnection(conn);
    }

    private static void removeAccountAttendance(Connection conn, Account account) throws SQLException, ClassNotFoundException {
        PreparedStatement getEventByIdStatement = conn.prepareStatement(REMOVE_ACCOUNT_ATTENDANCE_SQL);
        getEventByIdStatement.clearParameters();
        getEventByIdStatement.setInt(1, account.getUid());
        getEventByIdStatement.executeUpdate();
    }

    /*
     * Returns the Account object account, whose uid is parameter uid.
     * Returns null in the case the account does not exist in the database.
     * Does it not return the list of events the account is attending.
     *
     * @param uid the uid of the account to be retrieved.
     *
     */
    public static Account getAccountById(int uid) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        Account account = getAccountById(conn, uid);
        closeConnection(conn);
        return account;
    }

    private static Account getAccountById(Connection conn, int uid) throws SQLException, ClassNotFoundException {
        PreparedStatement getAccountByIdStatement = conn.prepareStatement(GET_ACCOUNT_BY_ID_SQL);
        getAccountByIdStatement.clearParameters();
        getAccountByIdStatement.setInt(1, uid);
        ResultSet rs = getAccountByIdStatement.executeQuery();
        if (rs.next()) {
            String name = rs.getString("name");
            return new Account(uid, name);
        }
        return null;
    }

    /*
     * Gets a list of events within the square whose center lies at location, and whose height is radius.
     * Does not return events with the attending field populated.
     *
     */
    public static List<Event> getEventsInRadius(Location location, double radius) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        List<Event> list = getEventsInRadius(conn, location, radius);
        closeConnection(conn);
        return list;
    }

    /*
    * Gets a list of events within the square whose center lies at location, and whose height is radius.
    * Does not return events with the attending field populated.
    *
    */
    public static List<Event> getEventsInRadiusWithAttendance(Location location, double radius) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        List<Event> list = getEventsInRadius(conn, location, radius);
        for (Event e : list) {
            e.attending = getAccountsAttendingEvent(conn, e);
        }
        closeConnection(conn);
        return list;
    }

    private static List<Event> getEventsInRadius(Connection conn, Location location, double radius) throws SQLException, ClassNotFoundException {
        PreparedStatement getEventByRadiusStatement = conn.prepareStatement(GET_EVENTS_IN_RADIUS_SQL);
        getEventByRadiusStatement.clearParameters();
        getEventByRadiusStatement.setDouble(1, location.getLatitude() + radius);
        getEventByRadiusStatement.setDouble(2, location.getLatitude() - radius);
        getEventByRadiusStatement.setDouble(3, location.getLongitude() + radius);
        getEventByRadiusStatement.setDouble(4, location.getLongitude() - radius);
        ResultSet rs = getEventByRadiusStatement.executeQuery();
        List<Event> list = new ArrayList<>();
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

    /*
     * Adds an attending relationship between account and event.
     *
     */
    public static void addAccountToEvent(Account account, Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        addAccountToEvent(conn, account, event);
        closeConnection(conn);
    }

    private static void addAccountToEvent(Connection conn, Account account, Event event) throws SQLException, ClassNotFoundException {
        PreparedStatement st = conn.prepareStatement(ADD_ACCOUNT_TO_EVENT_SQL);
        st.clearParameters();
        st.setInt(1, account.getUid());
        st.setInt(2, event.eid);
        st.executeUpdate();
    }

    /*
     * adds account to the database. Does not add any attending relationships to the database.
     *
     * @requires account must not be already in the database.
     *
     */
    public static void addAccount(Account account) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        addAccount(conn, account);
        closeConnection(conn);
    }

    private static void addAccount(Connection conn, Account account) throws SQLException, ClassNotFoundException {
        PreparedStatement createAccountStatement = conn.prepareStatement(CREATE_ACCOUNT_SQL);
        createAccountStatement.clearParameters();
        createAccountStatement.setInt(1, account.getUid());
        createAccountStatement.setString(2, account.getName());
        createAccountStatement.executeUpdate();
    }

    /*
     * Gets the list of events attended by account.
     *
     * @requires account be in the database.
     *
     */
    public static List<Event> getEventsAttendedByAccount(Account account) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        List<Event> list = getEventsAttendedByAccount(conn, account);
        closeConnection(conn);
        return list;
    }

    private static List<Event> getEventsAttendedByAccount(Connection conn, Account account) throws SQLException, ClassNotFoundException {
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

    /*
     * gets the count of accounts attending event.
     *
     */
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

    /*
     * gets a list of accounts attending event.
     *
     */
    public static List<Account> getAccountsAttendingEvent(Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        List<Account> list = getAccountsAttendingEvent(conn, event);
        closeConnection(conn);
        return list;
    }

    private static List<Account> getAccountsAttendingEvent(Connection conn, Event event) throws SQLException, ClassNotFoundException {
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



    /*
     * returns true if there is an attendance relationship between event and account in the database.
     * returns false otherwise.
     *
     */
    public static boolean checkAttendance(Account account, Event event) throws SQLException, ClassNotFoundException {
        Connection conn = openConnection();
        PreparedStatement statement = conn.prepareStatement(CHECK_ATTENDANCE_SQL);
        statement.clearParameters();
        statement.setInt(1, account.getUid());
        statement.setInt(2, event.eid);
        ResultSet rs = statement.executeQuery();
        closeConnection(conn);
        if (rs.next()) {
            int uid = rs.getInt("uid");
            int eid = rs.getInt("eid");
            if (uid == account.getUid() && eid == event.eid) {
                return true;
            }
        }
        return false;
    }

    /* Connection code to MySQL.  */
    private static Connection openConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private static void closeConnection(Connection conn) throws SQLException {
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
