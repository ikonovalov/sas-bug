package ru.luxoft.maven.alm.checkstyle;

import com.luxoft.sas.bug.FileLineIterator;
import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.CodePart;
import com.luxoft.sas.bug.codepart.SimpleCodePart;
import com.luxoft.sas.bug.codepart.UserWrittenCodePart;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import ru.luxoft.maven.alm.checkstyle.server.ALMServer;
import ru.luxoft.maven.alm.checkstyle.server.TestRunFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Test sas files for all metrics.
 */
@Mojo(name="sas-metrics", defaultPhase = LifecyclePhase.TEST)
public class SasMetrics extends AbstractMojo {

    private Log log;

    @Parameter(required = true)
    private String server;

    @Parameter(required = true)
    private String user;

    @Parameter(required = true)
    private String password;

    @Parameter(required = true)
    private String project;

    @Parameter(required = true)
    private String domain;

    @Parameter
    private String testSetId;

    @Parameter
    private String testId;

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.basedir}", required = true)
    private File src;

    @Override
    public void setLog(Log log) {
        super.setLog(log);
        this.log = log;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public File getSrc() {
        return src;
    }

    public void setSrc(File src) {
        this.src = src;
    }

    public String getTestSetId() {
        return testSetId;
    }

    public void setTestSetId(String testSetId) {
        this.testSetId = testSetId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Map<String, Map<Metrics, String>> results = testFiles("", new File[] {src});

        try {
            ALMServer almServer = new ALMServer(server);
            almServer.login(user, password);
            almServer.connect(domain, project);
            publishToALM(almServer.getTestRunFactory(), testSetId, testId, results);
            almServer.disconnect();
            almServer.logout();
            almServer.close();
        } catch (IOException e) {
            log.error(e);
        }

    }

    private Map<String, Map<Metrics, String>> testFiles(String pathPrefix, File[] files) throws MojoFailureException {
        Map<String, Map<Metrics, String>> results = new HashMap<String, Map<Metrics, String>>();

        for (File f : files) {
            if (f.isFile()) {
                Map<Metrics, String> fileResult = testFile(f);
                results.put(pathPrefix + "/" + f.getName(), fileResult);
            } else {
                Map<String, Map<Metrics, String>> innerResults =
                        testFiles(pathPrefix + "/" + f.getName(), f.listFiles());
                results.putAll(innerResults);
            }
        }

        return results;
    }

    private Map<Metrics, String> testFile(File f) throws MojoFailureException {
        log.debug("test file: " + f.getName());

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
                    log.debug("find error " + element.name() + " " + applicable);
                }
            }
        }
        return fileResult;
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
            rf.addRunStep(runId, xmlEncode(file.getKey()), passed, passed ? null : description.toString());

            //for (Map.Entry<Metrics, String> metric : file.getValue().entrySet()) {
            //    log.fine("run " + runId + " step " + metric.getKey() + ":\t" + metric.getValue());
            //    rf.addRunStep(runId, metric.getKey().name(), metric.getValue() != null, metric.getValue());
            //}
        }
        rf.updateRunResult(runId, runResult);
    }

    private String xmlEncode(String string) {
        return string.replace("<", ".").replace(">","."); // todo
    }


}
