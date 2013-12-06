package com.luxoft.sas.bug.metric;

import com.luxoft.sas.bug.codepart.CodePart;

public interface Metric {

    /**
     * Найти метрику.
     *
     * @param part
     * @return первый символ найденной метрики или -1 если метрика не найдена
     */
    public int applicable(CodePart part);

}
