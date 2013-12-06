package com.luxoft.sas.bug.codepart.iterator;

import com.luxoft.sas.bug.codepart.CodePart;
import com.luxoft.sas.bug.codepart.SimpleCodePart;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Итератор блоков кода по начальному и конечному regex выражениям.
 *
 * @param <T> тип блоков
 */
public abstract class RegExpIterator<T> implements Iterator<T> {

    private final CodePart code;

    private Matcher startMatcher;
    private Matcher endMatcher;

    private boolean hasNext;

    /**
     * @param charSequence код
     * @param startPattern шаблон начала блока кода
     * @param endPattern шаблон конца блока кода
     */
    public RegExpIterator(CharSequence charSequence, Pattern startPattern, Pattern endPattern) {
        this.code = new SimpleCodePart(charSequence);
        CharSequence codeContent = code.getCodeContent();
        startMatcher = startPattern.matcher(codeContent);
        endMatcher = endPattern.matcher(codeContent);
        hasNext = startMatcher.find();
    }

    /**
     * @param code код
     * @param startPattern шаблон начала блока кода
     * @param endPattern шаблон конца блока кода
     */
    public RegExpIterator(CodePart code, Pattern startPattern, Pattern endPattern) {
        this.code = code;
        CharSequence codeContent = code.getCodeContent();
        startMatcher = startPattern.matcher(codeContent);
        endMatcher = endPattern.matcher(codeContent);
        hasNext = startMatcher.find();
    }

    /**
     * Поиск следующего блока.
     */
    private void walkToNext() {
        if (!hasNext) throw new NoSuchElementException();
        int startIndex = startMatcher.start();
        boolean endFound = endMatcher.find(startIndex); //search from start index
        if (!endFound) throw new IllegalStateException("End statement not found!");
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public void remove() {
        walkToNext();
        hasNext = startMatcher.find(endMatcher.end()); // search next start from current end
    }

    @Override
    public T next() {
        walkToNext();
        try {
            return asElement(code, startMatcher.start(), endMatcher.end());
        } finally {
            int endOfCurrentPart = endMatcher.end();
            hasNext = startMatcher.find(endOfCurrentPart); // search next start from current end
        }
    }

    protected abstract T asElement(CodePart code, int start, int end);

}
