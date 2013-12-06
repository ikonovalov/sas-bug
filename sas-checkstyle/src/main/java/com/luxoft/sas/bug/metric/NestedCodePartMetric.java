package com.luxoft.sas.bug.metric;

import com.luxoft.sas.bug.codepart.iterator.CodePartIteratorFactory;
import com.luxoft.sas.bug.codepart.CodePart;

import java.util.Iterator;

/**
 * Метрика позволяющая из указанной CodePart вычленять дополнительнче секции кода и применять метрику именно к ним.
 */
public class NestedCodePartMetric implements Metric {

    private final Metric metric;

    private final CodePartIteratorFactory<? extends CodePart> codePartFactory;

    /**
     *
     * @param codePartFactory фабрика итератора влеженного кода.
     * @param metric - метрика, применяемая к секциям влеженого кода.
     */
    public NestedCodePartMetric(CodePartIteratorFactory<? extends CodePart> codePartFactory, Metric metric) {
        this.metric = metric;
        this.codePartFactory = codePartFactory;
    }

    public int applicable(final CodePart part) {
        Iterator<? extends CodePart> it = codePartFactory.getIterator(part);
        while (it.hasNext()) {
            CodePart codeSubPart = it.next();
            //System.out.print(codeSubPart);
            int startChar = metric.applicable(codeSubPart);
            if(startChar >= 0) {
                return codeSubPart.getStart() - part.getStart() + startChar;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return metric + " in " + codePartFactory.getClass().getSimpleName();
    }
}
