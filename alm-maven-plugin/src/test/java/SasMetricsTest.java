import org.apache.maven.plugin.logging.SystemStreamLog;
import ru.luxoft.maven.alm.checkstyle.SasMetrics;

import java.io.File;
import java.util.logging.*;

public class SasMetricsTest {

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger("ru.luxoft");
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
        s.setLog(new SystemStreamLog());
        s.setDomain("DEFAULT");
        s.setProject("TEST1");
        s.setServer("localhost:8082");
        s.setUser("admin");
        s.setPassword("admin");
        s.setSrc(new File("C:\\dev\\code\\proj1\\src\\main"));
        s.setTestSetId("104");
        s.setTestId("9");
        s.execute();
    }


}
