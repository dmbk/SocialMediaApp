/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.Controller;

/**
 *
 * @author Dulitha
 */
public enum PermissionType {

    //more to be added
    EMAIL("email"), FRIEND_LIST(""), USER_LIKES("user_likes");
    private String permissionString = "none";
   

    private PermissionType(String permissionString) {
        this.permissionString = permissionString;
        
    }

    public String getPermissionString() {
        return permissionString;
    }

    

}
