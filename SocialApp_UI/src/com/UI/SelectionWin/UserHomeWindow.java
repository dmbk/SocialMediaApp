/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.UI.SelectionWin;

import com.cse.Controller.FacebookEditor;
import com.cse.DBAccess.DatabaseExec;
import com.cse.OAuth.WebOAuth;
import com.cse.WindowControllers.UserHomeWindowController;
import com.cse.properties.PropertiesXmlFileHandler;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import java.io.File;
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
import javafx.scene.control.TextField;
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
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author dmbk
 */
public class UserHomeWindow implements WindowInterface {

    //private static String uId;
    private static final String USER_HOME_WINDOW = "uhw_scene";
    private boolean isChangable;//whether the user can change the albums being synced
    private List<ImageView> imageViewList;
    private Set<String> albumIdSet;
    private Set<String> syncedAlbumIdSet;
    private Map<String, ImageView> imgViewMap;
    //private static Facebook facebook;//should be FacebookEditor.getFacebook();

    private HBox lastHBoxRow;// assigned with lowest level HBox in the scroll pane
    private ImageView view;
    private HBox hbTempHolder;//temporary control nodes used to add image view process
    private Label lblAlbumName;
    private Scene scene;
    private String userId;
    private Facebook facebook;
    private Stage applicationStage;

    public UserHomeWindow(Stage stage, String userId, Facebook facebook) {
        this.userId = userId;
        this.facebook = facebook;
        this.applicationStage = stage;
    }

