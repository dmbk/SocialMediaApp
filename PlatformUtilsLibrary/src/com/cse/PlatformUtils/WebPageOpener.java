/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.PlatformUtils;

import com.cse.PlatformUtils.DesktopSpecific.DesktopWebPageOpener;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Dulitha
 */
public abstract class WebPageOpener {

    public abstract void openWebPage(String urlString);

    public static WebPageOpener getInstance() {
        
        return new DesktopWebPageOpener();
    }

    public abstract String getCurrentUrl();

}
