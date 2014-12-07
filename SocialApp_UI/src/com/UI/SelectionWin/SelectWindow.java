/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.UI.SelectionWin;

import com.cse.Controller.FacebookEditor;
import com.cse.DBAccess.DatabaseExec;
import static com.cse.DBAccess.DatabaseExec.executeQuery;
import com.cse.OAuth.WebOAuth;
import com.cse.WindowControllers.SelectWindowController;
import com.cse.properties.PropertiesXmlFileHandler;
import facebook4j.Album;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Photo;
import facebook4j.ResponseList;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.sql.rowset.CachedRowSet;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author dmbk
 */
public class SelectWindow implements WindowInterface {

    private static final String SELECT_WINDOW = "sw_scene";
    private boolean isChangable;//whether the user can change the albums being synced
    private List<ImageView> imageViewList;
    private String uId;
    private Facebook facebook;
    private Map<String, ImageView> imgViewMap;
    private Set<String> albumIdSet;//album ids to be synced
    private Set<String> syncedAlbumIdSet;//album ids processed (synced) 

    private Stage applicationStage;// the stage of the application
    private ImageView node;
    private Scene scene;

    public SelectWindow(Stage applicationStage, String uId, Facebook facebook) {
        this.applicationStage = applicationStage;
        this.uId = uId;
        this.facebook = facebook;
    }

    public Scene createSelectWindowScene() throws MalformedURLException, IOException, SQLException {
        albumIdSet = new LinkedHashSet<>();
        syncedAlbumIdSet = new LinkedHashSet<>();
        imageViewList = new ArrayList<ImageView>();
        imgViewMap = new LinkedHashMap<>();
        isChangable = true;
        final BorderPane root;

        final ScrollPane sp;
        final VBox vb;
        HBox hb;
        // final HBox hBoxTop;
        final Label lblName;
        //final ProgressIndicator indicator;
        final ImageView profPicView;
        final CheckBox chkBoxSelectAll;
        final VBox vBoxUpperLeft;
        final BorderPane upperBordorPane;
        final Label lblSyncProg;
        final VBox vBoxRightUpper;
        final Button btnSignOut;
        final HBox hBoxWholeTop;
        final VBox vBoxTop;
        final Label lblInstr;
        final Button btnStartSync;
        final Button btnGallery;
        final Label lblAlbumName = new Label();
        sp = new ScrollPane();
        vb = new VBox(10);
        vb.setPadding(new Insets(10));
        vb.setSpacing(20);

        Image[] images = new Image[5];
        ImageView[] pics = new ImageView[5];
        //final String[] imageNames = new String[]{"fw1.jpg", "fw2.jpg",
        //  "fw3.jpg", "fw4.jpg", "fw5.jpg"};

        String sql = "select album_id,cover_uri,album_name from albums where u_id=?";
        CachedRowSet set = executeQuery(sql, uId);

        columns:
        for (int j = 0; j < Integer.MAX_VALUE; j++) {
            hb = new HBox(40);
            //set.next();

            for (int i = 0; i < 5; i++) {

                if (!set.next()) {
                    if (i > 0) {
                        vb.getChildren().add(hb);
                        vb.setOpacity(1);
                        sp.setContent(vb);
                    }
                    break columns;
                }

                //"E:/facebook test albums/albums/690053834396886/107580375977571/cover_Mobile Uploads"
                //images[i] = new Image(new URL("file:///" + PropertiesXmlFileHandler.readFromProperties("user.folder") + "/albums/690053834396886/687648211304115/cover_~US~").openStream());
                String cover_uri = set.getString("cover_uri");
                if (cover_uri == null) {
                    cover_uri = PropertiesXmlFileHandler.readFromProperties("app.home") + "/res/empty_cover/default_cover.jpg";
                }
                images[i] = new Image(new URL("file:///" + cover_uri).openStream());

                //final String albumName = set.getString("album_name");
                final String albumId;

                albumId = set.getString("album_id");
                final String albumName = set.getString("album_name");

                pics[i] = new ImageView(images[i]);
                pics[i].setFitWidth(150);
                pics[i].setFitHeight(120);
                imgViewMap.put(albumId, pics[i]);
                //node.setPickOnBounds(false);
                final int index = 5 * j + i;
                imageViewList.add(index, pics[i]);
                pics[i].addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    int imageIndex = index;
                    boolean isSelected = false;
                    String alId = albumId;
                    String albumN = albumName;

                    @Override
                    public void handle(MouseEvent event) {
                        if (MouseButton.PRIMARY.equals(event.getButton())) {
                            lblAlbumName.setText("The album is " + albumName);

                            if (!isChangable) {
                                return;
                            }
                            if (!isSelected) {
                                        //ColorAdjust colorAdjust = new ColorAdjust();

                                //colorAdjust.setBrightness(0.7);
                                DropShadow dropShadow = new DropShadow();
                                dropShadow.setRadius(5.0);
                                dropShadow.setOffsetX(7.0);
                                dropShadow.setOffsetY(6.0);
                                dropShadow.setColor(Color.rgb(255, 255, 255, 1));
                                imageViewList.get(index).setEffect(dropShadow);
                                //imageViewList.get(index).setEffect(colorAdjust);
                                if (!albumIdSet.contains(alId)) {
                                    albumIdSet.add(alId);
                                }
                                isSelected = true;
                            } else if (isSelected) {
                                        //ColorAdjust colorAdjust = new ColorAdjust();

                                //colorAdjust.setBrightness(0);
                                DropShadow dropShadow = new DropShadow();
                                dropShadow.setRadius(5.0);
                                dropShadow.setOffsetX(6.0);
                                dropShadow.setOffsetY(6.0);
                                dropShadow.setColor(Color.color(0.5, 0.6, 0.6));
                                imageViewList.get(index).setEffect(dropShadow);
                                //imageViewList.get(index).setEffect(colorAdjust);
                                if (albumIdSet.contains(alId)) {
                                    albumIdSet.remove(alId);
                                }
                                isSelected = false;
                            }

                        }

                        event.consume();
                    }
                });

                //pics[i].setPreserveRatio(true);
                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(5.0);
                dropShadow.setOffsetX(6.0);
                dropShadow.setOffsetY(6.0);
                dropShadow.setColor(Color.color(0.5, 0.6, 0.6));
                pics[i].setEffect(dropShadow);

                hb.getChildren().add(pics[i]);

            }
            vb.getChildren().add(hb);
            vb.setOpacity(1);
            sp.setContent(vb);
        }
        root = new BorderPane();
        root.setPrefSize(940, 650);
        root.setMaxWidth(940);
        //sp.setOpacity(0.75);
