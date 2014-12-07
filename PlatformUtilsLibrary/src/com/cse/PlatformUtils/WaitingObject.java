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
public class WaitingObject {

    private static WaitingObject obj;

    private WaitingObject() {
    }

    public static WaitingObject getInstance() {
        if (obj == null) {
            obj = new WaitingObject();
        }
        return obj;

    }
}
