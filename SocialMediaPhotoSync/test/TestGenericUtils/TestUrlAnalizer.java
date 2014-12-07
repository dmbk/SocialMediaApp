/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TestGenericUtils;

import com.cse.Utils.GenericUtils.UrlAnalyzer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author dmbk
 */
public class TestUrlAnalizer {
    @Test
    public void testgetListOfMatches() {
        String[] testarray={"junit","junit","junit"};
        String test="dadasjkdjunitdaija83nhadnakjunitwajehawdjunitsadas";
        Assert.assertEquals(testarray,UrlAnalyzer.getListOfMatches("junit", test));
        // used to match query strings and return them in a List
    }
}
