package com.luxoft.sas.bug.codepart;

public interface CodePart {

    public CharSequence getCodeContent();

    public void setCodeContent(final CharSequence codeContent);

    public int getStart();

    public int getEnd();
}
