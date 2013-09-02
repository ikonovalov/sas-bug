package com.luxoft.sas.bug;

import com.luxoft.sas.bug.codepart.CodePart;
import com.luxoft.sas.bug.codepart.UserWritenCodePart;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ikonovalov
 * Date: 27.08.13
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class AppRG {

    private static final  CommandLineParser POSIX_PARSER = new PosixParser();

    private static final Options OPTIONS = new Options();

    public static final String FILE_SHORT = "f";

    public static final String METRIC_SINGLE_SHORT = "m";

    public static final String METRIC_ALL_SHORT = "M";

    static {
        OPTIONS.addOption(FILE_SHORT, "file", true, "SAS code file");
        OPTIONS.addOption(METRIC_SINGLE_SHORT, "metric", true, "Specific metric: " + Arrays.asList(Metrics.values()));
        OPTIONS.addOption(METRIC_ALL_SHORT, "allmetrics", false, "Apply all available metrics");
    }


    private static final int OUTPUT_MAX_WIDTH = 120;

    public static void main(String[] args) throws FileNotFoundException, ParseException {
        CommandLine cmd = POSIX_PARSER.parse(OPTIONS, args);

        if(!cmd.hasOption('f') || !(cmd.hasOption('m') || cmd.hasOption('M'))) {
            dumpHelp();
            return;
        }

        final File f = new File(cmd.getOptionValue('f'));

        final StringBuilder sb = FileLineIterator.asStringBuilder(f);

        // prepare required metrics
        final Metrics[] metrics = createMetrics(cmd);

        final Iterator<UserWritenCodePart> codeParts = UserWritenCodePart.FACTORY.getIterator(sb);

        // let's grab SAS code
        int totalCodeParts = 0;
        while (codeParts.hasNext()) {
            CodePart uwc = codeParts.next();
            System.out.print("* " + uwc);

            for (Metrics element : metrics) {
                System.out.print("\n\t\t");
                boolean applicable = element.applicable(uwc);
                System.out.print(element.name() + "=" + applicable + " ");
            }
            totalCodeParts++;
            System.out.println("\n");
        }
        System.out.println("Total code part handled: " + totalCodeParts);

    }

    private static Metrics[] createMetrics(CommandLine cmd) {
        Metrics[] metrics = null;
        if (cmd.hasOption('M')) {
            metrics = new Metrics[]{
                    Metrics.PREPROCESS,
                    Metrics.POSTPROCESS,
                    Metrics.GLOBAL,
                    Metrics.ERRORCHECK,
                    Metrics.SQLPROC_JOINS
            };
        } else if (cmd.hasOption('m')){
            metrics = new Metrics[]{Metrics.valueOf(cmd.getOptionValue('m'))};
        }
        return metrics;
    }

    private static void dumpHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(OUTPUT_MAX_WIDTH);
        formatter.printHelp(AppRG.class.getSimpleName(), OPTIONS);
    }
}
