package com.luxoft.sas.bug.codepart;

/**
 * Блок кода.
 */
public interface CodePart {

    /**
     * @return блок кода
     */
    CharSequence getCodeContent();

    /**
     * Заменить весь блок.
     *
     * @param codeContent новый код
     */
    void setCodeContent(final CharSequence codeContent);

    /**
     * @return номер первого символа кода в общем тексте.
     */
    int getStart();

    /**
     * @return номер поседнего символа кода в общем тексте.
     */
    int getEnd();
}
