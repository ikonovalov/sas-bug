package com.luxoft.sas.bug;

import com.luxoft.sas.bug.metric.Metrics;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;


public class AppRG {

    private static final Options OPTIONS = new Options();
    static {
        OPTIONS.addOption("f", true, "SAS code file");
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

        // start and end UserWriten writer
        Matcher startMatcher = UserWritenCodePart.getMatcher(sb, UserWritenCodePart.POSITION.START);
        Matcher endMatcher = UserWritenCodePart.getMatcher(sb, UserWritenCodePart.POSITION.END);

        CharSequence a = null;
        while (startMatcher.find()) {
            int startIndex = startMatcher.start();
            System.out.print(startIndex);
            boolean endFound = endMatcher.find(startIndex); //search from start index
            if (endFound) {
                int endIndex = endMatcher.end();

                UserWritenCodePart uwc = new UserWritenCodePart(sb.substring(startIndex, endIndex));

                Metrics[] metrics = new Metrics[]{
                        Metrics.PREPROCESS,
                        Metrics.POSTPROCESS,
                        Metrics.GLOBAL,
                        Metrics.ERRORCHECK
                };
                for (Metrics element : metrics) {
                    boolean applicable = element.metric().applicable(uwc);
                    System.out.print(" -> " + element.name() + "=" + applicable);
                }

                System.out.print(" -> " + endIndex);

                a = uwc.getCodeContent();
            } else {
                throw new IllegalStateException("End statement not found!");
            }
            System.out.println();


        }

        //System.out.println(a);

    }
}
