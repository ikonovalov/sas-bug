package com.luxoft.sas.bug.codepart.iterator;

import com.luxoft.sas.bug.codepart.CodePart;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ttishin
 * Date: 29.08.13
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public interface CodePartIteratorFactory<T extends CodePart> {

    Iterator<T> getIterator(CodePart code);

}