    /*@Override
     public void start(Stage stage) throws Exception {
     applicationStage = stage;
     scene = getRefreshedScene();
     applicationStage.setTitle("Social Media Photo Sync");
     applicationStage.getIcons().add(new Image("file:///" + PropertiesXmlFileHandler.readFromProperties("app.home") + "/res/icons/icon_main.png"));
     applicationStage.setScene(scene);
     stage.setResizable(false);
     applicationStage.show();

     }*/
    public Scene createHomeWindowScene() throws SQLException, MalformedURLException, IOException {

        isChangable = true;//whether the user can change the albums being synced
        imageViewList = new ArrayList<ImageView>();

        albumIdSet = new LinkedHashSet<>();
        syncedAlbumIdSet = new LinkedHashSet<>();
        imgViewMap = new LinkedHashMap<>();
        lblAlbumName = new Label();

        final BorderPane root;
        //final Scene scene;
        final ScrollPane sp;
        final VBox vb;
        HBox hb = null;
        // final HBox hBoxTop;
        final Label lblName;

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

        final Button btnOnlineAlbums;
        final Button btnCreateAlbums;
        final Button btnRemoveAlbums;// three buttons in the lower part
        ImageView node;
        sp = new ScrollPane();
        vb = new VBox(10);
        vb.setPadding(new Insets(10));
        vb.setSpacing(20);

        Image[] images = new Image[5];
        ImageView[] pics = new ImageView[5];
        //final String[] imageNames = new String[]{"fw1.jpg", "fw2.jpg",
        //  "fw3.jpg", "fw4.jpg", "fw5.jpg"};

        String sql_albums = "select album_id,cover_uri,album_name from albums where u_id=? and sync=?";
        String sql_temp_albums = "select album_id,cover_uri,album_name from temp_albums where u_id=?";
        CachedRowSet set_albums = DatabaseExec.executeQuery(sql_albums, userId, "1");
        CachedRowSet set_temp_albums = DatabaseExec.executeQuery(sql_temp_albums, userId);
        CachedRowSet set = null;
        boolean albumSwitch = true;

        columns:
        for (int j = 0; j < Integer.MAX_VALUE; j++) {
            hb = new HBox(40);

            for (int i = 0; i < 5; i++) {
                if (albumSwitch) {

                    if (set_albums.next()) {

                        set = set_albums;
                    } else {

                        albumSwitch = false;
                    }
                }
                if (!albumSwitch) {

                    if (set_temp_albums.next()) {

                        set = set_temp_albums;
                    } else {

                        if (i > 0) {
                            vb.getChildren().add(hb);
                            vb.setOpacity(1);
                            sp.setContent(vb);
                        }
                        break columns;
                    }
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
                pics[i].setSmooth(true);
                imgViewMap.put(albumId, pics[i]);
                //node.setPickOnBounds(false);
                final int index = 5 * j + i;//for efficiency purposes used instead imageViewList.size()
                imageViewList.add(index, pics[i]);

                pics[i].addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    int imageIndex = index;
                    boolean isSelected = false;
                    String name = albumName;
                    String alId = albumId;

                    @Override
                    public void handle(MouseEvent event) {
                        if (MouseButton.PRIMARY.equals(event.getButton())) {

                            if (!isChangable || !imgViewMap.containsKey(albumId)) {
                                return;
                            }
                            if (event.getClickCount() == 2) {
                                AlbumViewerWindow window = new AlbumViewerWindow(applicationStage, albumId);
                                Scene nextScene = window.getRefreshedScene();
                                applicationStage.setScene(nextScene);

                                return;
                            }
                            if (!isSelected) {

                                int depth = 100; //Setting the uniform variable for the glow width and height

                                DropShadow borderGlow = new DropShadow();
                                borderGlow.setOffsetY(0f);
                                borderGlow.setOffsetX(0f);
                                borderGlow.setColor(Color.WHITE);
                                borderGlow.setWidth(depth);
                                borderGlow.setHeight(depth);

                                imageViewList.get(index).setEffect(borderGlow);
                                if (!albumIdSet.contains(alId)) {
                                    albumIdSet.add(alId);
                                }
                                lblAlbumName.setText("The album name is " + name);
                                isSelected = true;
                            } else if (isSelected) {

                                DropShadow dropShadow = new DropShadow();
                                dropShadow.setRadius(5.0);
                                dropShadow.setOffsetX(6.0);
                                dropShadow.setOffsetY(6.0);
                                dropShadow.setColor(Color.BLACK);
                                imageViewList.get(index).setEffect(dropShadow);
                                if (albumIdSet.contains(alId)) {
                                    albumIdSet.remove(alId);
                                }

                                lblAlbumName.setText("The album name is " + name);
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
                dropShadow.setColor(Color.BLACK);
                pics[i].setEffect(dropShadow);

                hb.getChildren().add(pics[i]);
                lastHBoxRow = hb;

            }
            vb.getChildren().add(hb);
            vb.setOpacity(1);

        }
        sp.setContent(vb);

        root = new BorderPane();
        root.setPrefSize(940, 650);
        root.setMaxWidth(940);

        //sp.setOpacity(0.75);
////////The rest is about making the upper part of the UI///////////////////////////////////////
///////////////The upper left///////////////////////////////////
        chkBoxSelectAll = new CheckBox("Select all");
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
                            int depth = 100;
                            DropShadow borderGlow = new DropShadow();
                            borderGlow.setOffsetY(0f);
                            borderGlow.setOffsetX(0f);
                            borderGlow.setColor(Color.WHITE);
                            borderGlow.setWidth(depth);
                            borderGlow.setHeight(depth);

                            tView.setEffect(borderGlow);

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
                            dropShadow.setColor(Color.BLACK);
                            tView.setEffect(dropShadow);

                        }
                        albumIdSet = new LinkedHashSet<>();
                    }
                }
            }

        });

        chkBoxSelectAll.setAlignment(Pos.BOTTOM_LEFT);
        vBoxUpperLeft = new VBox(5);

        Text textWelcome = new Text();
        textWelcome.setText("Albums Gallery");
        textWelcome.setId("welcome-text");

        lblInstr = new Label("Double click to open photos");
        lblInstr.setFont(new Font("ROD", 14));
        vBoxUpperLeft.getChildren().addAll(textWelcome, lblInstr, chkBoxSelectAll);
        //vBoxUpperLeft.fillWidthProperty();

