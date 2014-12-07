/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.DBAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DataAccess {

    private static DataAccess dataObj;

    private DataAccess() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConnection() {
        if (dataObj == null) {
            dataObj = new DataAccess();
        }
        Connection conn = null;

        try {
            //Create the connection object from driver
            conn = DriverManager.getConnection("jdbc:sqlite:social_app.db");
        } catch (SQLException e) {

            System.err.println(e + "Function: GetConnection in Class: DataAccess\n Cannot establish the connection");
        }
        return conn;
    }

    public static Boolean testConnection() {
        if (getConnection() == null) {
            return false;
        }
        return true;
    }

    public static String getDbType(Connection connection) {
        try {
            if (connection != null) {
                return connection.getMetaData().getDatabaseProductName();
            }
        } catch (Exception e) {
            System.err.println(e + "Couldn't identifiy the database type");
        }
        return null;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.err.println(ex + "Connection close faild");
        }
    }

    public static void main(String... args) {
        Connection con = getConnection();
        System.err.println(getDbType(con));
        closeConnection(con);
    }

}
