package ru.luxoft.maven.alm.checkstyle.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ALMUtils {

    public static String asQuery(String query) {
        try {
            return "query=" + URLEncoder.encode("{" + query + "}", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported UTF-8 Encoding", e);
        }
    }

    public static String getXMLEntityField(String entity, String fieldName) throws IllegalStateException {
        Pattern p = Pattern.compile("<Field Name=\"" + fieldName + "\"><Value>(\\d*)</Value></Field>");
        Matcher m = p.matcher(entity.replace("\n", ""));
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }


}
