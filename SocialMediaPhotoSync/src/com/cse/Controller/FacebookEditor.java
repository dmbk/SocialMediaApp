/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.Controller;

import com.cse.DBAccess.DatabaseExec;
import com.cse.OAuth.WebOAuth;
import com.cse.Utils.GenericUtils.Crypt;
import com.cse.properties.PropertiesXmlFileHandler;
import facebook4j.Album;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Media;
import facebook4j.PhotoUpdate;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Token[CAAVaMOdUxlYBAOqDH7ZBjs0282FZAGYvXaPNLCbwjRCTroRRbjl9VmXbBuEinoPsADpo2wUGy9g9ZBwvNqfZCkv3t4FBaxV7VwJ8ZAOE4ZCPZBBcUSjpZB0x3pbG0NGxhMQtw60AzHT3tii3tfratUCAydvAyj8Y0d1lqxZCrj8GvaycdcFjA8VEbdgsThi4Ew8qTQqlHZBq1LI6rmJG7minHM
 * , ]
 *
 * @author Dulitha
 */
public class FacebookEditor {

    private String accessToken;
    private static Facebook facebook;

    private ResponseList<Album> albumList;
    private String uId;

    public static Facebook getFacebook() {
        return facebook;
    }

    public void setAlbumList(ResponseList<Album> albumList) {
        this.albumList = albumList;
    }

    public FacebookEditor(String accessToken, String uId) {

        facebook = new FacebookFactory().getInstance();
        facebook.setOAuthAppId("1506540969576022", "77f8887895c3b5df16c2a6c9bd3e1d7d");
        facebook.setOAuthPermissions(PermissionHolder.getPermissionString());
        this.setAccessToken(accessToken);
        this.uId = uId;
    }

    public FacebookEditor(Facebook fb, String uId) {

        facebook = fb;
        this.uId = uId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        facebook.setOAuthAccessToken(new AccessToken(accessToken));
    }

    /**
     * @param picFileName -name of the picture to upload
     * @param pathToPicture -path to the picture file
     * @param message -message to be set with the picture upload
     * @return id of the picture
     *
     *
     */
    public String picUpload(String picFileName, String pathToPicture, String message) throws IOException, FacebookException {
        /* 
        
         FacebookClient facebookClient = new DefaultFacebookClient(this.accessToken);

         FacebookType publishPhotoResponse = facebookClient.publish("me/photos", FacebookType.class,
         BinaryAttachment.with(picFileName, new FileInputStream(pathToPicture)),
         Parameter.with("message", "Test photo upload"));

        
         return publishPhotoResponse.getId();*/

        PhotoUpdate photoUpdate = new PhotoUpdate(new Media(picFileName, new FileInputStream(pathToPicture)));

        String id = facebook.postPhoto(photoUpdate.message(message));
        return id;
    }

    public ResponseList<Album> getAlbums(Facebook fb) throws FacebookException {
        ResponseList<Album> albumList = null;

        albumList = fb.getAlbums();

        return albumList;
    }

