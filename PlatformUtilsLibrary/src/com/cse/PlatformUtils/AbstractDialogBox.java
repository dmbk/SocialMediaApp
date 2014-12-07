/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.PlatformUtils;

import com.cse.PlatformUtils.DesktopSpecific.DesktopDialogBox;

/**
 *
 * @author Dulitha
 */
public abstract class AbstractDialogBox {

    public abstract void popUpInfoMessgageDialog();

    public static AbstractDialogBox getInstance(String message, String title) {
        return new DesktopDialogBox(message, title);
    }
}
