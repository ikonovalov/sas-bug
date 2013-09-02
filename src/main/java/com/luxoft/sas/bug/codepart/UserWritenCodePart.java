package com.luxoft.sas.bug.codepart;

import com.luxoft.sas.bug.codepart.iterator.CodePartIteratorFactory;
import com.luxoft.sas.bug.codepart.iterator.RegExpIterator;

import java.util.Iterator;
import java.util.regex.Pattern;

public class UserWritenCodePart extends SimpleCodePart {

    private static final Pattern START_PATTERN = Pattern.compile("Transform:\\s+User Written");
    private static final Pattern END_PATTERN =  Pattern.compile("End of User Written Code");

    public static final CodePartIteratorFactory<UserWritenCodePart> FACTORY = new CodePartIteratorFactory<UserWritenCodePart>() {
        public Iterator<UserWritenCodePart> getIterator(CharSequence code) {
         return new RegExpIterator<UserWritenCodePart>(code, START_PATTERN, END_PATTERN) {
           @Override
           protected UserWritenCodePart asElement(CharSequence codePart, int start, int end) {
             return new UserWritenCodePart(codePart, start, end);
           }
         };
       }
    };

    public UserWritenCodePart(CharSequence codeContent, int start, int end) {
        super(codeContent, start, end);
    }

}
