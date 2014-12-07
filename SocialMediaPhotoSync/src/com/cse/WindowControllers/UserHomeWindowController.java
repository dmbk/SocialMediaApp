/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.WindowControllers;

import com.cse.Controller.FacebookEditor;
import com.cse.DBAccess.DatabaseExec;
import com.cse.properties.PropertiesXmlFileHandler;
import facebook4j.Album;
import facebook4j.AlbumUpdate;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Media;
import facebook4j.Photo;
import facebook4j.PrivacyBuilder;
import facebook4j.PrivacyParameter;
import facebook4j.PrivacyType;
import facebook4j.ResponseList;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author dmbk
 */
//E:\facebook test albums\albums\690053834396886
public class UserHomeWindowController {

    private static String u_id;

    public static void setU_id(String u_id) {
        UserHomeWindowController.u_id = u_id;
    }

    public static final String NEW_ALBUM_ID_PREFIX = "new_album";

    /**
     * *
     * adds a new album to the temp_albums table with the given album name.
     *
     * @param albumName -name of the new album to be added
     */
    public static String addNewAlbum(String albumName) {

        if (u_id == null) {
            return null;
        }
        String newAlbumId = generateAlbumId();
        String sql = "INSERT INTO temp_albums (album_id,album_name,cover_uri,album_uri,u_id) "
                + "VALUES (?,?,?,?,?);";

        String albumUri = PropertiesXmlFileHandler.readFromProperties("user.folder") + "/temp_albums/" + u_id + "/" + newAlbumId;
        DatabaseExec.execNonQuery(sql, newAlbumId, albumName, null, albumUri, u_id);
        return newAlbumId;
    }

    /**
     * *
     * returns a new album id generated using the current system time
     *
     * @return the generated new album id
     *
     */
    public static String generateAlbumId() {

        Long time = System.currentTimeMillis();
        return NEW_ALBUM_ID_PREFIX + time;
    }

