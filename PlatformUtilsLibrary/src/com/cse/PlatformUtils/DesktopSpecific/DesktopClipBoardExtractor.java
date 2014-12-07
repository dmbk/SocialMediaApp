/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.PlatformUtils.DesktopSpecific;

import com.cse.PlatformUtils.ClipBoardExtractor;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dulitha
 */
public class DesktopClipBoardExtractor extends ClipBoardExtractor {

    @Override
    public String getClipBoardData() {
        try {
            String data = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
            return data;
        } catch (UnsupportedFlavorException ex) {

        } catch (IOException ex) {

        }
        return null;
    }

}