////////The rest is about making the upper part of the UI///////////////////////////////////////
///////////////The upper left///////////////////////////////////
        chkBoxSelectAll = new CheckBox("Select all");
        chkBoxSelectAll.setAlignment(Pos.BOTTOM_LEFT);
        vBoxUpperLeft = new VBox(5);

        Text textWelcome = new Text();
        textWelcome.setText("Facebook Albums");
        textWelcome.setId("welcome-text");

        lblInstr = new Label("Please select your prefered albums");
        lblInstr.setFont(new Font("ROD", 14));
        vBoxUpperLeft.getChildren().addAll(textWelcome, lblInstr, chkBoxSelectAll);
        //vBoxUpperLeft.fillWidthProperty();

///////////////The center///////////////////////////////////
        vBoxTop = new VBox(20);
        lblSyncProg = new Label("{{{Download}}}");

        btnStartSync = new Button("Start Downloading");
        btnStartSync.setPrefSize(lblSyncProg.getPrefWidth(), 30);
        // btnStartSync.setPrefHeight(30);
        btnStartSync.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {

                if (t.getButton().equals(MouseButton.PRIMARY)) {

                    isChangable = false;

                    Service<Void> service = new Service<Void>() {
                        @Override
                        protected Task<Void> createTask() {
                            return new Task<Void>() {
                                @Override
                                protected Void call()
                                        throws InterruptedException {
                                    updateMessage("Initializing process");
                                    Iterator itr = albumIdSet.iterator();
                                    int size = albumIdSet.size();
                                    updateProgress(0, size);
                                    int prog = 1;
                                    try {
                                        while (itr.hasNext() && !isCancelled()) {

                                            String album_id = (String) itr.next();

                                            ResponseList<Photo> photos = facebook.getAlbumPhotos(album_id);

                                            SelectWindowController.setFacebook(facebook);
                                            SelectWindowController.setUserId(uId);
                                            SelectWindowController.downloadAlbumPhotos(photos, album_id);

                                            updateProgress(prog, size);
                                            updateMessage(prog + " album(s) download complete");

                                            prog += 1;
                                        }
                                    } catch (FacebookException ex) {
                                        Dialogs.create()
                                                .owner(applicationStage)
                                                .title("Connection error")
                                                .masthead("Connection timeout has occured.")
                                                .message("Please check your connection status!")
                                                .actions(Dialog.Actions.OK)
                                                .showWarning();
                                        return null;
                                    }
                                    isChangable = true;
                                    syncedAlbumIdSet.addAll(albumIdSet);
                                    //albumIdSet.removeAll(syncedAlbumIdSet);
                                    updateMessage("Download complete. " + (prog - 1) + " album(s) downloaded.");
                                    Thread.sleep(5000);
                                    return null;
                                }
                            };
                        }
                    };

                    Dialogs.create()
                            .owner(applicationStage)
                            .title("Download progress")
                            .masthead("Downloading " + albumIdSet.size() + " albums you selected")
                            .showWorkerProgress(service);

                    service.start();

                }
                t.consume();
            }
        });
        lblSyncProg.setAlignment(Pos.TOP_CENTER);
        btnStartSync.setAlignment(Pos.CENTER);
        vBoxTop.getChildren().addAll(lblSyncProg, btnStartSync);
