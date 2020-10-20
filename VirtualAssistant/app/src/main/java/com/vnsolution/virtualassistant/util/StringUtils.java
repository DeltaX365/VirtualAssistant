package com.vnsolution.virtualassistant.util;

import java.util.List;

public class StringUtils {
    public static String join(String delimiter, List<String> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null) {
            for (String type : list) {
                sb.append(type);
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
}
