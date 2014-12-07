/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.UI.SelectionWin;

import com.cse.OAuth.WebOAuth;
import com.cse.Utils.GenericUtils.Crypt;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author dmbk
 */
public class SignUpWindow implements WindowInterface {

    

    private static final String APP_ID = "1506540969576022";
    private static final String APP_SECRET = "77f8887895c3b5df16c2a6c9bd3e1d7d";
    Stage applicationStage;
    Scene scene;

    public SignUpWindow(Stage applicationStage) {

        this.applicationStage = applicationStage;

    }

    public Scene createLoginWindowScene() {

        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Sign Up");
        scenetitle.setId("welcome-text");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        Label pwCn = new Label("Confirm password:");
        grid.add(pwCn, 0, 3);

        final PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        final PasswordField pwBoxConfirm = new PasswordField();
        grid.add(pwBoxConfirm, 1, 3);

        Button btn = new Button("Sign up");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 5);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 7, 2, 1);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                ProgressIndicator indicator = new ProgressIndicator();
                grid.add(indicator, 0, 8, 2, 1);
                actiontarget.setFill(Color.FIREBRICK);
                final String uName = userTextField.getText().trim();
                final String pwd = pwBox.getText().trim();
                final String pwdCnfm = pwBoxConfirm.getText().trim();
                if ("".equals(uName) || "".equals(pwd) || "".equals(pwdCnfm)) {
                    actiontarget.setText("Please fill all the fields");
                    return;
                }
                if (!pwd.equals(pwdCnfm)) {
                    actiontarget.setText("Passwords do not match.");
                    return;
                }
                actiontarget.setText("Signning up");
                WebOAuth.setU_name(uName);
                WebOAuth.setU_pwd(new Crypt().encrypt(pwd));

                String authUrl = WebOAuth.getAuthUrl(APP_ID, APP_SECRET);
                FXBrowser fxBrowser = new FXBrowser(applicationStage, authUrl);
                Scene sceneBrowser = fxBrowser.getRefreshedScene();
                applicationStage.setScene(sceneBrowser);
                
            }
        });

        scene = new Scene(grid, 300, 300);

        scene.getStylesheets().add(SelectWindow.class.getResource("login.css").toExternalForm());

        return scene;

    }

    @Override
    public Scene getScene() {
        if (this.scene == null) {
            this.createLoginWindowScene();
        }
        return this.scene;
    }

    @Override
    public Scene getRefreshedScene() {
        Scene newScene = null;
        newScene = this.createLoginWindowScene();
        return newScene;
    }

}
