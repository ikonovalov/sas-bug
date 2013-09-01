package com.luxoft.sas.bug;

import com.luxoft.sas.bug.codepart.CodePart;
import com.luxoft.sas.bug.codepart.MacrosCodePart;
import com.luxoft.sas.bug.codepart.SQLProcCodePart;
import com.luxoft.sas.bug.metric.AbstractRegExpMetric;
import com.luxoft.sas.bug.metric.Metric;
import com.luxoft.sas.bug.metric.SubBlockMetric;

public enum Metrics {
    PREPROCESS {
        @Override
        public Metric createMetric() {
            return new AbstractRegExpMetric("Pre-Process");
        }
    },
    POSTPROCESS {
        @Override
        public Metric createMetric() {
            return new AbstractRegExpMetric("Post-Process");
        }

    },
    GLOBAL {
        @Override
        public Metric createMetric() {
            return new SubBlockMetric(MacrosCodePart.FACTORY, new AbstractRegExpMetric("%global"));
        }
    },
    ERRORCHECK {
        @Override
        public Metric createMetric() {
            return new AbstractRegExpMetric("((quit;)|(run;))\\s*\\S+\\s*%error_check");
        }
    },
    SQLPROC_JOINS {
        @Override
        public Metric createMetric() {
            return new SubBlockMetric(SQLProcCodePart.FACTORY, new AbstractRegExpMetric("(?i)(join\\W.*){5,}"));
        }
    };

    /**
     * @deprecated since 1.1
     */
    private Metric metric;

    protected abstract Metric createMetric();

    /**
     * @deprecated since 1.1
     * Get Metric cached instance.
     * @return
     */
    public Metric metric() {
        if (metric == null) {
            metric = createMetric();
        }
        return metric;
    }

    /**
     * Shorthande for Metrics.createMetric().applicable(com.luxoft.sas.bug.codepart.CodePart codePart);
     * @param codePart checking by this metric
     * @return true - Metric found, false - Metric not encountered.
     */
    public boolean applicable(CodePart codePart) {
        return createMetric().applicable(codePart);
    }
}
