package com.luxoft.sas.bug.metric;

import com.luxoft.sas.bug.CodePart;
import com.luxoft.sas.bug.Metric;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRegExpMetric implements Metric {

    private final Pattern pattern;

    protected AbstractRegExpMetric() {
        pattern = Pattern.compile(getRegExp());
    }

    public abstract String getRegExp();

    public boolean applicable(final CodePart part) {
        Matcher matcher = pattern.matcher(part.getCodeContent());
        return matcher.find();
    }


    @Override
    public String toString() {
        return pattern.toString();
    }
}
