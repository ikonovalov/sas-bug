package com.luxoft.sas.bug.metric;

import com.luxoft.sas.bug.codepart.iterator.CodePartIteratorFactory;
import com.luxoft.sas.bug.codepart.CodePart;

import java.util.Iterator;

public class SubBlockMetric implements Metric {

    private final Metric metric;
    private final CodePartIteratorFactory<? extends CodePart> codePartFactory;

    public SubBlockMetric(CodePartIteratorFactory<? extends CodePart> codePartFactory, Metric metric) {
        this.metric = metric;
        this.codePartFactory = codePartFactory;
    }

    public boolean applicable(final CodePart part) {
        Iterator<? extends CodePart> it = codePartFactory.getIterator(part.getCodeContent());
        while (it.hasNext()) {
            CodePart codeSubPart = it.next();
            System.out.print(codeSubPart);
            if(metric.applicable(codeSubPart)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return metric + " in " + codePartFactory.getClass().getSimpleName();
    }
}
