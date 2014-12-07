/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.UI.SelectionWin;

import com.cse.DBAccess.DatabaseExec;
import com.cse.Utils.GenericUtils.Crypt;
import java.sql.SQLException;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author dmbk
 */
public class LoginWindow implements WindowInterface {

    Stage applicationStage;
    Scene scene;

    public LoginWindow(Stage applicationStage) {
        this.applicationStage = applicationStage;

    }

    public Scene createLoginWindowScene() {

        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        scenetitle.setId("welcome-text");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        final PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 6, 2, 1);

        //add(scenetitle, 0, 0, 2, 1);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                ProgressIndicator indicator = new ProgressIndicator();

                grid.add(indicator, 0, 7, 2, 1);
                try {
                    actiontarget.setFill(Color.FIREBRICK);

                    final String uName = userTextField.getText().trim();
                    final String pwd = pwBox.getText().trim();
                    if ("".equals(uName) || "".equals(pwd)) {
                        actiontarget.setText("Please fill all the fields");
                        return;
                    }

                    actiontarget.setText("Signing in");

                    String sql = "Select u_id,u_pwd from user_profile where u_name=?";
                    CachedRowSet set = DatabaseExec.executeQuery(sql, uName);
                    if (set.next()) {
                        String retPwd = new Crypt().decrypt(set.getString("u_pwd"));
                        if (retPwd.equals(pwd)) {
                            ApplicationSceneHandler.refreshApplication(set.getString("u_id"));
                            Scene newScene = ApplicationSceneHandler.getUserHomeWindow().getRefreshedScene();
                            applicationStage.centerOnScreen();
                            applicationStage.sizeToScene();

                            applicationStage.setScene(newScene);

                        } else {
                            actiontarget.setText("Password entered is wrong");
                        }

                    } else {
                        actiontarget.setText("No email found.You may want to sign up first.");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        Button btnSignUp = new Button("Sign up");
        btnSignUp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (MouseButton.PRIMARY.equals(event.getButton())) {
                    applicationStage.setScene(new SignUpWindow(applicationStage).getRefreshedScene());

                }
            }

        });
        HBox hbBtnSignUp = new HBox(10);
        hbBtnSignUp.setAlignment(Pos.BOTTOM_LEFT);
        hbBtnSignUp.getChildren().add(btnSignUp);
        grid.add(hbBtnSignUp, 0, 4);

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
