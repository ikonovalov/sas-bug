package com.luxoft.sas.bug.codepart;

import com.luxoft.sas.bug.codepart.iterator.CodePartIteratorFactory;
import com.luxoft.sas.bug.codepart.iterator.RegExpIterator;

import java.util.Iterator;
import java.util.regex.Pattern;

public class SQLProcCodePart extends SimpleCodePart {

    private static final Pattern START_PATTERN = Pattern.compile("proc sql");
    private static final Pattern END_PATTERN =  Pattern.compile("quit;");

    public static final CodePartIteratorFactory<SQLProcCodePart> FACTORY = new CodePartIteratorFactory<SQLProcCodePart>() {
        public Iterator<SQLProcCodePart> getIterator(CodePart code) {
         return new RegExpIterator<SQLProcCodePart>(code, START_PATTERN, END_PATTERN) {
           @Override
           protected SQLProcCodePart asElement(CodePart codePart, int start, int end) {
             return new SQLProcCodePart(codePart, start, end);
           }
         };
       }
    };

    public SQLProcCodePart(CharSequence codeContent) {
        super(codeContent);
    }

    public SQLProcCodePart(CodePart codePart, int start, int end) {
        super(codePart, start, end);
    }

}