///////////////The upper Right///////////////////////////////////

        profPicView = new ImageView(new Image("file:///" + PropertiesXmlFileHandler.readFromProperties("user.folder") + "/albums/" + uId + "/prof_pic"));

        int depth = 40; //Setting the uniform variable for the glow width and height

        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.BLACK);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);

        profPicView.setEffect(borderGlow);
        //lblName = new Label("User");//give username
        btnSignOut = new Button("Sign Out");
        //lblName.setAlignment(Pos.CENTER);
        btnSignOut.setAlignment(Pos.CENTER);
        chkBoxSelectAll.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (MouseButton.PRIMARY.equals(event.getButton())) {
                    if (chkBoxSelectAll.isSelected()) {
                        Set<String> keySet = imgViewMap.keySet();
                        albumIdSet = new LinkedHashSet<>(keySet);
                        Iterator itr = albumIdSet.iterator();
                        while (itr.hasNext()) {
                            String idAlb = (String) itr.next();
                            ImageView tView = imgViewMap.get(idAlb);
                            DropShadow dropShadow = new DropShadow();
                            dropShadow.setRadius(5.0);
                            dropShadow.setOffsetX(7.0);
                            dropShadow.setOffsetY(6.0);
                            dropShadow.setColor(Color.rgb(255, 255, 255, 1));

                            tView.setEffect(dropShadow);

                        }
                    } else if (!chkBoxSelectAll.isSelected()) {
                        Set<String> keySet = imgViewMap.keySet();

                        Iterator itr = keySet.iterator();
                        while (itr.hasNext()) {
                            String idAlb = (String) itr.next();
                            ImageView tView = imgViewMap.get(idAlb);
                            DropShadow dropShadow = new DropShadow();
                            dropShadow.setRadius(5.0);
                            dropShadow.setOffsetX(6.0);
                            dropShadow.setOffsetY(6.0);
                            dropShadow.setColor(Color.color(0.5, 0.6, 0.6));
                            tView.setEffect(dropShadow);

                        }
                        albumIdSet = new LinkedHashSet<String>();
                    }
                }
            }

        });
        profPicView.setFitWidth(55);
        profPicView.setFitHeight(55);

        btnSignOut.minWidth(75);

        btnSignOut.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (MouseButton.PRIMARY.equals(event.getButton())) {
                    WebOAuth.refreshClass();
                    //applicationStage.setScene(new LoginWindow(applicationStage).getRefreshedScene());
                    applicationStage.close();
                }
            }

        });
        vBoxRightUpper = new VBox(10);
        vBoxRightUpper.getChildren().addAll(profPicView, btnSignOut);
        //vBoxRightUpper.fillWidthProperty();

        upperBordorPane = new BorderPane();

        hBoxWholeTop = new HBox(135);
        hBoxWholeTop.getChildren().addAll(vBoxUpperLeft, vBoxTop, vBoxRightUpper);
        hBoxWholeTop.fillHeightProperty();
        hBoxWholeTop.setPadding(new Insets(5, 20, 5, 20));
        //hBoxWholeTop.setStyle("-fx-background-color: DAE6F3;");
        hBoxWholeTop.setOpacity(1);
        //upperBordorPane.setStyle("-fx-background-image: url(\"background_f.jpg\")");
        upperBordorPane.setCenter(hBoxWholeTop);
        hBoxWholeTop.prefWidthProperty().bind(root.prefWidthProperty());
        upperBordorPane.prefWidthProperty().bind(root.prefWidthProperty());

