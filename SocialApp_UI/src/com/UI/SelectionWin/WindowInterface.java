/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.UI.SelectionWin;

import javafx.scene.Scene;

/**
 *
 * @author dmbk
 */
public interface WindowInterface {

    public static final String NEW_ALBUM_ID_PREFIX = "new_album";
    public static final String NEW_PHOTO_ID_PREFIX = "new_photo";

    public Scene getScene();
    
    public Scene getRefreshedScene();
}
