/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cse.PlatformUtils;

import com.cse.PlatformUtils.DesktopSpecific.DesktopClipBoardExtractor;

/**
 *
 * @author Dulitha
 */
public abstract class ClipBoardExtractor {
    public abstract String getClipBoardData();
    public static ClipBoardExtractor getInstance(){
        return new DesktopClipBoardExtractor();
    }
}
