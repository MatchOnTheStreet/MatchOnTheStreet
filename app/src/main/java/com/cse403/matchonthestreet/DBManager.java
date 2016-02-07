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

    /* Connection code to MySQL.  */
    public void openConnection() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 6: Clean-up environment
            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
    }
}
