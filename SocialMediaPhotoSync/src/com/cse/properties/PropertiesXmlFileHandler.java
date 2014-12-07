/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmbk
 */
public class PropertiesXmlFileHandler {

    private static final String PROPERTIES_FILE = "app_properties.xml";

    public static void writeToProperties(String key, String value, String comment) throws IOException {
        // get properties object
        Properties props = new Properties();

        // get file object
        File file = new File(PROPERTIES_FILE);

        // check whether the file exists
        if (file.exists()) {
            // get inpustream of the file
            InputStream is = new FileInputStream(PROPERTIES_FILE);

            // load the xml file into properties format
            props.loadFromXML(is);

            // store all the property keys in a set 
            Set<String> names = props.stringPropertyNames();

            // iterate over all the property names
            for (Iterator<String> i = names.iterator(); i.hasNext();) {
                // store each propertyname that you get
                String propname = i.next();

                // set all the properties (since these properties are not automatically stored when you update the file). All these properties will be rewritten. You also set some new value for the property names that you read
                props.setProperty(propname, props.getProperty(propname));
            }

        }
        // add some new properties to the props object
        props.setProperty(key, value);

        // get outputstream object to for storing the properties into the same xml file that you read
        OutputStream os = new FileOutputStream(PROPERTIES_FILE);

        // store the properties detail into a pre-defined XML file
        props.storeToXML(os, comment, "UTF-8");
    }

    public static String readFromProperties(String key) {
        //Map<String, String> env = System.getenv();
        String value = null;
        try {
            File file = new File(PROPERTIES_FILE);
            FileInputStream fileInput = new FileInputStream(PROPERTIES_FILE);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);
            fileInput.close();
            value = properties.getProperty(key);

            //System.out.println(key + ": " + value);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Set<String> getKeySet() {
        Set<String> keySet = new LinkedHashSet<String>();
        try {
            File file = new File(PROPERTIES_FILE);
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();

                keySet.add(key);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keySet;
    }

    public static void main(String[] args) {

        /*for (int i = 0; i < 10; i++) {
         try {
         writeToProperties("key" + i, "value" + i, "comment" + i);
         } catch (IOException ex) {
         Logger.getLogger(PropertiesXmlFileHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
         }
         for (int i = 0; i < 11; i++) {
         readFromProperties("key" + i);
         }
         getKeySet();*/
        try {
            writeToProperties("app.home", "E:/movable/NetBeansProjects/SocialApp_UI", "User selected folder");
        } catch (IOException ex) {
            Logger.getLogger(PropertiesXmlFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
