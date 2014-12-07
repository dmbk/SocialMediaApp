/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmbeddedResources;


import java.awt.Dimension;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author Dulitha
 */
public class Main extends Application {

    private Scene scene;

    private BorderPane root;

    private Button reloadButton, goButton;

    private TextField addressField;

    private WebView webView;

    private WebEngine webEngine;

    @Override

    public void start(Stage stage) throws Exception {

        System.out.println(Thread.currentThread().getName());

        //The vertical box that will hold our navigation buttons.
        HBox hBox = new HBox(5);

        hBox.setAlignment(Pos.CENTER);

        //Our buttons for navigation.
        reloadButton = new Button("Reload");

        goButton = new Button("go");

        //Add listeners to the buttons.
        reloadButton.setOnAction(reload);

        goButton.setOnAction(go);

        //The TextField for entering web addresses.
        addressField = new TextField("Enter Web address here...");

        addressField.setPrefColumnCount(50); //make the field at least 50 columns wide.

        //Add all out navigation nodes to the vbox.
        hBox.getChildren().addAll(reloadButton, goButton, addressField);

        //Our weiv that display ther page.
        webView = new WebView();

        //the engine that manages our pages.
        webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);

        webEngine.load("http://www.yahoo.com");

        //our main app layout with 5 regions.
        BorderPane root = new BorderPane();

        root.setPrefSize(1024, 768);

        //Add every node to the BorderPane.
        root.setTop(hBox);

        root.setCenter(webView);

        //Our scene is where all the action in JavaFX happens.  A scene holds all Nodes, and its root node is our BorderPane.
        scene = new Scene(root);

        //the stage manages the scene.
        stage.setTitle("JavaFX Web Browser");

        stage.setScene(scene);

        stage.show();

    }

    /////////////////////////////////////start event handlers for navigation///////////////////////////////
    private EventHandler<ActionEvent> reload = new EventHandler<ActionEvent>() {

        @Override

        public void handle(ActionEvent event) {

            webEngine.reload();

        }

    };

    private EventHandler<ActionEvent> go = new EventHandler<ActionEvent>() {

        @Override

        public void handle(ActionEvent event) {

            System.out.println(addressField.getText());

            webEngine.load("http://" + addressField.getText());

        }

    };

///////////////////////////////////////end event handlers for navigation//////////////////////////////////
    //main method is required for ides without javafxpackagertool support.
    public static void main(String[] args) {

        launch(args);

    }

}
