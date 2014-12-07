/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.UI.SelectionWin;

import com.cse.DBAccess.DatabaseExec;
import com.cse.WindowControllers.AlbumViewerWindowController;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.sql.rowset.CachedRowSet;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialog.Actions;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author dmbk
 */
public class AlbumViewerWindow implements WindowInterface {

    private Scene scene;
    private static final int BORDER_DEPTH = 50;
    private ImageView largeImgView = new ImageView();

    private Stage appliacationStage;
    private final Map<String, ImageView> imgViewMap = new LinkedHashMap<String, ImageView>();
    private String albumId;
    private ImageView currentImageView;

    public AlbumViewerWindow(Stage appStage, String albumId) {
        this.albumId = albumId;
        this.appliacationStage = appStage;
    }

    //needs the album id
    @Override
    public Scene getScene() {
        if (this.scene == null) {
            try {
                this.createAlbumViewerWindowScene();
            } catch (SQLException ex) {
                Logger.getLogger(AlbumViewerWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.scene;
    }

    @Override
    public Scene getRefreshedScene() {
        Scene newScene = null;
        try {
            newScene = this.createAlbumViewerWindowScene();
        } catch (SQLException ex) {
            Logger.getLogger(AlbumViewerWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newScene;
    }

    public static void main(String[] args) {
        Application.launch(args);

    }

    public Scene createAlbumViewerWindowScene() throws SQLException {

        final BorderPane root = new BorderPane();
        final ScrollPane spBottom = new ScrollPane();
        spBottom.setPrefHeight(115);
        final ScrollPane spCenter = new ScrollPane(largeImgView);

        final HBox hbTop = new HBox(50);
        final HBox hbBottom = new HBox(10);
        Label lblInstr = new Label("Drag and drop your photos");
        Button btnSaveToLocation = new Button("Save to a lacation");
        Button btnBackToHome = new Button("Back to home");

        btnBackToHome.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    Scene prevScene = ApplicationSceneHandler.getUserHomeWindow().getScene();
                    appliacationStage.setScene(prevScene);
                }
            }

        });

        btnSaveToLocation.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Image");

                    File file = fileChooser.showSaveDialog(appliacationStage);
                    if (file != null) {
                        try {
                            ImageIO.write(SwingFXUtils.fromFXImage(largeImgView.getImage(),
                                    null), "png", file);
                        } catch (IOException ex) {

                        }
                    }
                }
            }

        });
        btnBackToHome.setPadding(new Insets(10));
        btnSaveToLocation.setPadding(new Insets(10));
        lblInstr.setPadding(new Insets(5));
        hbTop.setPadding(new Insets(10));

        hbTop.getChildren().addAll(btnBackToHome, btnSaveToLocation, lblInstr);
        root.setPrefSize(1100, 695);
        root.setMaxWidth(1100);
