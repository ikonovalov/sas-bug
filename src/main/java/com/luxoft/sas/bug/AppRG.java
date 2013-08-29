package com.luxoft.sas.bug;

import com.luxoft.sas.bug.metric.Metrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;

/**
 * Created with IntelliJ IDEA.
 * User: ikonovalov
 * Date: 27.08.13
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class AppRG {

    public static void main(String[] args) throws FileNotFoundException {
        File f = new File(args[0]);

        StringBuilder sb = FileLineIterator.asStringBuilder(f);

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

                System.out.print(" -> " + endIndex);

                a =uwc.getCodeContent();
            } else {
                throw new IllegalStateException("End statement not found!");
            }
            System.out.println();


        }

        //System.out.println(a);

    }
}
