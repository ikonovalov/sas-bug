package com.luxoft.sas.bug.checkstyle;

import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.SimpleCodePart;

/**
 * Проверка 1.2 "после каждого run; или quit; в User Written должны присутствовать %error_check"
 */
public class ErrorCheckCheck extends UserWrittenWalker {

    private static final Metrics CHECK = Metrics.ERRORCHECK;

    public void visitToken(SimpleCodePart cp) {
        if (!CHECK.metric().applicable(cp)) {
            log(cp.getStart(), CHECK.name());
        }
    }
}
