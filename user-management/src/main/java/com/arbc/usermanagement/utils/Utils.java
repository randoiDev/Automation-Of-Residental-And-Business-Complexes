package com.arbc.usermanagement.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String extractDuplicateValueFromMongoException(String errorMessage) {

        // Define a regex pattern to match the duplicate key field and value
        String regex = "dup key: \\{ (\\w+): \"([^\"]+)\" }";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(errorMessage);

        // If a match is found, return the value from error message
        if (matcher.find()) {
            return matcher.group(2);
        }

        // If no match is found, return a generic message
        return "unknown";
    }
}
