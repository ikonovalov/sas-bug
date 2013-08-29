package com.luxoft.sas.bug;

import com.luxoft.sas.bug.metric.Metrics;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ikonovalov
 * Date: 27.08.13
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class AppRG {

    private static final Options OPTIONS = new Options();
    static {
        OPTIONS.addOption("f", "file", true, "SAS code file");
    }

    public static void main(String[] args) throws FileNotFoundException, ParseException {
        // check parameters
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( AppRG.class.getSimpleName(), OPTIONS );
            return;
        }

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(OPTIONS, args);

        File f = new File(cmd.getOptionValue('f'));

        StringBuilder sb = FileLineIterator.asStringBuilder(f);

        Iterator<UserWritenCodePart> codeParts = UserWritenCodePart.getIterator(sb);

        CharSequence a = null;
        while (codeParts.hasNext()) {
            CodePart uwc = codeParts.next();

            Metrics[] metrics = new Metrics[] {
                    Metrics.PREPROCESS,
                    Metrics.POSTPROCESS,
                    Metrics.GLOBAL,
                    Metrics.ERRORCHECK
            };
            for (Metrics element : metrics) {
                boolean applicable = element.metric().applicable(uwc);
                System.out.print(" -> " + element.name() + "=" + applicable);
            }

            a = uwc.getCodeContent();

            System.out.println();
        }

        //System.out.println(a);

    }
}
