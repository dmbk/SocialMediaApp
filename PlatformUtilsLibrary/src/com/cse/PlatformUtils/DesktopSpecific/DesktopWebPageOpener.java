/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.PlatformUtils.DesktopSpecific;

import com.cse.PlatformUtils.DesktopSpecific.EmbeddedResources.Browser;

import com.cse.PlatformUtils.WebPageOpener;

/**
 *
 * @author Dulitha
 */
public class DesktopWebPageOpener extends WebPageOpener {

    private String currentUrl;
    private String code;

    @Override
    public String getCurrentUrl() {
        return currentUrl;
    }

    @Override
    public void openWebPage(String urlString) {

        currentUrl = urlString;
        Browser.launchBrowser(urlString);

        /*try {
         Desktop.getDesktop().browse(new URL(urlString).toURI());
         } catch (IOException e) {
         } catch (URISyntaxException e) {
         }*/
    }

}
