package com.luxoft.sas.bug.checkstyle;

import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.SimpleCodePart;

/**
 * Проверка 1.4 "наличие в коде макроса %global  в  User Written"
 */
public class MacrosGlobalCheck extends UserWrittenWalker {

    private static final Metrics CHECK = Metrics.GLOBAL;

    public void visitToken(SimpleCodePart cp) {
        int startChar = CHECK.metric().applicable(cp);
        if (startChar >= 0) {
            log(cp.getStartLine() + cp.getLinesOffset(startChar), CHECK.name());
        }
    }
}
