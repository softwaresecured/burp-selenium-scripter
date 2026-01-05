package com.softwaresecured.burp.util;

import com.softwaresecured.burp.ui.HighlightRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollaboratorUtil {
    public static String extractFormattedValue( String rawSmtp, String regex, String format ) {
        String result = format;
        HashMap<Integer,String> output = new HashMap<>();
        Integer[] matchGroupFormatParameters = getFormatParameters(format);
        Pattern p = Pattern.compile(regex,Pattern.MULTILINE|Pattern.DOTALL);
        Matcher m = p.matcher(rawSmtp);
        while (m.find()) {
            for ( int i = 0; i < m.groupCount()+1; i++) {
                output.put(i,m.group(i));
            }
        }
        for ( int i : matchGroupFormatParameters ) {
            result = result.replaceAll("\\$%d".formatted(i),output.get(i));
        }
        return result;
    }

    public static ArrayList<HighlightRange> getHighlights(String rawSmtp, String regex, String format ) {
        ArrayList<HighlightRange> highlightRanges = new ArrayList<HighlightRange>();
        Pattern p = Pattern.compile(regex,Pattern.MULTILINE|Pattern.DOTALL);
        Matcher m = p.matcher(rawSmtp);
        List<Integer> matchGroups = Arrays.asList(getFormatParameters(format));
        while (m.find()) {
            for ( int i = 0; i < m.groupCount(); i++ ) {
                if ( matchGroups.contains(i+1)) {
                    highlightRanges.add(new HighlightRange(m.start(i),m.end(i)));
                }
            }
        }

        return highlightRanges;
    }

    public static Integer[] getFormatParameters( String formatString ) {
        ArrayList<Integer> formatParameters = new ArrayList<>();
        Pattern p = Pattern.compile("(\\$\\d+)");
        Matcher m = p.matcher(formatString);
        while ( m.find() ) {
            formatParameters.add(Integer.parseInt(m.group(0).replaceAll("\\$","")));
        }
        return formatParameters.toArray(new Integer[0]);
    }
}