//////////////////////////////////////loading the photo list///////////////////////////////////////

        String sql = "Select photo_id,photo_uri from photos where album_id=?";
        boolean isSet = false;
        int i = 0;
        while (i < 2) {

            CachedRowSet set = DatabaseExec.executeQuery(sql, albumId);
            while (set.next()) {

                final String photoId = set.getString("photo_id");
                final String photoUri = set.getString("photo_uri");
                ImageView tempView = new ImageView();
                if (set.getRow() == 1 && !isSet) {
                    Image imgInit = new Image("file:///" + photoUri);
                    largeImgView.setImage(imgInit);
                    double ratio = imgInit.getWidth() / imgInit.getHeight();
                    double offset = (1100 - 500 * ratio) / 2;
                    if (offset > 0) {
                        spCenter.setPadding(new Insets(5, offset, 5, offset));
                        largeImgView.setFitHeight(500);
                        largeImgView.setPreserveRatio(true);
                    } else {
                        offset = 5.0;
                        spCenter.setPadding(new Insets(5, offset, 5, offset));
                        largeImgView.setFitHeight(500);
                        largeImgView.setFitWidth(1100 - 10);
                    }
                    DropShadow borderGlow = new DropShadow();
                    borderGlow.setOffsetY(0f);
                    borderGlow.setOffsetX(0f);
                    borderGlow.setColor(Color.BLACK);
                    borderGlow.setWidth(BORDER_DEPTH);
                    borderGlow.setHeight(BORDER_DEPTH);

                    tempView.setEffect(borderGlow);
                    currentImageView = tempView;
                    isSet = true;

                }

                final ImageView finalViewOriginal = tempView;
                tempView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    String p_id = photoId;
                    String p_uri = photoUri;
                    ImageView img = finalViewOriginal;

                    @Override
                    public void handle(MouseEvent event) {
                        DropShadow borderGlow1 = new DropShadow();
                        borderGlow1.setOffsetY(0f);
                        borderGlow1.setOffsetX(0f);
                        borderGlow1.setColor(new Color(0, 0, 0, 0));
                        borderGlow1.setWidth(5);
                        borderGlow1.setHeight(5);
                        currentImageView.setEffect(borderGlow1);
                        if (MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 1) {

                            Image imgInit = new Image("file:///" + p_uri);
                            largeImgView.setImage(imgInit);
                            double ratio = imgInit.getWidth() / imgInit.getHeight();
                            double offset = (1100 - 500 * ratio) / 2;
                            if (offset > 0) {
                                spCenter.setPadding(new Insets(5, offset, 5, offset));
                                largeImgView.setFitHeight(500);
                                largeImgView.setPreserveRatio(true);
                            } else {
                                offset = 5.0;
                                spCenter.setPadding(new Insets(5, offset, 5, offset));
                                largeImgView.setFitHeight(500);
                                largeImgView.setFitWidth(1100 - 10);
                            }

                            DropShadow borderGlow = new DropShadow();
                            borderGlow.setOffsetY(0f);
                            borderGlow.setOffsetX(0f);
                            borderGlow.setColor(Color.BLACK);
                            borderGlow.setWidth(BORDER_DEPTH);
                            borderGlow.setHeight(BORDER_DEPTH);

                            img.setEffect(borderGlow);
                            currentImageView = img;
                        }

                    }

                });

                tempView.setImage(new Image("file:///" + photoUri));
                tempView.setFitHeight(90);
                tempView.setFitWidth(100);
                hbBottom.getChildren().add(tempView);
                imgViewMap.put(photoId, tempView);

            }
            sql = "Select photo_id,photo_uri from temp_photos where album_id=?";
            i++;
        }
        spBottom.setContent(hbBottom);
        spBottom.setPadding(new Insets(5, 10, 5, 10));
//////////////////

        root.setTop(hbTop);

        root.setCenter(spCenter);
        root.setBottom(spBottom);

        scene = new Scene(root);

////////////////////////////writing the drag and drop events for the scene/////////////////////////
        scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });

        // Dropping over surface
        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {

                Dragboard db = event.getDragboard();

                boolean success = false;
                if (db.hasFiles()) {

                    success = true;
                    String filePath = null;
                    int k = 0;
                    for (File file : db.getFiles()) {

                        filePath = file.getAbsolutePath();

                        ImageView imgTemp = new ImageView();
                        final String photoId = AlbumViewerWindowController.addAndMoveNewPhoto(albumId, filePath);
                        if (photoId == null) {

                            return;
                        }
                        String sql = "Select photo_uri from temp_photos where photo_id=?";
                        CachedRowSet set = DatabaseExec.executeQuery(sql, photoId);
                        String photoUri = null;
                        try {
                            if (set.next()) {

                                imgTemp.setImage(new Image("file:///" + set.getString("photo_uri")));
                                imgTemp.setFitHeight(90);
                                imgTemp.setFitWidth(100);

                                photoUri = set.getString("photo_uri");
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(AlbumViewerWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (photoUri != null) {
                            final String finalPUri = photoUri;
                            final ImageView tempFinalview = imgTemp;
                            imgViewMap.put(photoId, imgTemp);
                            imgTemp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                String p_id = photoId;
                                String p_uri = finalPUri;
                                ImageView img = tempFinalview;

                                @Override
                                public void handle(MouseEvent event) {
                                    DropShadow borderGlow1 = new DropShadow();
                                    borderGlow1.setOffsetY(0f);
                                    borderGlow1.setOffsetX(0f);
                                    borderGlow1.setColor(new Color(0, 0, 0, 0));
                                    borderGlow1.setWidth(BORDER_DEPTH);
                                    borderGlow1.setHeight(BORDER_DEPTH);
                                    currentImageView.setEffect(borderGlow1);
                                    if (MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 1) {
                                        Image imgInit = new Image("file:///" + p_uri);
                                        largeImgView.setImage(imgInit);
                                        double ratio = imgInit.getWidth() / imgInit.getHeight();
                                        double offset = (1100 - 500 * ratio) / 2;
                                        if (offset > 0) {
                                            spCenter.setPadding(new Insets(5, offset, 5, offset));
                                            largeImgView.setFitHeight(500);
                                            largeImgView.setPreserveRatio(true);
                                        } else {
                                            offset = 5.0;
                                            spCenter.setPadding(new Insets(5, offset, 5, offset));
                                            largeImgView.setFitHeight(500);
                                            largeImgView.setFitWidth(1100 - 10);
                                        }

                                        DropShadow borderGlow = new DropShadow();
                                        borderGlow.setOffsetY(0f);
                                        borderGlow.setOffsetX(0f);
                                        borderGlow.setColor(Color.BLACK);
                                        borderGlow.setWidth(BORDER_DEPTH);
                                        borderGlow.setHeight(BORDER_DEPTH);

                                        img.setEffect(borderGlow);
                                        currentImageView = img;

                                    }

                                }

                            });

                        }

                        hbBottom.getChildren().add(imgTemp);
                        if (currentImageView == null && k == 0) {
                            Image imgInit = imgTemp.getImage();
                            largeImgView.setImage(imgTemp.getImage());
                            double ratio = imgInit.getWidth() / imgInit.getHeight();
                            double offset = (1100 - 500 * ratio) / 2;
                            if (offset > 0) {
                                spCenter.setPadding(new Insets(5, offset, 5, offset));
                                largeImgView.setFitHeight(500);
                                largeImgView.setPreserveRatio(true);
                            } else {
                                offset = 5.0;
                                spCenter.setPadding(new Insets(5, offset, 5, offset));
                                largeImgView.setFitHeight(500);
                                largeImgView.setFitWidth(1100 - 10);
                            }

                            DropShadow borderGlow = new DropShadow();
                            borderGlow.setOffsetY(0f);
                            borderGlow.setOffsetX(0f);
                            borderGlow.setColor(Color.BLACK);
                            borderGlow.setWidth(BORDER_DEPTH);
                            borderGlow.setHeight(BORDER_DEPTH);

                            imgTemp.setEffect(borderGlow);
                            currentImageView = imgTemp;
                        }
                        k++;
                    }
                }
                event.setDropCompleted(success);

                event.consume();
            }

        });

