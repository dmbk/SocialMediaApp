/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.PropertiesFileTest;

import com.cse.properties.PropertiesXmlFileHandler;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author dmbk
 */
public class PropertiesFileTest {

    @Test
    public void testPropertiesFilesLoad() {
        Assert.assertEquals("E:/movable/NetBeansProjects/SocialApp_UI", PropertiesXmlFileHandler.readFromProperties("app.home"));
        Assert.assertEquals("E:/facebook test albums", PropertiesXmlFileHandler.readFromProperties("user.folder"));

        String[] keys = {"user.folder", "app.home", "User.Folder"};
        Set<String> set = new LinkedHashSet<String>();
        for (int i = 0; i < keys.length; i++) {
            set.add(keys[i]);

        }
        Assert.assertEquals(set, PropertiesXmlFileHandler.getKeySet());

    }

}
