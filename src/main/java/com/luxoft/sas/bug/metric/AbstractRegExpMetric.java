package com.luxoft.sas.bug.metric;

import com.luxoft.sas.bug.codepart.CodePart;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractRegExpMetric implements Metric {

    private final Pattern pattern;

    public AbstractRegExpMetric(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public int applicable(final CodePart part) {
        Matcher matcher = pattern.matcher(part.getCodeContent());
        if (matcher.find()) {
            //System.out.println(part.getCodeContent().subSequence(matcher.start(), matcher.end()));
            return matcher.start();
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return pattern.toString();
    }
}
