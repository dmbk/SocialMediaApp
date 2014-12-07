/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.test.OAuth;

import com.cse.OAuth.WebOAuth;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;
import org.scribe.model.Response;
import org.scribe.model.Token;

/**
 *
 * @author Dulitha
 */
public class TestWebOAuth {

    String accessTioken;
    Response response;
    String appID = "1506540969576022";
    String appSecret = "77f8887895c3b5df16c2a6c9bd3e1d7d";
    String userId="312262722278060";
    String userName="Dulitha Kularathne";

    
    public void setUp() {
        //WebOAuth.doOAuth_2_0Dance();
        accessTioken = WebOAuth.getAccessToken();
        response = WebOAuth.getResponseForProfile(new Token(accessTioken, appSecret));
    }

    @Test
    public void test_getUserData() {
        if(response== null){
            return;
        }
        try {
            assertEquals(userId, WebOAuth.getUserData(response,"id"));
            assertEquals(userName,  WebOAuth.getUserData(response,"name"));
        } catch (IOException ex) {
            Logger.getLogger(TestWebOAuth.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void test_getAuthUrl() {

        String authUrl = "https://www.facebook.com/dialog/oauth?client_id=1506540969576022&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2FSEProjectApplication%2Findex.jsp&scope=user_photos%2Cpublish_stream%2Cpublish_actions%2Coffline_access";
        assertEquals(authUrl, WebOAuth.getAuthUrl(appID, appSecret));
    }

}
