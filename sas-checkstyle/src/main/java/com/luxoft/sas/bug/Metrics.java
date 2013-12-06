package com.luxoft.sas.bug;

import com.luxoft.sas.bug.codepart.CodePart;
import com.luxoft.sas.bug.codepart.MacrosCodePart;
import com.luxoft.sas.bug.codepart.SQLProcCodePart;
import com.luxoft.sas.bug.metric.AbstractRegExpMetric;
import com.luxoft.sas.bug.metric.Metric;
import com.luxoft.sas.bug.metric.NestedCodePartMetric;

public enum Metrics {
    PREPROCESS("в коде User Written не хватает фразы Pre-Process") {
        @Override
        public Metric createMetric() {
            return new AbstractRegExpMetric("Pre-Process");
        }
    },
    POSTPROCESS("в коде User Written не хватает фразы Post-Process") {
        @Override
        public Metric createMetric() {
            return new AbstractRegExpMetric("Post-Process");
        }

    },
    GLOBAL("в коде User Written присутствует макрос %global") {
        @Override
        public Metric createMetric() {
            return new NestedCodePartMetric(MacrosCodePart.FACTORY, new AbstractRegExpMetric("%global"));
        }
    },
    ERRORCHECK("не хватает %error_check после run; или quit;") {
        @Override
        public Metric createMetric() {
            // todo: искать БЕЗ %error_check
            return new AbstractRegExpMetric("((quit;)|(run;))\\s*\\S+\\s*%error_check");
        }
    },
    SQLPROC_JOINS("наличие в User Written в рамках одной конструкции proc SQL; более 5 джойнов") {
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
     *
     */
    private String description;

    /**
     * Should return new fresh and "thread ready" metric
     * @return
     */
    protected abstract Metric createMetric();

    public String description() {
        return description;
    }

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

    private Metrics(String description) {
        this.description = description;
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
