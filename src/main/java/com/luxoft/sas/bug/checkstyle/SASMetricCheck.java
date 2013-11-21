package com.luxoft.sas.bug.checkstyle;

import com.luxoft.sas.bug.FileLineIterator;
import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.UserWritenCodePart;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

/**
 * Checkstyle Check for verify SAS metric.
 */
public class SASMetricCheck extends AbstractFileSetCheck {

    private String metric;

    /**
     * @param metric metric name {@link Metrics}
     */
    public void setMetric(String metric) {
        this.metric = metric;
    }

    @Override
    protected void processFiltered(File file, List<String> strings) {

        if (metric == null) throw new IllegalArgumentException("metric not specified");
        Metrics check = Metrics.valueOf(metric.trim().toUpperCase());
        if (check == null) throw new IllegalArgumentException("metric '" + metric + "' not found. See " + Metrics.class.getName());

        StringBuilder sb = null;
        try {
            sb = FileLineIterator.asStringBuilder(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("can't read file", e);
        }

        Iterator<UserWritenCodePart> codeParts = UserWritenCodePart.FACTORY.getIterator(sb);

        while (codeParts.hasNext()) {
            UserWritenCodePart uwc = codeParts.next();
            if (!check.metric().applicable(uwc)) {
                log(uwc.getStart(), check.name());
            }
        }

    }
}
