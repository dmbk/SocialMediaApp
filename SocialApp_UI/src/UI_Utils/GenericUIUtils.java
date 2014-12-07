/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI_Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author dmbk
 */
public class GenericUIUtils {

    /**
     * *
     * gives the file URL as a string
     *
     * @param relativePath -the relative path to the resource, relative to the
     * class path
     *
     */
    public static URL getUserDirURL(String relativePath) throws MalformedURLException {
        
        // System.out.println(new File(System.getProperty("user.dir").replaceAll("\\\\", "/")+relativePath).toURI().toURL().toExternalForm());
        return new URL("file:///" + System.getProperty("user.dir").replaceAll("\\\\", "/") + relativePath);
    }
}
