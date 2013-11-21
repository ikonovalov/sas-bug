package com.luxoft.sas.bug.checkstyle;

import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.SimpleCodePart;

/**
 * Проверка 1.1 "наличие в коде фразы Post-Process в User Written"
 */
public class PostProcessCheck extends UserWrittenWalker {

    private static final Metrics CHECK = Metrics.POSTPROCESS;

    public void visitToken(SimpleCodePart cp) {
        if (!CHECK.metric().applicable(cp)) {
            log(cp.getStart(), CHECK.name());
        }
    }
}
