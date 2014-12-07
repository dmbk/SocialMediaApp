package com.UI.SelectionWin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class FXBrowser implements WindowInterface {

    final static String authCallBack = "http://dulithamethmal.wix.com/socialmediaphotosync";
    final static String deAuthCallBack = "http://dulithamethmal.wix.com/appfail";
    private Scene scene;

    private BorderPane root;

    private WebView webView;

    private WebEngine webEngine;
    private Stage applicationStage;
    private String initUrl;

    public FXBrowser(Stage stage, String initUrl) {
        this.initUrl = initUrl;
        this.applicationStage = stage;
    }

    public Scene createFXBrowserScene() {

        webView = new WebView();

        webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);

        webEngine.load(initUrl);

        root = new BorderPane();
        final Button btnTop = new Button("View the albums");
        btnTop.setDisable(true);
        btnTop.setPadding(new Insets(10));
        btnTop.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Scene scene = ApplicationSceneHandler.getSelectWindow().getRefreshedScene();
                applicationStage.setScene(scene);

            }
        });
        BorderPane topPane = new BorderPane();
        topPane.setPadding(new Insets(10));
        topPane.setRight(btnTop);

        final Label lblInstr = new Label("Your authorization is required to continue.");
        lblInstr.setPadding(new Insets(10));
        topPane.setLeft(lblInstr);
        final ProgressIndicator indicator = new ProgressIndicator();

        topPane.setCenter(indicator);
        root.setTop(topPane);
        root.setPrefSize(1100, 650);

        root.setCenter(webView);

        scene = new Scene(root);

        //stage.setTitle("Social Media Photo Sync");
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        final String location = webEngine.getLocation();
                        if (newState == State.SUCCEEDED && location.contains(authCallBack)) {
                            lblInstr.setText("Thank you!... Please wait.This may take a while.");
                            /*Service<Integer> service = new Service<Integer>() {
                             @Override
                             protected Task<Integer> createTask() {
                             return new Task<Integer>() {
                             @Override
                             protected Integer call() {
                             String code = extractCode(location);
                             if (code != null) {

                             boolean success = ApplicationSceneHandler.proceedWithAuthCode(code);
                             //applicationStage.close();
                             }
                             return 0;

                             }

                             };
                             }
                             };
                             indicator.progressProperty().bind(service.progressProperty());
                             btnTop.disableProperty().bind(service.runningProperty());

                             try {
                             Thread.sleep(2000);
                             } catch (InterruptedException ex) {
                             Logger.getLogger(FXBrowser.class.getName()).log(Level.SEVERE, null, ex);
                             }
                             lblInstr.setText("You will be able to view your albums in a while.");
                             service.start();*/

                            String code = extractCode(location);
                            if (code != null) {

                                try {
                                    boolean success = ApplicationSceneHandler.proceedWithAuthCode(code);
                                    if (!success) {
                                        //applicationStage.close();
                                        applicationStage.setScene(new SignUpWindow(applicationStage).getRefreshedScene());
                                    } else {
                                        btnTop.setDisable(false);
                                        Scene scene = ApplicationSceneHandler.getSelectWindow().getRefreshedScene();
                                        applicationStage.setScene(scene);
                                    }
                                } catch (IOException ex) {
                                    Logger.getLogger(FXBrowser.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        } else if (newState == State.SUCCEEDED && location.contains(deAuthCallBack)) {
                            lblInstr.setText("Sorry!! you may need to authorize the app. This window is closing in 5 seconds.");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(FXBrowser.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            applicationStage.close();
                        }
                    }
                });
        scene.getStylesheets().add(SelectWindow.class.getResource("style.css").toExternalForm());

        return scene;
    }

    /**
     * extracts the code part from the location url provided
     *
     * @param location- the url to be examined for auth code
     * @return- the query string code part
     *
     */
    public static String extractCode(String location) {
        String code = null;
        try {
            URL url = new URL(location);
            String query = url.getQuery();
            code = query.replaceAll("code=", "").replaceAll("#_=_", "");
           
            //setCode(code);

        } catch (MalformedURLException ex) {

        }
        return code.trim();
    }

    @Override
    public Scene getScene() {
        if (this.scene == null) {
            this.createFXBrowserScene();
        }
        return this.scene;
    }

    @Override
    public Scene getRefreshedScene() {
        Scene newScene = null;
        newScene = this.createFXBrowserScene();
        return newScene;
    }
}
