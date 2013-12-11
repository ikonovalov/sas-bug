package ru.luxoft.maven.alm.checkstyle;

import com.luxoft.sas.bug.FileLineIterator;
import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.CodePart;
import com.luxoft.sas.bug.codepart.SimpleCodePart;
import com.luxoft.sas.bug.codepart.UserWrittenCodePart;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import ru.luxoft.maven.alm.checkstyle.server.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Test sas files for all metrics.
 */
@Mojo(name="sas-metrics", defaultPhase = LifecyclePhase.TEST)
public class SasMetrics extends AbstractMojo {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Parameter( property = "alm.server", defaultValue = "localhost:8080" )
    private String server = "localhost:8082";

    String almUser = "admin";
    String almPassword = "admin";

    @Parameter( property = "alm.project", defaultValue = "TEST1" )
    private String project = "TEST1";

    @Parameter(property = "alm.domain", defaultValue = "DEFAULT")
    private String domain = "DEFAULT";

    /**
     * Location of the file.
     */
    @Parameter(property = "sas.sources", defaultValue = "${project.build.directory}", required = true )
    private File sourceDir = new File("C:\\dev\\code\\proj1\\src\\main\\sas");

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        //System.out.println("SasMetrics context: " + getPluginContext().get("") + " " + getPluginContext().get(""));

        Map<String, Map<Metrics, String>> results = testFiles();

        try {
            ALMServer almServer = new ALMServer(server);
            almServer.login(almUser, almPassword);
            almServer.connect(domain, project);
            publishToALM(almServer.getTestRunFactory(), "104", "9", results);
            almServer.disconnect();
            almServer.logout();
            almServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Map<String, Map<Metrics, String>> testFiles() throws MojoFailureException {
        Map<String, Map<Metrics, String>> results = new HashMap<String, Map<Metrics, String>>();

        for (File f : sourceDir.listFiles()) {

            log.fine("test file: " + f.getName());

            Map<Metrics, String> fileResult = new HashMap<Metrics, String>();

            final StringBuilder sb;
            try {
                sb = FileLineIterator.asStringBuilder(f);
            } catch (FileNotFoundException e) {
                throw new MojoFailureException("file not found " + f.getAbsolutePath());
            }
            final Iterator<UserWrittenCodePart> codeParts = UserWrittenCodePart.FACTORY.getIterator(new SimpleCodePart(sb));

            Metrics[] metrics =  new Metrics[]{Metrics.ERRORCHECK,Metrics.GLOBAL,Metrics.SQLPROC_JOINS,
                    Metrics.POSTPROCESS,Metrics.PREPROCESS};

            for (Metrics metric : metrics) {
                fileResult.put(metric, null);
            }

            while (codeParts.hasNext()) {
                CodePart uwc = codeParts.next();
                for (Metrics element : metrics) {
                    int applicable = element.applicable(uwc);
                    if (applicable > 0) {
                        String description = fileResult.get(element);
                        if (description == null) description = element.description();
                        description += "\nСтрока " + applicable;
                        fileResult.put(element, description);
                        log.fine("error " + element.name() + " " + applicable);
                    }
                }
            }
            results.put(f.getName(), fileResult);
        }

        return results;
    }

    private void publishToALM(TestRunFactory rf, String testSetId, String testId,
                              Map<String, Map<Metrics, String>> results) {

        String runId = rf.addRun(testSetId, testId, "SasMetrics " + SimpleDateFormat.getDateTimeInstance().format(new Date()), null);
        boolean runResult = true;
        for (Map.Entry<String, Map<Metrics, String>> file: results.entrySet()) {
            StringBuffer description = new StringBuffer();
            for (String desc : file.getValue().values()) {
                if (desc != null && desc.length() > 0) {
                    if (description.length() > 0) description.append("\n");
                    description.append(desc);
                    runResult = false;
                }
            }

            boolean passed = description.length() == 0;
            rf.addRunStep(runId, file.getKey(), passed, passed ? null : description.toString());

            //for (Map.Entry<Metrics, String> metric : file.getValue().entrySet()) {
            //    log.fine("run " + runId + " step " + metric.getKey() + ":\t" + metric.getValue());
            //    rf.addRunStep(runId, metric.getKey().name(), metric.getValue() != null, metric.getValue());
            //}
        }
        rf.updateRunResult(runId, runResult);
    }


}
