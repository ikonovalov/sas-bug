package com.luxoft.sas.bug.checkstyle;

import com.luxoft.sas.bug.FileLineIterator;
import com.luxoft.sas.bug.codepart.SimpleCodePart;
import com.luxoft.sas.bug.codepart.UserWrittenCodePart;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

/**
 * Checkstyle Check for verify SAS metric.
 */
public abstract class UserWrittenWalker extends AbstractFileSetCheck {

    @Override
    protected void processFiltered(File file, List<String> strings) {
        StringBuilder sb = null;
        try {
            sb = FileLineIterator.asStringBuilder(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("can't read file", e);
        }

        Iterator<UserWrittenCodePart> codeParts = UserWrittenCodePart.FACTORY.getIterator(new SimpleCodePart(sb));

        while (codeParts.hasNext()) {
            visitToken(codeParts.next());
        }
    }

    public abstract void visitToken(SimpleCodePart cp);
}
