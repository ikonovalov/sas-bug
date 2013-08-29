package com.luxoft.sas.bug.metric;

import com.luxoft.sas.bug.codepart.MacrosCodePart;

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
    };

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
