package com.luxoft.sas.bug;

import com.luxoft.sas.bug.codepart.CodePart;
import com.luxoft.sas.bug.codepart.MacrosCodePart;
import com.luxoft.sas.bug.codepart.SQLProcCodePart;
import com.luxoft.sas.bug.metric.AbstractRegExpMetric;
import com.luxoft.sas.bug.metric.Metric;
import com.luxoft.sas.bug.metric.NestedCodePartMetric;

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
            return new NestedCodePartMetric(MacrosCodePart.FACTORY, new AbstractRegExpMetric("%global"));
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
            return new NestedCodePartMetric(SQLProcCodePart.FACTORY, new AbstractRegExpMetric("(?i)(join\\W.*){6,}"));
        }
    };

    /**
     * @deprecated since 1.1
     */
    private Metric metric;

    /**
     * Should return new fresh and "thread ready" metric
     * @return
     */
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
     * Shorthand for Metrics.createMetric().applicable(com.luxoft.sas.bug.codepart.CodePart codePart);
     * @param codePart checking by this metric
     * @return start Metric char, -1 if Metric not encountered.
     */
    public int applicable(CodePart codePart) {
        return createMetric().applicable(codePart);
    }
}
