package com.luxoft.sas.bug.checkstyle;

import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.SimpleCodePart;

/**
 * Проверка 1.4 "наличие в коде макроса %global  в  User Written"
 */
public class MacrosGlobalCheck extends UserWrittenWalker {

    private static final Metrics CHECK = Metrics.GLOBAL;

    public void visitToken(SimpleCodePart cp) {
        if (!CHECK.metric().applicable(cp)) {
            log(cp.getStart(), CHECK.name());
        }
    }
}