/////////////////////////////////////////Context menu/////////////////////////////////////////////////////////   
        final ContextMenu cm = new ContextMenu();
        cm.setOpacity(0.75);

        MenuItem cmItem1 = new MenuItem("Copy Image");
        cmItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putImage(largeImgView.getImage());
                clipboard.setContent(content);
            }
        });

        MenuItem cmItem2 = new MenuItem("Save");
        cmItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Image");

                File file = fileChooser.showSaveDialog(appliacationStage);
                if (file != null) {
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(largeImgView.getImage(),
                                null), "png", file);
                    } catch (IOException ex) {

                    }
                }
            }
        }
        );
        MenuItem cmItem0 = new MenuItem("Remove");
        cmItem0.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                Set<String> keySet = imgViewMap.keySet();

                Iterator itr = keySet.iterator();
                String keyToUse = null;
                while (itr.hasNext()) {
                    String keyTemp = (String) itr.next();
                    if (currentImageView.equals(imgViewMap.get(keyTemp))) {
                        if (!keyTemp.startsWith(NEW_PHOTO_ID_PREFIX)) {
                            Action showWarning = Dialogs.create()
                                    .owner(appliacationStage)
                                    .title("Sorry!")
                                    .masthead("Unable to delete.")
                                    .message("You can only remove newly added photos.")
                                    .actions(Dialog.Actions.OK)
                                    .showError();
                            return;
                        }
                        keyToUse = keyTemp;

                    }

                }
                Action showWarning = Dialogs.create()
                        .owner(appliacationStage)
                        .title("Warning")
                        .masthead("You are about to delete a picture.")
                        .message("Are you sure you want to delete this?")
                        .actions(Dialog.Actions.OK, Dialog.Actions.CANCEL)
                        .showWarning();

                if (keyToUse != null && showWarning == Actions.OK) {
                    hbBottom.getChildren().remove(currentImageView);
                    String sql = "Delete from temp_photos where photo_id=?";
                    int rowsNum = DatabaseExec.execNonQuery(sql, keyToUse);
                    appliacationStage.setScene(new AlbumViewerWindow(appliacationStage, albumId).getRefreshedScene());
                }
            }
        }
        );
        cm.getItems().add(cmItem0);
        cm.getItems().add(cmItem1);
        cm.getItems().add(cmItem2);

        largeImgView.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (e.getButton() == MouseButton.PRIMARY) {
                            cm.hide();
                        }
                        if (e.getButton() == MouseButton.SECONDARY) {
                            cm.show(largeImgView, e.getScreenX(), e.getScreenY());
                        }
                    }
                });

//////////////////////////////////////////////////////////////////////////////////////////////////////////////        
        scene.getStylesheets().add(SelectWindow.class.getResource("style.css").toExternalForm());
        return scene;

    }
}
