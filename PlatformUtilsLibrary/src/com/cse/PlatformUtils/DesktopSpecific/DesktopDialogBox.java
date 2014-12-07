/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.PlatformUtils.DesktopSpecific;

import com.cse.PlatformUtils.AbstractDialogBox;
import java.awt.EventQueue;
//import static java.awt.SystemColor.window;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Dulitha
 */
public class DesktopDialogBox extends AbstractDialogBox {

    String message;
    String title;

    public DesktopDialogBox(String message, String title) {
        this.message=message;
        this.title=title;
    }

    public void popUpInfoMessgageDialog() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                } catch (InstantiationException ex) {
                } catch (IllegalAccessException ex) {
                } catch (UnsupportedLookAndFeelException ex) {
                }

                JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
                JOptionPane.getRootFrame().dispose();
                //WebOAuth.onUserAuthConfirmation();

            }
        });
    }

}