///////////////////Upper right corner//////////////////////////
        Image img = new Image("file:///" + PropertiesXmlFileHandler.readFromProperties("app.home") + "/res/display.jpg");
        ImageView imgView = new ImageView(img);

        imgView.setEffect(borderGlow);
        VBox vbUpperRightCorner = new VBox(10);
        Label labelRightCorner = new Label("Facebook");

        imgView.setFitWidth(75);
        imgView.setFitHeight(75);
        vbUpperRightCorner.setPadding(new Insets(5, 10, 5, 10));
        vbUpperRightCorner.getChildren().addAll(labelRightCorner, imgView);
        upperBordorPane.setRight(vbUpperRightCorner);

        root.setTop(upperBordorPane);

        sp.setStyle("-fx-background: rgb(80,80,80);");
        root.setCenter(sp);

///////////////Bottom part of the pane/////////////////////////////////////////////////////
        btnGallery = new Button("Go to gallery");
        btnGallery.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (t.getButton() == MouseButton.PRIMARY) {
                    Scene nextScene = ApplicationSceneHandler.getUserHomeWindow().getRefreshedScene();
                    applicationStage.setScene(nextScene);
                }
            }
        });

        btnGallery.setPrefHeight(30);

        Button btnRefresh = new Button("Refresh albums");
        btnRefresh.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (t.getButton() == MouseButton.PRIMARY) {
                    try {
                        String token = null;
                        String sql = "Select access_token from user_profile where u_id=?";
                        String sqlIds = "Select album_id from albums where u_id=?";
                        CachedRowSet set = DatabaseExec.executeQuery(sql, uId);
                        CachedRowSet setIds = DatabaseExec.executeQuery(sqlIds, uId);
                        if (set.next()) {
                            token = set.getString("access_token");
                            FacebookEditor editor = new FacebookEditor(token, uId);
                            ResponseList<Album> albums = editor.getAlbums(facebook);
                            Map<String, Album> albumIdMap = new LinkedHashMap<>();
                            if (albums != null) {
                                Iterator itr = albums.iterator();
                                while (itr.hasNext()) {
                                    Album al = (Album) itr.next();
                                    String alId = al.getId();
                                    albumIdMap.put(alId, al);
                                }
                                //editor.getAlbumCovers(, facebook);
                                while (setIds.next()) {
                                    albumIdMap.remove(setIds.getString("album_id"));
                                }
                                albums.retainAll(albumIdMap.values());
                                editor.getAlbumCovers(albums, facebook);
                            }

                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(SelectWindow.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FacebookException ex) {
                        Dialogs.create()
                                .owner(applicationStage)
                                .title("Connection error")
                                .masthead("Connection timeout has occured.")
                                .message("Please check your connection status!")
                                .actions(Dialog.Actions.OK)
                                .showWarning();
                    }
                    Scene newScene = ApplicationSceneHandler.getSelectWindow().getRefreshedScene();
                    applicationStage.setScene(newScene);
                }
            }
        });

        btnRefresh.setPrefHeight(30);

        BorderPane bottomPane = new BorderPane();
        bottomPane.setPadding(new Insets(10, 20, 20, 20));
        bottomPane.setRight(btnGallery);

        bottomPane.setCenter(lblAlbumName);
        bottomPane.setLeft(btnRefresh);
        root.setBottom(bottomPane);
        scene = new Scene(root);
        //stage.setTitle("Social Media Photo Sync");
        //stage.setScene(scene);
        scene.getStylesheets().add(SelectWindow.class.getResource("style.css").toExternalForm());

        return scene;
    }

    @Override
    public Scene getScene() {
        if (this.scene == null) {
            try {
                this.createSelectWindowScene();
            } catch (SQLException | IOException ex) {
                Logger.getLogger(UserHomeWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.scene;
    }

    @Override
    public Scene getRefreshedScene() {
        Scene newScene = null;
        try {
            newScene = this.createSelectWindowScene();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(UserHomeWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newScene;
    }

}
