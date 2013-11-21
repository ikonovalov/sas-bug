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
        this.end = codeContent.length();
    }

    public SimpleCodePart(CharSequence codeContent, int start, int end) {
        if (start < 0) throw new ArrayIndexOutOfBoundsException("start can't be < 0");
        if (end > codeContent.length()) throw new ArrayIndexOutOfBoundsException("end can't be after end of codeContent");

        this.codeContent = codeContent;
        this.start = start;
        this.end = end;
    }

    public SimpleCodePart(CodePart codePart, int start, int end) {
        if (!(codePart instanceof SimpleCodePart)) throw new UnsupportedOperationException("Unsupported codePart class"); //TODO: implement
        SimpleCodePart source = ((SimpleCodePart)codePart);
        this.codeContent = source.codeContent;

        if (start < -source.getStart()) throw new ArrayIndexOutOfBoundsException("start char out of original source codeContent");
        if (end > codeContent.length() - source.getStart()) throw new ArrayIndexOutOfBoundsException("end can't be after end of source codeContent");

        this.start = source.getStart() + start;
        this.end = source.getStart() + end;
    }

    public CharSequence getCodeContent() {
        return codeContent.subSequence(start, end);
    }

    public void setCodeContent(CharSequence newCodeContent) {
        StringBuilder sb = new StringBuilder();

        if (start > 0) {
            sb.append(codeContent.subSequence(0, start - 1));
        }

        sb.append(newCodeContent);

        if (end < codeContent.length()) {
            sb.append(codeContent.subSequence(end, codeContent.length()));
            end = start + newCodeContent.length() - 1;
        }

        codeContent = sb;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + start + "," + end + "]";
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getStartLine() {
        int startLine = 1;
        for (int i = 0; i < start; i++) {
            if (codeContent.charAt(i) == '\n') startLine++;
        }
        return startLine;
    }

    public int getLinesOffset(int codePartCharNumber) {
        int linesCount = 0;
        for (int i = start; i < start + codePartCharNumber; i++) {
            if (codeContent.charAt(i) == '\n') linesCount++;
        }
        return linesCount;
    }
}
