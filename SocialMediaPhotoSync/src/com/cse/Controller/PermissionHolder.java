/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.Controller;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * "user_photos,publish_stream,publish_actions"
 *
 * @author Dulitha
 */
public class PermissionHolder {

    private static EnumSet<PermissionType> permissions;//holds all grantd permissions 

    public PermissionHolder(PermissionType... permissionArray) {

        permissions = EnumSet.of(PermissionType.EMAIL, permissionArray);
        //email permission is initially required
    }

    public static int addPermissions(PermissionType... permissionArray) {

        for (int i = 0; i < permissionArray.length; i++) {
            permissions.add(permissionArray[i]);

        }
        includePermissions(getPermissionString(permissionArray));
        return permissionArray.length;//returns number of permissions added
    }//adds all the permissions parsed into the method into the EnumSet that holds granted permissions

    public static int removePermissions(PermissionType... permissionArray) {

        permissions.remove(new LinkedHashSet<PermissionType>(Arrays.asList(permissionArray)));

        revokePermissions(getPermissionString(permissionArray));
        return permissionArray.length;//returns number of permissions removed
    }//removes all the permissions parsed into the method into the EnumSet that holds granted permissions

    public static void revokePermissions(String... permisionString) {
        // String url="https://facebook.com/"
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet getReq = new HttpGet();
    }//removes permissions by calling Graph API

    public static void includePermissions(String... permissionString) {

    }// includes permissions by calling graph API

    public static String getPermissionString(PermissionType... permissionList) {
        String permissionString = "";
        for (int i = 0; i < permissionList.length; i++) {

            permissionString = permissionString + "," + permissionList[i].getPermissionString();
        }
        return permissionString;
    }//returns the given permissions set as a string sepereated by commas

    public static String getPermissionString() {
        return "user_photos,publish_stream,publish_actions,offline_access";
    }//returns permission which are already declared in enumset string  seperated by commas
}
