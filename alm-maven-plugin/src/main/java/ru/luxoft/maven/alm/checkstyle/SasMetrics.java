package ru.luxoft.maven.alm.checkstyle;

import com.luxoft.sas.bug.FileLineIterator;
import com.luxoft.sas.bug.Metrics;
import com.luxoft.sas.bug.codepart.CodePart;
import com.luxoft.sas.bug.codepart.SimpleCodePart;
import com.luxoft.sas.bug.codepart.UserWrittenCodePart;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    CloseableHttpClient httpClient;
    String sessionCookie;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        //System.out.println("SasMetrics context: " + getPluginContext().get("") + " " + getPluginContext().get(""));

        Map<String, Map<Metrics, String>> results = testFiles();

        httpClient = HttpClients.createDefault();
        publishToALM(results, "102", "1010");
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Map<Metrics, String>> testFiles() throws MojoFailureException {
        Map<String, Map<Metrics, String>> results = new HashMap<String, Map<Metrics, String>>();

        //for (File f : sourceDir.listFiles()) {
        File f = new File(sourceDir + "/" + "202_002_Stream_DQ_DRVT.sas");

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
        //}

        return results;
    }

    private void publishToALM(Map<String, Map<Metrics, String>> results, String testSetId, String testConfigId) {
        login();
        startSession();
        String testId = getTestIdFromTestConfig(testConfigId);

        for (Map.Entry<String, Map<Metrics, String>> file: results.entrySet()) {
            Boolean result = true;
            for (String description : file.getValue().values()) {
                if (description != null) {
                    result = false;
                    break;
                }
            }
            String runId = addRun(testSetId, testId, testConfigId,  file.getKey(), result);
            for (Map.Entry<Metrics, String> metric : file.getValue().entrySet()) {
                log.fine("update step " + runId + " " + metric.getKey() + ": " + metric.getValue());
                updateRunStep(runId, metric.getKey(), metric.getValue());
            }
        }

        // todo: version control commit (if enabled)
        logout();
    }

    public String post(String serviceUrl, String xml) {
        String serviceEndpoint = encodeUrl(serviceUrl);
        try {
            HttpPost httppost = new HttpPost(serviceEndpoint);
            if (xml != null) {
                StringEntity ent = new StringEntity(xml, "UTF-8");
                ent.setContentType("application/xml");
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("post " + serviceEndpoint + "\n" + xml.replace("\n", ""));
                }
                httppost.setEntity(ent);
            } else {
                log.finest("post " + serviceEndpoint);
            }
            CloseableHttpResponse response = httpClient.execute(httppost);
            try {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.finest("post response " + result);
                return result;
            } finally {
                response.close();
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed POST " + serviceEndpoint, e);
        }
    }

    private String encodeUrl(String serviceUrl) {
        return "http://" + server + "/qcbin" + serviceUrl;
    }

    public String put(String serviceUrl, String xml) {
        String serviceEndpoint = encodeUrl(serviceUrl);
        try {
            HttpPut httpput = new HttpPut(serviceEndpoint);
            if (xml != null) {
                StringEntity ent = new StringEntity(xml, "UTF-8");
                ent.setContentType("application/xml");
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("put " + serviceEndpoint + "\n" + xml.replace("\n", ""));
                }
                httpput.setEntity(ent);
            } else {
                log.finest("put " + serviceEndpoint);
            }
            CloseableHttpResponse response = httpClient.execute(httpput);
            try {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.finest("put response " + result);
                return result;
            } finally {
                response.close();
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed PUT " + serviceEndpoint, e);
        }
    }

    private void login() {
        String serviceEndpoint = encodeUrl("/authentication-point/alm-authenticate");
        try {
            HttpPost httppost = new HttpPost(serviceEndpoint);
            StringEntity ent = new StringEntity(
                    "<alm-authentication>" +
                    "<user>" + almUser + "</user>" +
                    "<password>" + almPassword + "</password>" +
                    "</alm-authentication>", "UTF-8");
            httppost.setEntity(ent);
            CloseableHttpResponse response = httpClient.execute(httppost);
            try {
                Header[] hs = response.getHeaders("Set-Cookie");
                if (hs.length > 0) {
                    sessionCookie = hs[0].getValue();
                    log.fine("login success");
                }
            } finally {
                response.close();
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed POST " + serviceEndpoint, e);
        }
    }

    private String get(String serviceUrl) {
        String serviceEndpoint = encodeUrl(serviceUrl);
        try {
            HttpGet get = new HttpGet(serviceEndpoint);
            CloseableHttpResponse response = httpClient.execute(get);
            try {
                String result = EntityUtils.toString(response.getEntity());
                log.finest("get from " + serviceEndpoint + "\n" + result);
                return result;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed GET " + serviceEndpoint, e);
        }
    }

    private void logout() {
        get("/authentication-point/logout");
        sessionCookie = null;
        log.fine("logout");
    }

    private String addRun(String testSetId, String testId, String testConfigId, String testFile, Boolean result) {
        String testInstanceId = getTestInstanceId(testSetId, testConfigId, testId, testFile);
        String run = post("/rest/domains/" + domain + "/projects/" + project + "/runs",
                "<Entity Type=\"run\"><Fields>\n" +
                        "<Field Name=\"cycle-id\"><Value>" + testSetId + "</Value></Field>\n" +
                        "<Field Name=\"test-id\"><Value>" + testId + "</Value></Field>\n" +
                        "<Field Name=\"testcycl-id\"><Value>" + testInstanceId + "</Value></Field>\n" +
                        "<Field Name=\"test-config-id\"><Value>" + testConfigId + "</Value></Field>\n" +
                        "<Field Name=\"status\"><Value>" + (result ? "Passed" : "Failed") + "</Value></Field>\n" +
                        "<Field Name=\"name\"><Value>Check " + testFile + "</Value></Field>\n" +
                        "<Field Name=\"subtype-id\"><Value>hp.qc.run.MANUAL</Value></Field>\n" +
                        "<Field Name=\"owner\"><Value>" + almUser + "</Value></Field>\n" +
                        //"<Field Name=\"host\"><Value></Value></Field>\n" +
                        //"<Field Name=\"duration\"><Value>0</Value></Field>\n" +
                        "</Fields></Entity>");

        return getFieldValue(run, "id");
    }

    private void addRunStep(String runId, Metrics step, String testId, String description) {
        String stepName = step.name();
        String stepId = getDesignStepId(testId, stepName);
        post("/rest/domains/" + domain + "/projects/" + project + "/runs/" + runId + "/run-steps",
                "<Entity Type=\"run-step\"><Fields>\n" +
                        "<Field Name=\"desstep-id\"><Value>" + stepId + "</Value></Field>\n" +
                        "<Field Name=\"name\"><Value>" + stepName + "</Value></Field>\n" +
                        "<Field Name=\"status\"><Value>" + asStatus(description) + "</Value></Field>\n" +
                        "<Field Name=\"test-id\"><Value>" + testId + "</Value></Field>\n" +
                        "<Field Name=\"parent-id\"><Value>" + runId + "</Value></Field>\n" +
                        "</Fields></Entity>");
    }

    private void updateRunStep(String runId, Metrics step, String description) {
        String stepName = step.name();
        String runStepId = getRunStepId(runId, stepName);
        if (runStepId == null) return; // todo
        put("/rest/domains/" + domain + "/projects/" + project + "/runs/" + runId + "/run-steps/" + runStepId,
                "<Entity Type=\"run-step\"><Fields>\n" +
                        "<Field Name=\"status\"><Value>" + asStatus(description) + "</Value></Field>\n" +
                        (description == null ? "" : "<Field Name=\"description\"><Value>" + description + "</Value></Field>\n") +
                        "</Fields></Entity>");
    }

    private String asStatus(String description) {
        return description == null ? "Passed" : "Failed";
    }

    private String getFieldValue(String entity, String fieldName) throws IllegalStateException {
        Pattern p = Pattern.compile("<Field Name=\"" + fieldName + "\"><Value>(\\d*)</Value></Field>");
        Matcher m = p.matcher(entity.replace("\n", ""));
        if (m.find()) {
            return m.group(1);
        } else {
            throw new IllegalStateException(fieldName + " not found in entity");
        }
    }

    private void startSession() {
        post("/rest/site-session", null);
    }

    private String getTestIdFromTestConfig(String testConfigId) {
        String testConfig = get("/rest/domains/" + domain + "/projects/" + project + "/test-configs/" + testConfigId
                + "?fields=parent-id");
        return getFieldValue(testConfig, "parent-id");
    }

    private String getRunStepId(String runId, String name) {
        String runStep = get("/rest/domains/" + domain + "/projects/" + project + "/runs/" + runId
                + "/run-steps?" + asQuery("name[" + name + "]") + "&fields=id");
        try {
            return getFieldValue(runStep, "id");
        } catch (IllegalStateException e) {
            log.warning("Step '" + name + " not found in run with id=" + runId);
            return null;
        }
    }

    private String getDesignStepId(String testId, String name) {
        String runStep = get("/rest/domains/" + domain + "/projects/" + project
                + "/design-steps?" + asQuery("name[" + name + "];parent-id[" + testId + "]") + "&fields=id");
        return getFieldValue(runStep, "id");
    }

    private String asQuery(String query) {
        try {
            return "query=" + URLEncoder.encode("{" + query + "}", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported UTF-8 Encoding", e);
        }
    }

    private String getTestInstanceId(String testSetId, String testConfigId, String testId, String fileName) {
        String runStep = get("/rest/domains/" + domain + "/projects/" + project + "/test-instances?"
                + asQuery("cycle-id[" + testSetId + "];test-config-id[" + testConfigId + "];test-id[" + testId + "]") + "&fields=id");

        // todo: find TestInstance by fileName

        try {
            return getFieldValue(runStep, "id");
        } catch (IllegalStateException e) {
            return addTestInstance(testSetId, testConfigId, testId, fileName);
        }
    }

    private String addTestInstance(String testSetId, String testConfigId, String testId, String fileName) {
        String run = post("/rest/domains/" + domain + "/projects/" + project + "/test-instances",
                "<Entity Type=\"test-instance\"><Fields>\n" +
                "<Field Name=\"test-config-id\"><Value>" + testConfigId + "</Value></Field>\n" +
                "<Field Name=\"cycle-id\"><Value>" + testSetId + "</Value></Field>\n" +
                "<Field Name=\"actual-tester\"><Value>" + almUser + "</Value></Field>\n" +
                "<Field Name=\"test-id\"><Value>" + testId + "</Value></Field>\n" +
                "<Field Name=\"owner\"><Value>" + almUser + "</Value></Field>\n" +
                "<Field Name=\"subtype-id\"><Value>hp.qc.test-instance.MANUAL</Value></Field>\n" +
                "<Field Name=\"test-order\"><Value>1</Value></Field>\n" +
                "</Fields></Entity>");

        return getFieldValue(run, "id");
    }

}
