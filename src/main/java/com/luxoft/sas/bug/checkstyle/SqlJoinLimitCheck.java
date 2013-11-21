package com.luxoft.sas.bug.checkstyle;

import com.luxoft.sas.bug.codepart.SQLProcCodePart;
import com.luxoft.sas.bug.codepart.SimpleCodePart;
import com.luxoft.sas.bug.metric.AbstractRegExpMetric;
import com.luxoft.sas.bug.metric.SubBlockMetric;

/**
 * Проверка 1.5 "наличие в User Written в рамках одной  конструкции proc SQL;     quit; более 5 джойнов"
 */
public class SqlJoinLimitCheck extends UserWrittenWalker {

    private int max = 5;

    public void setMax(int max) {
        this.max = max;
    }

    public void visitToken(SimpleCodePart cp) {
        // modified Metrics.SQLPROC_JOINS
        SubBlockMetric metric = new SubBlockMetric(SQLProcCodePart.FACTORY, new AbstractRegExpMetric("(?i)(join\\W.*){" + (max + 1) + ",}"));
        if (!metric.applicable(cp)) {
            log(cp.getStart(), "sql.join.limit", max);
        }
    }
}
