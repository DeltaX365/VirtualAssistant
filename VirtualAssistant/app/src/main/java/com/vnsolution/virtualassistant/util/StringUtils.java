package com.vnsolution.virtualassistant.util;

import java.util.List;

public class StringUtils {
    public static String join(String delimiter, List<String> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i));
                if (i < list.size() - 1) {
                    sb.append(delimiter);
                }
            }
        }
        return sb.toString();
    }
}
