/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.UI.SelectionWin;

import com.cse.Controller.FacebookEditor;
import com.cse.Controller.PermissionHolder;
import com.cse.DBAccess.DatabaseExec;
import com.cse.OAuth.WebOAuth;
import com.cse.Utils.GenericUtils.Crypt;
import com.cse.properties.PropertiesXmlFileHandler;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.sql.rowset.CachedRowSet;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author dmbk
 */
public class ApplicationSceneHandler extends Application {

    private static final String APP_ID = "1506540969576022";
    private static final String APP_SECRET = "77f8887895c3b5df16c2a6c9bd3e1d7d";
    private static String token;
    private static Facebook facebook;
    private static String uId;
    private static WindowInterface selectWindow;
    private static WindowInterface userHomeWindow;
    private static WindowInterface albumViewerWindow;
    private static WindowInterface fxBrowser;
    private static WindowInterface loginWindow;
    private static WindowInterface signUpWindow;
    private static Stage applicationStage;

    public static void setuId(String uId) {
        ApplicationSceneHandler.uId = uId;
    }

    public static WindowInterface getSelectWindow() {
        return selectWindow;
    }

    public static WindowInterface getUserHomeWindow() {
        return userHomeWindow;
    }

    public static void refreshApplication(String userId) {
        uId = userId;
        try {
            String sql = "select access_token from user_profile where u_id=?";
            CachedRowSet set = DatabaseExec.executeQuery(sql, uId);
            if (set.next()) {
                token = set.getString("access_token");
                facebook = new FacebookFactory().getInstance();
                facebook.setOAuthAppId("1506540969576022", "77f8887895c3b5df16c2a6c9bd3e1d7d");
                facebook.setOAuthPermissions(PermissionHolder.getPermissionString());
                facebook.setOAuthAccessToken(new AccessToken(token));

            }
        } catch (SQLException ex) {
            Logger.getLogger(SelectWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        selectWindow = new SelectWindow(applicationStage, uId, facebook);
        userHomeWindow = new UserHomeWindow(applicationStage, uId, facebook);

    }

    @Override
    public void start(Stage stage) throws Exception {
        applicationStage = stage;

        //String authUrl = WebOAuth.getAuthUrl(APP_ID, APP_SECRET);
        //fxBrowser = new FXBrowser(applicationStage, authUrl);
        //Scene scene = fxBrowser.getRefreshedScene();
        //uId = "690053834396886";
        //refreshApplication();
        loginWindow = new LoginWindow(applicationStage);
        Scene scene = loginWindow.getRefreshedScene();

        applicationStage.setTitle("Social Media Photo Sync");
        applicationStage.setScene(scene);
        applicationStage.setResizable(false);
        applicationStage.getIcons().add(new Image("file:///" + PropertiesXmlFileHandler.readFromProperties("app.home") + "/res/icons/icon_main.png"));

        applicationStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static boolean proceedWithAuthCode(String code) throws IOException {
        //WebOAuth.setU_pwd(new Crypt().encrypt("dmbk1991"));
        //WebOAuth.setU_name("dulithamethmal@gmail.com");

        boolean success = WebOAuth.doOAuth_2_0Dance(code);
        if (success) {
            uId = WebOAuth.getUserId();
            ApplicationSceneHandler.refreshApplication(uId);

            FacebookEditor editor = new FacebookEditor(token, uId);
            try {
                editor.getAlbumCovers(editor.getAlbums(facebook), facebook);

            } catch (FacebookException ex) {
                Dialogs.create()
                        .owner(applicationStage)
                        .title("Connection error")
                        .masthead("Connection timeout has occured.")
                        .message("Please check your connection status!")
                        .actions(Dialog.Actions.OK)
                        .showWarning();
            }
            System.err.println("done");
            return true;
        } else {
            System.err.println("not done");
            Dialogs.create()
                    .owner(applicationStage)
                    .title("Sign up failed!")
                    .masthead("Username is bound to a different facebook profile.")
                    .message("Please try signing up with a different username or use your usual username"
                            + " to login to your online account so that only your password"
                            + " would be replaced with the new one provided!")
                    .actions(Dialog.Actions.OK)
                    .showError();
            return false;
        }
    }

}
