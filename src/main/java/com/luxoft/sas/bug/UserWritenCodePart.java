package com.luxoft.sas.bug;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserWritenCodePart implements CodePart {

    private CharSequence codeContent = null;

    private int[] indexs = new int[]{0,0};

    /**
     * We assume that CodePart start with  (Transform:\s+User Written)
     * and end with (End of User Written Code)
     */
    public enum POSITION {
        START {
            Pattern getPattern() {
                return  Pattern.compile("(.+Transform:\\s+User Written(.*))");
            }
        },
        END {
           Pattern getPattern() {
               return Pattern.compile("(.+End of User Written Code.+)");
           }
        };

        abstract Pattern getPattern();
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

    public static Matcher getMatcher(CharSequence charSequence, POSITION position) {
        return position.getPattern().matcher(charSequence);
    }

    public CharSequence getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(CharSequence codeContent) {
        this.codeContent = codeContent;
    }
}
