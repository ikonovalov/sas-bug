package com.luxoft.sas.bug;

import com.luxoft.sas.bug.codepart.CodePart;
import com.luxoft.sas.bug.codepart.UserWritenCodePart;
import com.luxoft.sas.bug.metric.Metrics;

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

    public static void main(String[] args) throws FileNotFoundException {
        File f = new File(args[0]);

        StringBuilder sb = FileLineIterator.asStringBuilder(f);

        Iterator<? extends CodePart> codeParts = UserWritenCodePart.FACTORY.getIterator(sb);

        CharSequence a = null;
        while (codeParts.hasNext()) {
            CodePart uwc = codeParts.next();
            System.out.print(uwc);

            Metrics[] metrics = new Metrics[] {
                    Metrics.PREPROCESS,
                    Metrics.POSTPROCESS,
                    Metrics.GLOBAL,
                    Metrics.ERRORCHECK
            };
            for (Metrics element : metrics) {
                boolean applicable = element.metric().applicable(uwc);
                System.out.print(" -> " + element.name() + "=" + applicable + " ");
            }

            a = uwc.getCodeContent();

            System.out.println();
        }

        //System.out.println(a);

    }
}