    /**
     * *
     * removes all the albums given with the set
     *
     * @param albumIdSet -album ids to be removed from the database
     *
     */
    public static void removeExistingAlbum(Set albumIdSet) {
        if (albumIdSet == null) {
            return;
        }

        String sqlAlbums = "Delete from albums where album_id=?";
        String sqlTempAlbums = "Delete from temp_albums where album_id=?";
        String sqlPhotos = "Delete from photos where album_id=?";
        String sqlTempPhotos = "Delete from temp_photos where album_id=?";
        String sqlA = null;
        String sqlP = null;
        Iterator itr = albumIdSet.iterator();
        while (itr.hasNext()) {
            String tempId = (String) itr.next();
            if (tempId.startsWith(NEW_ALBUM_ID_PREFIX)) {
                sqlA = sqlTempAlbums;
                sqlP = sqlTempPhotos;
            } else {
                sqlA = sqlAlbums;
                sqlP = sqlPhotos;
            }

            DatabaseExec.execNonQuery(sqlA, tempId);
            DatabaseExec.execNonQuery(sqlP, tempId);
            String pathToAlbum;
            if (tempId.startsWith(NEW_ALBUM_ID_PREFIX)) {
                pathToAlbum = PropertiesXmlFileHandler.readFromProperties("user.folder") + "/temp_albums/" + u_id + "/" + tempId;
            } else {
                pathToAlbum = PropertiesXmlFileHandler.readFromProperties("user.folder") + "/albums/" + u_id + "/" + tempId;

            }
            deleteFolder(new File(pathToAlbum));

        }

    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void removeAlbum(String tempId) {
        if (tempId == null) {
            return;
        }

        String sqlAlbums = "Delete from albums where album_id=?";
        String sqlTempAlbums = "Delete from temp_albums where album_id=?";
        String sqlPhotos = "Delete from photos where album_id=?";
        String sqlTempPhotos = "Delete from temp_photos where album_id=?";
        String sqlA = null;
        String sqlP = null;

        if (tempId.startsWith(NEW_ALBUM_ID_PREFIX)) {
            sqlA = sqlTempAlbums;
            sqlP = sqlTempPhotos;
        } else {
            sqlA = sqlAlbums;
            sqlP = sqlPhotos;
        }

        DatabaseExec.execNonQuery(sqlA, tempId);
        DatabaseExec.execNonQuery(sqlP, tempId);
        String pathToAlbum;
        if (tempId.startsWith(NEW_ALBUM_ID_PREFIX)) {
            pathToAlbum = PropertiesXmlFileHandler.readFromProperties("user.folder") + "/temp_albums/" + u_id + "/" + tempId;
        } else {
            pathToAlbum = PropertiesXmlFileHandler.readFromProperties("user.folder") + "/albums/" + u_id + "/" + tempId;

        }
        deleteFolder(new File(pathToAlbum));

    }

    public static void syncWithOnlinePhotos(String u_id, Facebook facebook) {

        try {

            String sql = "select album_id from albums where u_id=? and sync=?;";
            CachedRowSet set = DatabaseExec.executeQuery(sql, u_id, "1");

            while (set.next()) {
                String albumId = set.getString("album_id");
                ResponseList<Album> albums = facebook.getAlbums();
                Set<String> albumSetTemp = new LinkedHashSet<>();
                int sizeAl = albums.size();
                for (int i = 0; i < sizeAl; i++) {

                    albumSetTemp.add(albums.get(i).getId());
                }

                if (!albumSetTemp.contains(albumId)) {
                    removeAlbum(albumId);

                } else {
                    ResponseList<Photo> photos = facebook.getAlbumPhotos(albumId);
                    Map<String, Photo> photoMap = new LinkedHashMap<String, Photo>();
                    int size = photos.size();
                    for (int i = 0; i < size; i++) {
                        Photo pic = photos.get(i);
                        photoMap.put(pic.getId(), pic);
                    }

                    String sqlPics = "select photo_id,photo_uri from photos where album_id=?;";
                    CachedRowSet setPics = DatabaseExec.executeQuery(sqlPics, albumId);

                    while (setPics.next()) {
                        if (photoMap.containsKey(setPics.getString("photo_id"))) {
                            photoMap.remove(setPics.getString("photo_id"));
                        } else {
                            deleteFolder(new File(setPics.getString("photo_uri")));
                            String sqlRemove = "Delete from photos where photo_id=?;";
                            DatabaseExec.execNonQuery(sqlRemove, setPics.getString("photo_id"));
                        }

                    }
                    photos.retainAll(photoMap.values());

                    SelectWindowController.setUserId(u_id);
                    SelectWindowController.setFacebook(facebook);
                    SelectWindowController.downloadAlbumPhotos(photos, albumId);
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHomeWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FacebookException ex) {

        }

    }

    public static String createAllNewAlbums(String userId, String albumId, String albumName, Facebook facebook) throws FacebookException {

        if (!albumId.startsWith(NEW_ALBUM_ID_PREFIX)) {
            return null;

        }
        PrivacyParameter pv = new PrivacyBuilder().setValue(PrivacyType.SELF).build();
        String newAlbumId = facebook.createAlbum(userId, new AlbumUpdate(albumName, pv));
        String sqlInsert = "INSERT INTO albums (album_id,album_name,cover_uri,album_uri,sync,u_id) VALUES( ?,  ?,  ?,  ?,  ?,  ?);";
        String albumUri = PropertiesXmlFileHandler.readFromProperties("user.folder") + "/" + "albums" + "/" + userId + "/" + newAlbumId;
        String coverUri = PropertiesXmlFileHandler.readFromProperties("user.folder") + "/" + "albums" + "/" + userId + "/" + newAlbumId + "/" + "cover_" + albumName;

        DatabaseExec.execNonQuery(sqlInsert, newAlbumId, albumName, coverUri, albumUri, "1", userId);
        String sqlUpdate = "UPDATE temp_photos SET album_id=? WHERE album_id= ?;";
        DatabaseExec.execNonQuery(sqlUpdate, newAlbumId, albumId);
        String sqlRemove = "Delete from temp_albums where album_id=?;";
        DatabaseExec.execNonQuery(sqlRemove, albumId);
        return newAlbumId;
    }

    public static void uploadAllPhotoAdditions(String userId, String albumId, Facebook facebook) throws IOException {
        try {

            String sqlP = "select photo_id,photo_uri from temp_photos where album_id=?;";
            CachedRowSet setP = DatabaseExec.executeQuery(sqlP, albumId);

            while (setP.next()) {
                String sourcePath = setP.getString("photo_uri");
                String oldPId = setP.getString("photo_id");
                String newPId = facebook.addAlbumPhoto(albumId, new Media(new File(sourcePath)));
                String destPath = PropertiesXmlFileHandler.readFromProperties("user.folder") + "/" + "albums" + "/" + userId + "/" + albumId + "/" + newPId;

                String sqlPhotos = "Insert into photos (photo_id,photo_uri,album_id) Values(?,?,?);";
                DatabaseExec.execNonQuery(sqlPhotos, newPId, destPath, albumId);

                String sqlTempPhotos = "Delete from temp_photos where photo_id=?";
                DatabaseExec.execNonQuery(sqlTempPhotos, oldPId);

                AlbumViewerWindowController.copyFile(new File(sourcePath), new File(destPath));
                deleteFolder(new File(sourcePath));

            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHomeWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FacebookException ex) {
            Logger.getLogger(UserHomeWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
