/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestControllers;

import com.cse.WindowControllers.AlbumViewerWindowController;
import com.cse.WindowControllers.SelectWindowController;
import com.cse.WindowControllers.UserHomeWindowController;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;

/**
 *
 * @author dmbk
 */
public class TestWindowControllers {

    static String testFileSrc = "C:/test/testFile";
    static String testFileDest = "C:/test/testFileDest";

    @Test(expected = IOException.class)
    public void testAlbumViewerWindowControllerUtilMethods() throws IOException {
        AlbumViewerWindowController.copyFile(new File(testFileSrc), new File(testFileDest));
    }

    @Test(expected = FacebookException.class)
    public void testSelectWindowControllerUtils() {
        Set<String> set = new LinkedHashSet<String>();
        set.add("false id");
        SelectWindowController.synchronizePhotos(testFileSrc, set);
    }

    @Test(expected = SQLException.class)
    public void testUserHomerWindowControllerUtilMethods() throws IOException, FacebookException {
        UserHomeWindowController.createAllNewAlbums("asdas", "100331856702423", "sadasda", FacebookFactory.getSingleton());
    }

}
