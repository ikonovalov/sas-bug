package com.luxoft.sas.bug;

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
        protected Metric createMetric() {
            return new AbstractRegExpMetric("((quit;)|(run;))\\s*\\S+\\s*%error_check");
        }
    },
    SQLPROC_JOINS {
        @Override
        public Metric createMetric() {
            return new SubBlockMetric(SQLProcCodePart.FACTORY, new AbstractRegExpMetric("(?i)(join\\W.*){5,}"));
        }
    },;

    private Metric metric;

    protected abstract Metric createMetric();

    /**
     * Get Metric cached instance.
     * @return
     */
    public Metric metric() {
        if (metric == null) {
            metric = createMetric();
        }
        return metric;
    }
}