///////////////The center///////////////////////////////////
        vBoxTop = new VBox(20);
        lblSyncProg = new Label("Upload updates:");

        btnStartSync = new Button("Start album uploading");
        btnStartSync.setPrefHeight(30);
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
                                    try {
                                        updateMessage("Initializing process");
                                        int prog = 0;
                                        String SqlTempAlbums = "Select album_id,album_name from temp_albums;";
                                        CachedRowSet setTempAlbums = DatabaseExec.executeQuery(SqlTempAlbums);
                                        updateMessage("Creating new albums");
                                        List<String> newAlbumIdList = new ArrayList<>();
                                        List<String> newAlbumNameList = new ArrayList<>();
                                        while (setTempAlbums.next()) {

                                            String tempAlbumId = setTempAlbums.getString("album_id");
                                            String tempAlbumName = setTempAlbums.getString("album_name");
                                            String newAlbumId = UserHomeWindowController.createAllNewAlbums(userId, tempAlbumId, tempAlbumName, facebook);
                                            newAlbumIdList.add(prog, newAlbumId);
                                            newAlbumNameList.add(prog, tempAlbumName);
                                            prog++;
                                            updateProgress(prog, setTempAlbums.size());
                                        }
                                        updateMessage("Uploading new photos");
                                        String sql = "select album_id from albums where sync=?;";
                                        CachedRowSet set = DatabaseExec.executeQuery(sql, "1");
                                        prog = 0;
                                        while (set.next()) {
                                            String albumId = set.getString("album_id");
                                            UserHomeWindowController.uploadAllPhotoAdditions(userId, albumId, facebook);
                                            prog++;
                                            updateProgress(prog, set.size());
                                        }

                                        updateMessage("Downloading cover photos");
                                        FacebookEditor editor = new FacebookEditor(facebook, userId);
                                        if (newAlbumIdList.size() == newAlbumNameList.size()) {
                                            for (int k = 0; k < newAlbumIdList.size(); k++) {
                                                editor.getAlbumCovers(newAlbumIdList.get(k), newAlbumNameList.get(k), facebook);
                                                updateProgress(k, newAlbumIdList.size());
                                            }

                                        }

                                        isChangable = true;
                                        UserHomeWindowController.deleteFolder(new File(PropertiesXmlFileHandler.readFromProperties("user.folder") + "/" + "temp_albums"));

                                        updateMessage("Uploading complete.All albums synchronized.");
                                        Thread.sleep(5000);

                                    } catch (SQLException ex) {
                                        Logger.getLogger(UserHomeWindow.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (FacebookException ex) {
                                        Dialogs.create()
                                                .owner(applicationStage)
                                                .title("Connection error")
                                                .masthead("Connection timeout has occured.")
                                                .message("Please check your connection status!")
                                                .actions(Dialog.Actions.OK)
                                                .showWarning();
                                    } catch (IOException ex) {
                                        Logger.getLogger(UserHomeWindow.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    return null;
                                }
                            };
                        }
                    };

                    Dialogs.create()
                            .owner(applicationStage)
                            .title("Updating online albums")
                            .masthead("Updating all album(s)")
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

        profPicView = new ImageView(new Image("file:///" + PropertiesXmlFileHandler.readFromProperties("user.folder") + "/albums/" + userId + "/prof_pic"));

        int depth = 40; //Setting the uniform variable for the glow width and height

        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.BLACK);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);

        profPicView.setEffect(borderGlow);

        btnSignOut = new Button("Sign Out");

        btnSignOut.setAlignment(Pos.CENTER);
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
        profPicView.setFitWidth(55);
        profPicView.setFitHeight(55);

        btnSignOut.minWidth(75);
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
        BorderPane bottomPane = new BorderPane();
        bottomPane.setPadding(new Insets(10, 20, 20, 20));
        bottomPane.setPrefHeight(100);
        btnOnlineAlbums = new Button("View online albums");
        btnOnlineAlbums.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (t.getButton() == MouseButton.PRIMARY) {
                    Scene prevScene = ApplicationSceneHandler.getSelectWindow().getScene();
                    applicationStage.setScene(prevScene);
                }
            }
        });
        btnOnlineAlbums.setPrefHeight(30);

        bottomPane.setLeft(btnOnlineAlbums);

        //////////
        HBox hbBottomCenter = new HBox(10);

        final TextField txtNewAlbumName = new TextField("Enter new album name");
        txtNewAlbumName.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                txtNewAlbumName.setText("");
            }
        });
        txtNewAlbumName.setPadding(new Insets(10, 10, 10, 10));

        btnCreateAlbums = new Button("Create Album");

        btnCreateAlbums.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (t.getButton().equals(MouseButton.PRIMARY)) {
                    String cover_uri_temp = "file:///" + PropertiesXmlFileHandler.readFromProperties("app.home") + "/res/empty_cover/default_cover.jpg";
                    view = new ImageView(new Image(cover_uri_temp));
                    if (lastHBoxRow == null) {
                        lastHBoxRow = new HBox(40);
                        vb.getChildren().add(lastHBoxRow);
                    }
                    final int rowSize = lastHBoxRow.getChildren().size();
                    if (rowSize < 5) {
                        hbTempHolder = lastHBoxRow;
                    } else {
                        hbTempHolder = new HBox(40);
                        vb.getChildren().add(hbTempHolder);
                        lastHBoxRow = hbTempHolder;
                    }

                    Task<Integer> task = new Task<Integer>() {

                        @Override
                        protected Integer call() throws Exception {

                            UserHomeWindowController.setU_id(userId);
                            String name = txtNewAlbumName.getText().trim();

                            if ("".equals(name) || name.startsWith("Enter new album name")) {
                                name = "Untitled album";
                            }
                            final String albumName = name;
                            final String newAlbumId = UserHomeWindowController.addNewAlbum(albumName);

                            final int index = imageViewList.size();
                            imgViewMap.put(newAlbumId, view);
                            imageViewList.add(index, view);

                            view.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                int imageIndex = index;
                                boolean isSelected = false;
                                private String albumId = newAlbumId;

                                @Override
                                public void handle(MouseEvent event) {
                                    if (MouseButton.PRIMARY.equals(event.getButton())) {

                                        if (!isChangable || !imgViewMap.containsKey(albumId)) {
                                            return;
                                        }
                                        if (event.getClickCount() == 2) {
                                            AlbumViewerWindow window = new AlbumViewerWindow(applicationStage, albumId);
                                            Scene nextScene = window.getRefreshedScene();
                                            applicationStage.setScene(nextScene);

                                            return;
                                        }
                                        if (!isSelected) {
                                            int depth = 100; //Setting the uniform variable for the glow width and height
                                            DropShadow borderGlow = new DropShadow();
                                            borderGlow.setOffsetY(0f);
                                            borderGlow.setOffsetX(0f);
                                            borderGlow.setColor(Color.WHITE);
                                            borderGlow.setWidth(depth);
                                            borderGlow.setHeight(depth);

                                            imageViewList.get(imageIndex).setEffect(borderGlow);

                                            if (!albumIdSet.contains(albumId)) {
                                                albumIdSet.add(albumId);
                                            }
                                            lblAlbumName.setText("The album name is " + albumName);
                                            isSelected = true;
                                        } else if (isSelected) {

                                            DropShadow dropShadow = new DropShadow();
                                            dropShadow.setRadius(5.0);
                                            dropShadow.setOffsetX(6.0);
                                            dropShadow.setOffsetY(6.0);
                                            dropShadow.setColor(Color.BLACK);
                                            imageViewList.get(imageIndex).setEffect(dropShadow);
                                            if (albumIdSet.contains(albumId)) {
                                                albumIdSet.remove(albumId);
                                            }

                                            lblAlbumName.setText("The album name is " + albumName);
                                            isSelected = false;
                                        }

                                    }

                                    event.consume();
                                }
                            });

                            return 0;
                        }

                    };
                    view.setFitHeight(120);
                    view.setFitWidth(150);

                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setRadius(5.0);
                    dropShadow.setOffsetX(6.0);
                    dropShadow.setOffsetY(6.0);
                    dropShadow.setColor(Color.BLACK);

                    view.setEffect(dropShadow);

                    hbTempHolder.getChildren().add(view);
                    Thread t1 = new Thread(task);
                    t1.setDaemon(true);
                    t1.start();

                    t.consume();

                }

            }

        });

        btnCreateAlbums.setPrefHeight(30);

        //bottomPane.setPadding(new Insets(10, 10, 20, 20));
        hbBottomCenter.getChildren().addAll(txtNewAlbumName, btnCreateAlbums);
        hbBottomCenter.setAlignment(Pos.CENTER);
        bottomPane.setCenter(hbBottomCenter);
        //////////
        final Button btnRefresh = new Button("Refresh Albums");

        btnRefresh.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (MouseButton.PRIMARY.equals(event.getButton())) {
                    Scene newScene = ApplicationSceneHandler.getUserHomeWindow().getRefreshedScene();
                    applicationStage.setScene(newScene);

                }

            }

        });

        btnRemoveAlbums = new Button("Remove Album(s)");
        btnRemoveAlbums.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (t.getButton() == MouseButton.PRIMARY) {

                    Task<Integer> task = new Task<Integer>() {

                        @Override
                        protected Integer call() throws Exception {
                            UserHomeWindowController.removeExistingAlbum(albumIdSet);

                            return 0;
                        }
                    };

                    Action response = Dialogs.create()
                            .owner(applicationStage)
                            .title("Deleting albums")
                            .masthead("You are going to delete " + albumIdSet.size() + " album(s)")
                            .message("Are you ok with this?")
                            .actions(Dialog.Actions.OK, Dialog.Actions.CANCEL)
                            .showConfirm();

                    if (response == Dialog.Actions.OK) {
                        Iterator itr = albumIdSet.iterator();
                        while (itr.hasNext()) {
                            String id = (String) itr.next();
                            ImageView img = imgViewMap.get(id);
                            if (img != null) {
                                img.setImage(new Image("file:///" + PropertiesXmlFileHandler.readFromProperties("app.home") + "/res/empty_cover/removed.jpg"));
                            }
                            imgViewMap.remove(id);
                        }
                        Thread t1 = new Thread(task);
                        t1.setDaemon(true);
                        t1.start();

                    }
                }
            }
        });
        btnRemoveAlbums.setPrefHeight(30);

        VBox vbRightBottomCorner = new VBox(20);
        vbRightBottomCorner.getChildren().addAll(btnRemoveAlbums, btnRefresh);
        vbRightBottomCorner.setPadding(new Insets(10));
        bottomPane.setRight(vbRightBottomCorner);

        bottomPane.setBottom(lblAlbumName);
        //bottomPane.setPadding(new Insets(10, 10, 20, 20));
        root.setBottom(bottomPane);
        scene = new Scene(root);
        //stage.setTitle("Social Media Photo Sync");
        //stage.setScene(scene);
        scene.getStylesheets().add(SelectWindow.class.getResource("style.css").toExternalForm());
        startDownloadInBackground();
        return scene;
    }

    /**
     * *
     * launches a thread that checks in the background for all the new updates
     * in the set of albums that have sync variable=1
     *
     *
     *
     */
    public void startDownloadInBackground() {

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                while (applicationStage.isShowing()) {
                    UserHomeWindowController.syncWithOnlinePhotos(userId, facebook);
                    Thread.sleep(5 * 60000);
                }

                return null;

            }

        };
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();

    }

    @Override
    public Scene getScene() {
        if (this.scene == null) {
            try {
                this.createHomeWindowScene();
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
            newScene = this.createHomeWindowScene();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(UserHomeWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newScene;
    }

}
