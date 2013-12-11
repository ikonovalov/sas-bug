package ru.luxoft.maven.alm.checkstyle.server;

/**
 * Test runs creator.
 */
public class TestRunFactory {

    private ALMServer server;

    public TestRunFactory(ALMServer server) {
        this.server = server;
    }

    public String addRun(String testSetId, String testId, String runName, Boolean result) {
        if (testSetId == null) throw new IllegalArgumentException("testSetId can't be null");
        if (testId == null) throw new IllegalArgumentException("testId can't be null");

        String testInstanceId = getTestInstance(testSetId, testId);
        if (testInstanceId == null) testInstanceId = addTestInstance(testSetId, testId);

        String run = server.post("/runs",
                "<Entity Type=\"run\"><Fields>\n" +
                "<Field Name=\"cycle-id\"><Value>" + testSetId + "</Value></Field>\n" +
                "<Field Name=\"testcycl-id\"><Value>" + testInstanceId + "</Value></Field>\n" +
                "<Field Name=\"test-id\"><Value>" + testId + "</Value></Field>\n" +
                "<Field Name=\"status\"><Value>" + (result == null ? "No Run" : result ? "Passed" : "Failed") + "</Value></Field>\n" +
                "<Field Name=\"name\"><Value>" + runName + "</Value></Field>\n" +
                "<Field Name=\"subtype-id\"><Value>hp.qc.run.MANUAL</Value></Field>\n" +
                "<Field Name=\"owner\"><Value>" + server.getUser() + "</Value></Field>\n" +
                //"<Field Name=\"test-config-id\"><Value>" + testConfigId + "</Value></Field>\n" +
                //"<Field Name=\"host\"><Value></Value></Field>\n" +
                //"<Field Name=\"duration\"><Value>0</Value></Field>\n" +
                "</Fields></Entity>");

        return ALMUtils.getXMLEntityField(run, "id");
    }

    public void updateRunResult(String runId, Boolean result) {
        if (runId == null) throw new IllegalArgumentException("runId can't be null");

        server.put("/runs/" + runId,
                "<Entity Type=\"run\"><Fields>\n" +
                "<Field Name=\"status\"><Value>" + (result == null ? "No Run" : result ? "Passed" : "Failed") + "</Value></Field>\n" +
                "</Fields></Entity>");
    }

    public void addRunStep(String runId, String stepName, Boolean result, String description) {
        if (runId == null) throw new IllegalArgumentException("runId can't be null");

        String testId = getEntityField("/runs/" + runId, "test-id");
        String stepId = testId == null ? null : getDesignStepId(testId, stepName);
        server.post("/runs/" + runId + "/run-steps",
                "<Entity Type=\"run-step\"><Fields>\n" +
                "<Field Name=\"parent-id\"><Value>" + runId + "</Value></Field>\n" +
                "<Field Name=\"name\"><Value>" + stepName + "</Value></Field>\n" +
                "<Field Name=\"status\"><Value>" + (result == null ? "No Run" : result ? "Passed" : "Failed") + "</Value></Field>\n" +
                (description == null ? "" : "<Field Name=\"description\"><Value>" + description + "</Value></Field>\n") +
                (testId == null ? "" : "<Field Name=\"test-id\"><Value>" + testId + "</Value></Field>\n") +
                (stepId == null ? "" : "<Field Name=\"desstep-id\"><Value>" + stepId + "</Value></Field>\n") +
                "</Fields></Entity>");
    }

    public void updateRunStep(String runId, String stepName, Boolean result, String description) {
        String runStepId = getRunStepId(runId, stepName);
        if (runStepId == null) throw new IllegalStateException("RunStep '" + stepName + "' not found in TestRun[" + runId + "]");
        server.put("/runs/" + runId + "/run-steps/" + runStepId,
                "<Entity Type=\"run-step\"><Fields>\n" +
                "<Field Name=\"status\"><Value>" + (result == null ? "No Run" : result ? "Passed" : "Failed") + "</Value></Field>\n" +
                (description == null ? "" : "<Field Name=\"description\"><Value>" + description + "</Value></Field>\n") +
                "</Fields></Entity>");
    }

    public String addTestInstance(String testSetId, String testId) {
        if (testSetId == null) throw new IllegalArgumentException("testSetId can't be null");
        if (testId == null) throw new IllegalArgumentException("testConfigId can't be null");

        String run = server.post("/test-instances",
                "<Entity Type=\"test-instance\"><Fields>\n" +
                "<Field Name=\"cycle-id\"><Value>" + testSetId + "</Value></Field>\n" +
                "<Field Name=\"test-id\"><Value>" + testId + "</Value></Field>\n" +
                //"<Field Name=\"test-config-id\"><Value>" + testConfigId + "</Value></Field>\n" +
                "<Field Name=\"owner\"><Value>" + server.getUser() + "</Value></Field>\n" +
                "<Field Name=\"actual-tester\"><Value>" + server.getUser() + "</Value></Field>\n" +
                "<Field Name=\"subtype-id\"><Value>hp.qc.test-instance.MANUAL</Value></Field>\n" +
                "<Field Name=\"test-order\"><Value>1</Value></Field>\n" +
                "</Fields></Entity>");

        return ALMUtils.getXMLEntityField(run, "id");
    }

    public String getTestInstance(String testSetId, String testId) {
        return getEntityField("/test-instances?" + ALMUtils.asQuery(
                "cycle-id[" + testSetId + "];test-id[" + testId + "]"), "id");
    }

    public String getRunStepId(String runId, String name) {
        return getEntityField("/runs/" + runId + "/run-steps?" + ALMUtils.asQuery(
                "name[" + name + "]"), "id");
    }

    public String getDesignStepId(String testId, String name) {
        return getEntityField("/design-steps?" + ALMUtils.asQuery(
                "name[" + name + "];parent-id[" + testId + "]"), "id");
    }

    protected String getEntityField(String entityUrl, String fieldName) {
        if (entityUrl == null) throw new IllegalArgumentException("entityUrl can't be null");
        if (fieldName == null) throw new IllegalArgumentException("fieldName can't be null");

        String entity = server.get(entityUrl + ( entityUrl.contains("?") ? "&" :"?" ) + "fields=" + fieldName);
        return ALMUtils.getXMLEntityField(entity, fieldName);
    }
}
