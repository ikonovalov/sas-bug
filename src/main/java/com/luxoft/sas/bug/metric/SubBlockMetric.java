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
        boolean result = false;
        String codePartString = null;
        while (it.hasNext()) {
            CodePart codeSubPart = it.next();
            codePartString = codeSubPart.toString();
            boolean metricApplicable = metric.applicable(codeSubPart);
            System.out.print("[" + codePartString+ " metric " +  metricApplicable + "]");
            if(metricApplicable){
                result = true;
                break;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return metric + " in " + codePartFactory.getClass().getSimpleName();
    }
}
