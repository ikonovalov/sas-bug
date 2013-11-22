package com.luxoft.sas.bug.codepart.iterator;

import com.luxoft.sas.bug.codepart.CodePart;

import java.util.Iterator;

/**
 * Итератор блоков кода.
 *
 * @param <T> тип блоков
 */
public interface CodePartIteratorFactory<T extends CodePart> {

    Iterator<T> getIterator(CodePart code);

}
