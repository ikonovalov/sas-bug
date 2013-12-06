import ru.luxoft.maven.alm.checkstyle.SasMetrics;

import java.util.logging.*;

public class SasMetricsTest {

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger(SasMetrics.class.getName());
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getLevel().getName() + ": " + record.getMessage() + "\n";
            }
        });
        logger.addHandler(handler);

        SasMetrics s = new SasMetrics();
        s.execute();
    }


}
