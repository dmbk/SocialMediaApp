/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cse.Utils.GenericUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Dulitha
 */
public class UrlAnalyzer {

    public static List<String> getListOfMatches(String regexPattern, String stringToMatch) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(stringToMatch);

        List<String> listMatches = new ArrayList<String>();

        while (matcher.find()) {
            listMatches.add(matcher.group(2));
        }
        return listMatches;
    }
}
