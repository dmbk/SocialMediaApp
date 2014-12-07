package com.cse.OAuth;

import com.cse.Controller.PermissionHolder;
import com.cse.DBAccess.DatabaseExec;
import static com.cse.DBAccess.DatabaseExec.execNonQuery;

import facebook4j.Facebook;
import facebook4j.RawAPIResponse;
import facebook4j.auth.AccessToken;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.sql.SQLException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

public class WebOAuth {

    private static final String APP_ID = "1506540969576022";
    private static final String APP_SECRET = "77f8887895c3b5df16c2a6c9bd3e1d7d";
    private static final String NETWORK_NAME = "Facebook";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;
    private static String userAuthCode;
    private static OAuthService service;
    private static Verifier verifier;
    private static Token accesTokenObj;
    private static String accessTokenRecieved;//when the app starts the access token is loaded and cached here for 
    // repetitive usage

    // Jackson ObjectMapper
    private static ObjectMapper objectMapper;

    private static String userId;
    private static String u_pwd = null;//encrypted
    private static String u_name = null;

    /**
     * run this method before login with a new user
     */
    public static void refreshClass() {
        u_name = null;
        u_pwd = null;
        userAuthCode = null;
        userId = null;

    }

    public static void setU_name(String u_name) {
        WebOAuth.u_name = u_name;
    }

    public static void setU_pwd(String u_pwd) {
        WebOAuth.u_pwd = u_pwd;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        WebOAuth.userId = userId;
    }

    public static void setAccessToken(String accessToken) {
        WebOAuth.accessTokenRecieved = accessToken;
    }

    public static String getAccessToken() {
        return accessTokenRecieved;
    }

    /**
     * The access token is refreshed using facebook4j libraries
     *
     * @param facebook -The facebook object containing the data of the current
     * user
     * @param currentToken -The current access token of the current user
     * @return -The refreshed accessToken
     *
     *
     */
    private AccessToken refreshToken(Facebook facebook, AccessToken currentToken) throws Exception {

        @SuppressWarnings("Convert2Diamond")
        Map<String, String> params = new HashMap<String, String>();
        params.put("client_id", APP_ID);
        params.put("client_secret", APP_SECRET);
        params.put("grant_type", "fb_exchange_token");
        params.put("fb_exchange_token", currentToken.getToken());

        RawAPIResponse apiResponse = facebook.callGetAPI("/oauth/access_token", params);

        String response = apiResponse.asString();
        AccessToken newAccessToken = new AccessToken(response);

        facebook.setOAuthAccessToken(newAccessToken);

        return newAccessToken;
    }

    /**
     * performs the OAuth dance for facebook graph api
     *
     * @param authCode -thee auth code recieved from facebook
     * @return -success
     */
    public static boolean doOAuth_2_0Dance(final String authCode) {

        String accessToken = getAccessToken(authCode);
        setAccessToken(accessToken);
        return storeAccessTokenDetails(accessToken);

    }

