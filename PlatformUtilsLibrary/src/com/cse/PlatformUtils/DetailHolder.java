/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cse.PlatformUtils;

/**
 *
 * @author Dulitha
 */
public class DetailHolder {
    private static String authCode;

    public static void setAuthCode(String authCode) {
        DetailHolder.authCode = authCode;
    }

    public static String getAuthCode() {
        return authCode;
    }
    
}