    /**
     *
     * @param list -the list of responses that contains albums name list
     *
     * calls the method to download and store the files and make relevant
     * database entries
     *
     */
    public void getAlbumCovers(List<Album> list, Facebook fb) throws FacebookException {
        if (uId == null) {
            return;
        }
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Album album = (Album) itr.next();
            try {

                URL coverURL = fb.getAlbumCoverPhoto(album.getId());
                String folderRelativePath = "/albums/" + uId + "/" + album.getId();
                String albumURI = PropertiesXmlFileHandler.readFromProperties("user.folder") + folderRelativePath;
                String fileName = "cover_" + album.getName();
                String albumName = album.getName();

                if (albumName.startsWith("Profile Pictures")) {

                    downloadProfilePic(coverURL, PropertiesXmlFileHandler.readFromProperties("user.folder") + "/albums/" + uId + "/" + "prof_pic");

                }
                DynamicDataHolder holder = new DynamicDataHolder();
                Map<String, String> dataMap = holder.getDataMap();
                dataMap.put("album_id", album.getId());
                dataMap.put("album_name", album.getName());
                dataMap.put("cover_uri", albumURI + "/" + fileName);
                dataMap.put("album_uri", albumURI);
                dataMap.put("sync", "0");
                dataMap.put("u_id", uId);
// "690053834396886"  WebOAuth.getUserId()
                downloadAndStoreDetails(coverURL, holder);

            } catch (IOException ex) {
                Logger.getLogger(FacebookEditor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public void getAlbumCovers(String albumId, String albumName, Facebook fb) throws FacebookException {
        if (uId == null) {
            return;
        }

        try {

            URL coverURL = fb.getAlbumCoverPhoto(albumId);
            String folderRelativePath = "/albums/" + uId + "/" + albumId;
            String albumURI = PropertiesXmlFileHandler.readFromProperties("user.folder") + folderRelativePath;
            String fileName = "cover_" + albumName;

            DynamicDataHolder holder = new DynamicDataHolder();
            Map<String, String> dataMap = holder.getDataMap();

            dataMap.put("cover_uri", albumURI + "/" + fileName);

            downloadOnly(coverURL, holder);

        } catch (IOException ex) {
            Logger.getLogger(FacebookEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * album_id TEXT PRIMARY KEY NOT NULL, album_name TEXT , cover_uri	TEXT ,
     * album_uri TEXT NOT NULL, sync INTEGER	NOT NULL, u_id	TEXT	NOT NULL,
     */
    public void downloadAndStoreDetails(final URL url, final DynamicDataHolder holder) throws IOException {

        // String fileDestination = System.getProperty("user.dir").replaceAll("\\\\", "/") + folderRelativePath + "/" + fileName;
        final String fileDestination = holder.getDataMap().get("cover_uri");
        InputStream is = url.openStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] b = new byte[2048];
        int length = -2;

        Path pathToFile = Paths.get(fileDestination);

        try {
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);

            }
            is.close();
            os.close();
            byte[] response = os.toByteArray();

            Files.createDirectories(pathToFile.getParent());
            //Files.createFile(pathToFile);
            File file = new File(fileDestination);

            FileOutputStream fos = new FileOutputStream(file);

            fos.write(response);
            fos.close();

            String sql_albums = "INSERT INTO albums (album_id,album_name,cover_uri,album_uri,sync,u_id) "
                    + "VALUES (?,?,?,?,?,?);";
            Map<String, String> data = holder.getDataMap();
            int rows = DatabaseExec.execNonQuery(sql_albums,
                    data.get("album_id"),
                    data.get("album_name"),
                    data.get("cover_uri"),
                    data.get("album_uri"),
                    data.get("sync"),
                    data.get("u_id"));

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public void downloadOnly(final URL url, final DynamicDataHolder holder) throws IOException {

        // String fileDestination = System.getProperty("user.dir").replaceAll("\\\\", "/") + folderRelativePath + "/" + fileName;
        final String fileDestination = holder.getDataMap().get("cover_uri");
        InputStream is = url.openStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] b = new byte[2048];
        int length = -2;

        Path pathToFile = Paths.get(fileDestination);

        try {
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

    }

    public void downloadProfilePic(URL url, String fileDestination) throws FacebookException, IOException {
        //URL url = fb.getMe().getPicture().getURL();

        InputStream is = url.openStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] b = new byte[2048];
        int length = -2;

        Path pathToFile = Paths.get(fileDestination);

        try {
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);

            }
            is.close();
            os.close();
            byte[] response = os.toByteArray();

            Files.createDirectories(pathToFile.getParent());

            File file = new File(fileDestination);

            FileOutputStream fos = new FileOutputStream(file);

            fos.write(response);
            fos.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public class DynamicDataHolder {

        private Map<String, String> keyMap;

        public DynamicDataHolder() {
            keyMap = new LinkedHashMap<String, String>();
        }

        public Map<String, String> getDataMap() {
            return this.keyMap;
        }

    }

    public static void main(String... args) {
        /* String token = "CAAVaMOdUxlYBAPtcZBmBG2Kjl8Nz"
         + "5AEggIl1xcoFjbDEivPp4RrtGxpWOnBZBN9Tev50jN"
         + "ZAJ8JZCpyLcG5ZCucPHjbKraQkOKSaDIZCoqMrPAYqeTYHFZB3D"
         + "kFvxtORDAZAgVbCOUiUB16LUC4DV9yrDE9NejrKF"
         + "G16VYjAektnaZC8B2ClfQZBjjg2DKuTiv8YJDvvHc8iWZBxP3IxGIy6ZBDz";*/
        WebOAuth.setU_pwd(new Crypt().encrypt("dmbk1991"));
        //WebOAuth.doOAuthDance();
        String token = WebOAuth.getAccessToken();

      //  FacebookEditor editor = new FacebookEditor(token);
        /*try {
         editor.picUpload("wallpaper.jpg", "E:/movable/NetBeansProjects/SocialMediaPhotoSync/wallpaper.jpg", "Test message");

         } catch (IOException ex) {
         Logger.getLogger(FacebookEditor.class.getName()).log(Level.SEVERE, null, ex);
         } catch (FacebookException ex) {
         Logger.getLogger(FacebookEditor.class.getName()).log(Level.SEVERE, null, ex);
         }*/
        //editor.getAlbumCovers(editor.getAlbums());
    }

}
