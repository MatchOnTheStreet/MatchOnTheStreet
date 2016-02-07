package com.cse403.matchonthestreet;

import java.sql.*;

/**
 * Created by Iris on 2/7/16.
 */
public class DBManager {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://c-24-16-210-83.hsd1.wa.comcast.net/databasename";

    //  Database credentials
    static final String USER = "ruijiw@c-24-16-210-83.hsd1.wa.comcast.net";
    static final String PASS = "bZQf23xm";

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

}
