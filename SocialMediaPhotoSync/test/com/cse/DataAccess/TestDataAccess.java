/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.DataAccess;

import com.cse.DBAccess.DataAccess;
import java.sql.Connection;
import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dulitha
 */
public class TestDataAccess {

    Connection con;
    String dbType = "SQLite";
    @Before
    public void setUp() {
        con = DataAccess.getConnection();

    }

    @Test
    public void test_DBType() {
        assertEquals(dbType, DataAccess.getDbType(con));
    }
    @Test
    public void test_DBCOnnection(){
        org.junit.Assert.assertTrue(DataAccess.testConnection());
    }

    public void tearDown() {
        DataAccess.closeConnection(con);
    }
}
