package com.luxoft.sas.bug;

import java.util.Iterator;
import java.util.regex.Pattern;

public class UserWritenCodePart implements CodePart {

    private CharSequence codeContent = null;

    private int[] indexs = new int[]{0,0};

    private static final Pattern START_PATTERN = Pattern.compile("(.+Transform:\\s+User Written(.*))");
    private static final Pattern END_PATTERN =  Pattern.compile("(.+End of User Written Code.+)");

   public static Iterator<UserWritenCodePart> getIterator(CharSequence code) {
     return new RegExpIterator<UserWritenCodePart>(code, START_PATTERN, END_PATTERN) {
       @Override
       protected UserWritenCodePart asElement(CharSequence codePart) {
         return new UserWritenCodePart(codePart);
       }
     };
   }

    public UserWritenCodePart() {
        super();
    }

    public UserWritenCodePart(CharSequence codeContent, int... indexs) {
        super();
        this.codeContent = codeContent;
        if (indexs != null && indexs.length != 0 && indexs.length != 2) {
            throw new IllegalArgumentException("Indexs can have only 2 values: start index and stop index");
        }
        this.indexs = indexs;
    }

    public CharSequence getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(CharSequence codeContent) {
        this.codeContent = codeContent;
    }
}
