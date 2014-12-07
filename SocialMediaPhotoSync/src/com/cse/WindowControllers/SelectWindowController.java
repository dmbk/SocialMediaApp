/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.WindowControllers;

import com.cse.Controller.FacebookEditor;
import com.cse.DBAccess.DatabaseExec;
import com.cse.properties.PropertiesXmlFileHandler;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Photo;
import facebook4j.ResponseList;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelectWindowController {

    private static Facebook facebook;
    private static String userId;

    public static void setFacebook(Facebook facebook) {
        SelectWindowController.facebook = facebook;
    }

    public static void setUserId(String userId) {
        SelectWindowController.userId = userId;
    }

    /**
     * *
     * @param albumIds -list of albums that needs to be synced
     * @param uId -user Id gets the list of relevant photo ids and call methods
     * to save all the photos into the relevant folders corresponding to these
     * album ids
     *
     */
    public static void synchronizePhotos(String uId, Set<String> albumIds) {
        Facebook facebookObj = FacebookEditor.getFacebook();
        facebook = facebookObj;
        userId = uId;
        if (albumIds == null) {
            return;
        }
        Iterator itr = albumIds.iterator();
        while (itr.hasNext()) {
            String album_id = (String) itr.next();
            try {
                ResponseList<Photo> photos = facebook.getAlbumPhotos(album_id);
                downloadAlbumPhotos(photos, album_id);
            } catch (FacebookException ex) {
                Logger.getLogger(SelectWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * *
     * @param albumId -album id being synced
     * @param photoList -Photo list of the particular album
     *
     * takes photo by photo in the album and calls method to download and store
     * @throws facebook4j.FacebookException
     *
     *
     */
    public static void downloadAlbumPhotos(ResponseList<Photo> photoList, String albumId) throws FacebookException {

        Iterator itr = photoList.iterator();
        while (itr.hasNext()) {
            Photo photo = (Photo) itr.next();

            URL photoURL = facebook.getPhotoURL(photo.getId());

            String folderRelativePath = "/albums/" + userId + "/" + albumId;
            String albumURI = PropertiesXmlFileHandler.readFromProperties("user.folder") + folderRelativePath;
            String fileName = photo.getId();

            DynamicDataHolder holder = new DynamicDataHolder();
            Map<String, String> dataMap = holder.getDataMap();
            dataMap.put("photo_id", photo.getId());
            dataMap.put("photo_uri", albumURI + "/" + fileName);
            dataMap.put("album_id", albumId);

            downloadAndStoreDetails(photoURL, holder);

            String sql = "UPDATE albums SET sync = ? WHERE album_id= ?;";
            DatabaseExec.execNonQuery(sql, "1", albumId);

        }

    }

    /**
     * *
     * @param holder Database table names holder
     *
     * downloads all the photos in the album given and stores in the
     * corresponding folder after makes a database entry in the photos table
     *
     */
    public static void downloadAndStoreDetails(final URL url, final DynamicDataHolder holder) {

        // String fileDestination = System.getProperty("user.dir").replaceAll("\\\\", "/") + folderRelativePath + "/" + fileName;
        final String fileDestination = holder.getDataMap().get("photo_uri");

        try {
            InputStream is = url.openStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] b = new byte[2048];
            int length = -2;

            Path pathToFile = Paths.get(fileDestination);
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);

            }
            is.close();
            os.close();
            byte[] response = os.toByteArray();

            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
            File file = new File(fileDestination);
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(response);
            fos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String sql_albums = "INSERT INTO photos (photo_id,photo_uri,album_id) "
                + "VALUES (?,?,?);";
        Map<String, String> data = holder.getDataMap();
        int rows = DatabaseExec.execNonQuery(sql_albums,
                data.get("photo_id"),
                data.get("photo_uri"),
                data.get("album_id"));

    }

    public static class DynamicDataHolder {

        private Map<String, String> keyMap;

        public DynamicDataHolder() {
            keyMap = new LinkedHashMap<String, String>();
        }

        public Map<String, String> getDataMap() {
            return this.keyMap;
        }

    }

}
