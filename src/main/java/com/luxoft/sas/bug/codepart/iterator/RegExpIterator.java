package com.luxoft.sas.bug.codepart.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Итератор блоков кода.
 */
public abstract class RegExpIterator<T> implements Iterator<T> {

    private final CharSequence code;

    private Matcher startMatcher;
    private Matcher endMatcher;

    private boolean hasNext;

    public RegExpIterator(CharSequence code, Pattern startPattern, Pattern endPattern) {
        this.code = code;
        startMatcher = startPattern.matcher(code);
        endMatcher = endPattern.matcher(code);
        hasNext = startMatcher.find();
    }

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

    protected abstract T asElement(CharSequence code, int start, int end);

}
