/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.DBAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import javax.swing.JOptionPane;

/**
 *
 * @author Dulitha
 */
public class DatabaseExec {

    public static int execNonQuery(String nonAssigned, String... varArgs) {

        Connection con = DataAccess.getConnection();
        PreparedStatement ps;
        int rows = -1;
        try {
            ps = con.prepareStatement(nonAssigned);

            assignVars(ps, varArgs);
            rows = ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseExec.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            DataAccess.closeConnection(con);

        }
        return rows;
    }

    public static CachedRowSet executeQuery(String nonAssigned, String... varArgs) {
        CachedRowSet crs = null;
        ResultSet set;
        PreparedStatement ps;
        Connection con = DataAccess.getConnection();
        try {
            ps = con.prepareStatement(nonAssigned);
            assignVars(ps, varArgs);
            set = ps.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(set);

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseExec.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DataAccess.closeConnection(con);
        }
        return crs;
    }

    public static void assignVars(PreparedStatement ps, String... varArgs) throws SQLException {
        if (varArgs == null || varArgs.length == 0) {
            return;
        }
        for (int i = 0; i < varArgs.length; i++) {
            ps.setString(i + 1, varArgs[i]);
        }
    }

    public static void main(String... args) {

        String sql;
        /*sql = "CREATE TABLE user_profile "
         + "(u_id TEXT PRIMARY KEY    NOT NULL,"
         + " u_name           TEXT    NOT NULL,"
         + " u_pwd            TEXT    NOT NULL,"
         + " access_token     TEXT    NOT NULL"
         + " );";        
         execNonQuery(sql, null);
         sql = "CREATE TABLE albums "
         + "    (album_id TEXT PRIMARY KEY    NOT NULL,"
         + "     album_name           TEXT    ,"
         + "	 cover_uri           TEXT,"
         + "     album_uri     TEXT    NOT NULL,"
         + "	 sync 		 INTEGER	NOT NULL,"
         + "	 u_id	TEXT	NOT NULL,"
         + "	 FOREIGN KEY (u_id) REFERENCES user_profile(u_id)"
         + "	 );";
         execNonQuery(sql, null);
         sql = "CREATE TABLE photos \n"
         + "    (photo_id TEXT PRIMARY KEY    NOT NULL,\n"
         + "   	 photo_uri		      TEXT    , 			\n"
         + "     album_id     TEXT    NOT NULL,\n"
         + "	 FOREIGN KEY (album_id) REFERENCES albums(album_id)\n"
         + "	 );";
         execNonQuery(sql, null);*/
        sql = "INSERT INTO user_profile (u_id,u_name,u_pwd,access_token) "
                + "VALUES (?,?,?,?);";

        execNonQuery(sql, "110340e", "Peter", "dmbk1991", "hjwkjwghcw631f1y62d1u2d12d121d12");

        sql = "select u_name from user_profile where u_id=?";
        CachedRowSet set = executeQuery(sql, "110340e");
        try {
            if (set.next()) {
                System.out.println(set.getString("u_name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseExec.class.getName()).log(Level.SEVERE, null, ex);

        }

        String updateQuery = "UPDATE user_profile SET access_token = ?, u_name = ? WHERE u_id= ?;";
        DatabaseExec.execNonQuery(updateQuery, "dsajkdasdas", "Kaweesh", "110336e");
    }

}
