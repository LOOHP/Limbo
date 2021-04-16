package com.loohp.limbo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomStringUtils {

    public static boolean arrayContains(String compare, String[] args, boolean IgnoreCase) {
        return IgnoreCase ? Arrays.stream(args).anyMatch(each -> each.equalsIgnoreCase(compare)) : Arrays.asList(args).contains(compare);
    }

    public static boolean arrayContains(String compare, String[] args) {
        return arrayContains(compare, args, true);
    }

    public static String[] splitStringToArgs(String str) {
        List<String> tokens = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        boolean insideQuote = false;

        for (char c : str.toCharArray()) {
            if (c == '"') {
                insideQuote = !insideQuote;
            } else if (c == ' ' && !insideQuote) {
                if (sb.length() > 0) {
                    tokens.add(sb.toString());
                }
                sb.delete(0, sb.length());
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());

        return tokens.toArray(new String[0]);
    }

    public static int getIndexOfArg(String str, int ordinal) {
        StringBuilder sb = new StringBuilder();

        boolean insideQuote = false;

        int pos = 0;
        int found = 0;
        for (char c : str.toCharArray()) {
            if (c == '"') {
                insideQuote = !insideQuote;
            } else if (c == ' ' && !insideQuote) {
                if (sb.length() > 0) {
                    found++;
                }
                sb.delete(0, sb.length());
            } else {
                sb.append(c);
            }
            if (found == ordinal) {
                return pos;
            }
            pos++;
        }

        return -1;
    }

}
