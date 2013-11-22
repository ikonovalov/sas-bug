package com.luxoft.sas.bug.codepart;

import com.luxoft.sas.bug.codepart.iterator.CodePartIteratorFactory;
import com.luxoft.sas.bug.codepart.iterator.RegExpIterator;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Блок кода Step: User Written.
 */
public class UserWrittenCodePart extends SimpleCodePart {

    private static final Pattern START_PATTERN = Pattern.compile("(?s)Step:\\s+User Written");
    //private static final Pattern START_PATTERN = Pattern.compile("(?s)/\\*.*?Step:\\s+User Written");
    private static final Pattern END_PATTERN =  Pattern.compile("(?s)Step end User Written.*?\\*/");

    public static final CodePartIteratorFactory<UserWrittenCodePart> FACTORY = new CodePartIteratorFactory<UserWrittenCodePart>() {
        public Iterator<UserWrittenCodePart> getIterator(CodePart code) {
         return new RegExpIterator<UserWrittenCodePart>(code, START_PATTERN, END_PATTERN) {
           @Override
           protected UserWrittenCodePart asElement(CodePart codePart, int start, int end) {
             return new UserWrittenCodePart(codePart, start, end);
           }
         };
       }
    };

    public UserWrittenCodePart(CharSequence codeContent) {
        super(codeContent);
    }

    public UserWrittenCodePart(CodePart codePart, int start, int end) {
        super(codePart, start, end);
    }

}
