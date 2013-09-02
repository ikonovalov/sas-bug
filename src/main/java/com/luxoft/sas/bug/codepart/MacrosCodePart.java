package com.luxoft.sas.bug.codepart;

import com.luxoft.sas.bug.codepart.iterator.CodePartIteratorFactory;
import com.luxoft.sas.bug.codepart.iterator.RegExpIterator;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ttishin
 * Date: 29.08.13
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
public class MacrosCodePart extends SimpleCodePart {

    private static final Pattern START_PATTERN = Pattern.compile("%macro(.*);");
    private static final Pattern END_PATTERN =  Pattern.compile("%mend(.*);");

    public static final CodePartIteratorFactory<MacrosCodePart> FACTORY = new CodePartIteratorFactory<MacrosCodePart>() {
        public Iterator<MacrosCodePart> getIterator(CharSequence code) {
            return new RegExpIterator<MacrosCodePart>(code, START_PATTERN, END_PATTERN) {
                @Override
                protected MacrosCodePart asElement(CharSequence code, int start, int end) {
                    return new MacrosCodePart(code, start, end);
                }
            };
        }
    };

    public MacrosCodePart(CharSequence codeContent, int start, int end) {
        super(codeContent, start, end);
    }
}
