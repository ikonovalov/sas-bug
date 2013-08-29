package com.luxoft.sas.bug.metric;

import com.luxoft.sas.bug.Metric;

public enum Metrics {
    PREPROCESS {
        @Override
        public Metric createMetric() {
            return new AbstractRegExpMetric() {
                @Override
                public String getRegExp() {
                    return "Pre-Process";
                }
            };
        }
    },
    POSTPROCESS {
        @Override
        public Metric createMetric() {
            return new AbstractRegExpMetric() {
                @Override
                public String getRegExp() {
                    return "Post-Process";
                }
            };
        }

    },
    GLOBAL {
        @Override
        public Metric createMetric() {
            return new AbstractRegExpMetric() {
                @Override
                public String getRegExp() {
                    return "%global";
                }
            };
        }
    },
    ERRORCHECK {
        @Override
        protected Metric createMetric() {
            return new AbstractRegExpMetric() {
                @Override
                public String getRegExp() {
                    return "((quit;)|(run;))\\s*\\S+\\s*%error_check";
                }
            };
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
