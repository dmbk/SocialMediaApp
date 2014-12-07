/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.PlatformUtils.DesktopSpecific.EmbeddedResources;

import com.cse.PlatformUtils.DetailHolder;
import com.cse.PlatformUtils.WaitingObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author Dulitha
 */
/*



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
public class Browser extends Application {

    final String authCallBack = "http://dulithamethmal.wix.com/socialmediaphotosync";
    final String deAuthCallBack = "http://dulithamethmal.wix.com/appfail";
    private Scene scene;

    private BorderPane root;

    private WebView webView;

    private WebEngine webEngine;

    private static String initUrl;
    private static WaitingObject waitingObj = WaitingObject.getInstance();

    @Override

    public void start(final Stage stage) throws Exception {

        System.out.println(Thread.currentThread().getName());

        webView = new WebView();

        webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);

        loadPage(initUrl);

        root = new BorderPane();

        root.setPrefSize(1024, 768);

        root.setCenter(webView);

        scene = new Scene(root);

        stage.setTitle("Social Media Photo Sync");

        stage.setScene(scene);
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        final String location = webEngine.getLocation();
                        if (newState == State.SUCCEEDED && location.contains(authCallBack)) {

                            Thread t1 = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    browserController(location);
                                }

                            });
                            t1.setPriority(10);
                            t1.start();
                            stage.close();

                        } else if (newState == State.SUCCEEDED && location.contains(deAuthCallBack)) {
                            stage.close();
                        }
                    }
                });
        //this is the action handler that is triggered when the browaser is loaded with the expected page.
        //the callback url
        //Platform.setImplicitExit(false);
        stage.show();

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
            System.out.println(code);
            //setCode(code);

        } catch (MalformedURLException ex) {
            Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return code.trim();
    }

    /**
     * calls the method in WebOAuth to invoke the method on successful user
     * authorization
     *
     * @param location- the url that is parsed to be extracted
     */
    public static void browserController(String location) {
        DetailHolder.setAuthCode(extractCode(location));
        synchronized (waitingObj) {
            waitingObj.notifyAll();
        }

    }

    public static void launchBrowser(String url) {
        initUrl = url;
        launch();
    }

    public void loadPage(String pageUrl) {

        webEngine.load(pageUrl);

    }

}
