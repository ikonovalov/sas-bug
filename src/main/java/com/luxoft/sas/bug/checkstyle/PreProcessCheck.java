package com.luxoft.sas.bug.checkstyle;

import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.SimpleCodePart;

/**
 * Проверка 1.1 "наличие в коде фразы Pre-Process в User Written"
 */
public class PreProcessCheck extends UserWrittenWalker {

    private static final Metrics CHECK = Metrics.PREPROCESS;

    public void visitToken(SimpleCodePart cp) {
        int startChar = CHECK.metric().applicable(cp);
        if (startChar < 0) {
            log(cp.getStartLine(), CHECK.name());
        }
    }
}
