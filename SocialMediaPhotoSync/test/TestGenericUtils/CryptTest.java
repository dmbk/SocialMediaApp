/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestGenericUtils;

import com.cse.Utils.GenericUtils.Crypt;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author dmbk
 */
public class CryptTest {

    static String encryptedString = "N9qXREynNrQWoYMfUUep8nQt908qsojK";
    static String decryptedString = "This is a message";

    @Test
    public void testCryptUtils() {
        Assert.assertEquals(encryptedString, new Crypt().encrypt(decryptedString));

        Assert.assertEquals(decryptedString, new Crypt().decrypt(encryptedString));

    }
}
