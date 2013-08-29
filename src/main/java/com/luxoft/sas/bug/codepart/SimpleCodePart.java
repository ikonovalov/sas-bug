package com.luxoft.sas.bug.codepart;

/**
 * Created with IntelliJ IDEA.
 * User: ttishin
 * Date: 29.08.13
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCodePart implements CodePart {

    private CharSequence codeContent = null;

    private int start;
    private int end;

    public SimpleCodePart(CharSequence codeContent) {
        this.codeContent = codeContent;
        this.start = 0;
        this.end = codeContent.length() - 1;
    }

    public SimpleCodePart(CharSequence codeContent, int start, int end) {
        this.codeContent = codeContent;
        this.start = start;
        this.end = end;
        // TODO validate start,end
    }

    public CharSequence getCodeContent() {
        return codeContent.subSequence(start, end);
    }

    public void setCodeContent(CharSequence codeContent) {
        this.codeContent = codeContent;
        // TODO check start,end
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + start + "," + end + "]";
    }
}
