/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.WindowControllers;

import com.cse.DBAccess.DatabaseExec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author dmbk
 */
public class AlbumViewerWindowController {

    public static final String NEW_PHOTO_ID_PREFIX = "new_photo";

    public static String generateAlbumId() {

        Long time = System.currentTimeMillis();
        return NEW_PHOTO_ID_PREFIX + time;
    }

    public static String addAndMoveNewPhoto(String albumId, String photoUri) {
        try {
            String photoId = generateAlbumId();
            String sqlTemp = null;
            if (albumId.startsWith(UserHomeWindowController.NEW_ALBUM_ID_PREFIX)) {
                sqlTemp = "select album_uri from temp_albums where album_id=?";
            } else {
                sqlTemp = "select album_uri from albums where album_id=?";
            }
            CachedRowSet set = DatabaseExec.executeQuery(sqlTemp, albumId);
            String albumUri = null;

            if (set.next()) {
                albumUri = set.getString("album_uri");
            }
            if (albumUri == null) {

                return null;
            }
            String destinationPlace = albumUri + "/" + photoId;

            File sourceFile = new File(photoUri);
            File destFile = new File(destinationPlace);

            copyFile(sourceFile, destFile);

            String sql = "INSERT INTO temp_photos (photo_id,photo_uri,album_id) "
                    + "VALUES (?,?,?);";
            DatabaseExec.execNonQuery(sql, photoId, destinationPlace, albumId);

            return photoId;
        } catch (SQLException | MalformedURLException ex) {
            Logger.getLogger(AlbumViewerWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AlbumViewerWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            Path pathToFile = Paths.get(destFile.getAbsolutePath());
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
            // destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}
