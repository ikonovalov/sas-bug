package com.luxoft.sas.bug.codepart;

import com.luxoft.sas.bug.codepart.iterator.CodePartIteratorFactory;
import com.luxoft.sas.bug.codepart.iterator.RegExpIterator;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Блок кода macro.
 */
public class MacrosCodePart extends SimpleCodePart {

    private static final Pattern START_PATTERN = Pattern.compile("%macro(.*);");
    private static final Pattern END_PATTERN =  Pattern.compile("%mend(.*);");

    public static final CodePartIteratorFactory<MacrosCodePart> FACTORY = new CodePartIteratorFactory<MacrosCodePart>() {
        public Iterator<MacrosCodePart> getIterator(CodePart code) {
            return new RegExpIterator<MacrosCodePart>(code, START_PATTERN, END_PATTERN) {
                @Override
                protected MacrosCodePart asElement(CodePart code, int start, int end) {
                    return new MacrosCodePart(code, start, end);
                }
            };
        }
    };

    public MacrosCodePart(CharSequence codeContent) {
        super(codeContent);
    }

    public MacrosCodePart(CodePart codePart, int start, int end) {
        super(codePart, start, end);
    }
}
