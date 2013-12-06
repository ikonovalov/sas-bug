package com.luxoft.sas.bug.checkstyle;

import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.SimpleCodePart;

/**
 * Проверка 1.2 "после каждого run; или quit; в User Written должны присутствовать %error_check".
 */
public class ErrorCheckCheck extends UserWrittenWalker {

    private static final Metrics CHECK = Metrics.ERRORCHECK;

    @Override
    public void visitToken(SimpleCodePart cp) {
        int startChar = CHECK.metric().applicable(cp);
        if (startChar >= 0) {
            log(cp.getStartLine() + cp.getLinesOffset(startChar), CHECK.name());
        }
    }
}