    /**
     * When a user authorizes the app the app needs to store thee access token
     * received and and other relevant user data
     *
     * @param accessTokenDerived
     * @return- the success of the process
     */
    public static boolean storeAccessTokenDetails(String accessTokenDerived) {

        Response resp = getResponseForProfile(accesTokenObj);
        try {
            String uId = getUserData(resp, "id");
            userId = uId;

            String userName = u_name;
            String password = u_pwd;
            if (password == null) {
                return false;
            }
            setUserId(uId);

            String selectQuery = "select u_id from user_profile where u_id=?";
            CachedRowSet set = DatabaseExec.executeQuery(selectQuery, uId);

            String sqlVerify = "select u_id from user_profile where u_name=?";
            CachedRowSet set2 = DatabaseExec.executeQuery(sqlVerify, userName);
            try {
                if (set.next()) {
                    if (set2.next() && uId.equals(set2.getString("u_id"))) {
                        String updateQuery = "UPDATE user_profile SET access_token = ?,u_pwd=? WHERE u_id= ?;";
                        DatabaseExec.execNonQuery(updateQuery, accessTokenDerived, password, uId);
                        return true;
                    } else {
                        return false;
                    }
                }
                if (set2.next()) {
                    return false;
                }

            } catch (SQLException ex) {
                Logger.getLogger(WebOAuth.class.getName()).log(Level.SEVERE, null, ex);
            }

            String sql = "INSERT INTO user_profile (u_id,u_name,u_pwd,access_token) "
                    + "VALUES (?,?,?,?);";

            execNonQuery(sql, uId, userName, password, accessTokenDerived);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(WebOAuth.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    /**
     * calling the fb graph 'me' endpoint and returns the response containing
     * the user profile data
     *
     * @param accessToken
     * @return response object
     */
    public static Response getResponseForProfile(Token accessToken) {
        objectMapper = new ObjectMapper();

        OAuthRequest oauthRequest
                = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
        if (service != null) {
            service.signRequest(accessToken, oauthRequest);
        }
        return oauthRequest.send();
    }//gets the response with the JSON returned by facebook regarding user data

    /*sample JSON object
     {
     "id":"4",
     "name":"Mark Zuckerberg",
     "first_name":"Mark",
     "last_name":"Zuckerberg",
     "link":"https:\/\/www.facebook.com\/zuck",
     "username":"zuck",
     "gender":"male",
     "locale":"en_US"
     } 
     */
    /**
     * extracting user data using the response objects JSON object
     *
     * @param key- key for the field to be extracted
     * @param response- response object retrieved from a Facebook graph api node
     * @return -the the key field value as String
     * @throws java.io.IOException
     */
    public static String getUserData(Response response, String key) throws IOException {
        String responseBody = response.getBody();
        if (objectMapper != null) {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode node = jsonNode.get(key);
            return node.asText();
        }
        return null;
    }

    public static String getAccessToken(String code) {

        verifier = new Verifier(code.trim());

        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        accesTokenObj = accessToken;

        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();

        return accessToken.getToken();
    }
//"http://localhost:8080/SEProjectApplication/index.jsp"

    public static String getAuthUrl(String appId, String appSecret) {
        String apiKey = appId;
        String apiSecret = appSecret;
        service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback("http://dulithamethmal.wix.com/socialmediaphotosync")
                .scope(PermissionHolder.getPermissionString())
                .build();
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);

        return authorizationUrl;

    }

    public static void setCode(String code) {
        userAuthCode = code;

    }

    public static void setHttpProxySettings(String proxyUrl, String proxyPort,
            final String authUser, final String authPassword) {
        Properties sysProperties = System.getProperties();

        sysProperties.setProperty("http.proxyHost", proxyUrl);
        sysProperties.setProperty("http.proxyPort", proxyPort);
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);

        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(authUser, authPassword
                        .toCharArray());
            }
        });
        //  this.savePrefs(proxyUrl, proxyPort, authUser, authPassword, "http");

    }

    public static void setFtpProxySettings(String proxyUrl, String proxyPort,
            final String authUser, final String authPassword) {
        Properties sysProperties = System.getProperties();

        sysProperties.setProperty("ftp.proxyHost", proxyUrl);
        sysProperties.setProperty("ftp.proxyPort", proxyPort);
        System.setProperty("ftp.proxyUser", authUser);
        System.setProperty("ftp.proxyPassword", authPassword);

        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(authUser, authPassword
                        .toCharArray());
            }
        });
        // this.savePrefs(proxyUrl, proxyPort, authUser, authPassword, "ftp");
    }

    public static void setHttpsProxySettings(String proxyUrl, String proxyPort,
            final String authUser, final String authPassword) {
        Properties sysProperties = System.getProperties();

        sysProperties.setProperty("https.proxyHost", proxyUrl);
        sysProperties.setProperty("https.proxyPort", proxyPort);
        System.setProperty("https.proxyUser", authUser);
        System.setProperty("https.proxyPassword", authPassword);

        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(authUser, authPassword
                        .toCharArray());
            }
        });
        // this.savePrefs(proxyUrl, proxyPort, authUser, authPassword, "https");
    }

    public static void main(String... args) {
        //setHttpProxySettings("cache.mrt.ac.lk", "3128", "110305B", "dmbk1991@CS");
        Properties sysProperties = System.getProperties();

        sysProperties.remove("http.proxyHost");
        sysProperties.remove("http.proxyPort");
        System.setProperty("http.proxyUser", "");
        System.setProperty("http.proxyPassword", "");

    }

}
