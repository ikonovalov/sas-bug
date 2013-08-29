package com.luxoft.sas.bug.metric;

import com.luxoft.sas.bug.codepart.CodePart;

public interface Metric {

    public boolean applicable(CodePart part);

}
