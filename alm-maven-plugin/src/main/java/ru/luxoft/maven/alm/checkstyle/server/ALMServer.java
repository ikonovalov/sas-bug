package ru.luxoft.maven.alm.checkstyle.server;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ALM Endpoint.
 */
public class ALMServer implements Closeable {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private String server;

    protected CloseableHttpClient httpClient;

    private String domain;

    private String project;

    private String user;

    public ALMServer(String server) {
        this.server = server;
        httpClient = HttpClients.createDefault();
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

    @Override
    public void close() throws IOException {
        try {
            httpClient.close();
        } catch (IOException e) {
            log.warning(e.getLocalizedMessage());
        }
    }

    private String execute(HttpUriRequest request) {
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            throw new IllegalStateException("Failed call " + request.getMethod() + " request to " + request.getURI(), e);
        }
        try {
            if (response.getStatusLine().getStatusCode() >= 400) {
                try {
                    log.severe(EntityUtils.toString(response.getEntity(), "UTF-8"));
                } catch (IOException e) {
                    log.warning(e.getLocalizedMessage());
                }
                throw new IllegalArgumentException(response.getStatusLine() + " (" + request.getMethod() + " to " + request.getURI() + ")");
            }

            String result;
            try {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                throw new IllegalStateException("Failed get result of " + request.getMethod() + " request to " + request.getURI(), e);
            }

            if (log.isLoggable(Level.FINEST)) {
                log.finest(request.getMethod() + " " + request.getURI() + (result.length() > 0 ? "\n" + result.replace("\n","") : ""));
            }

            return result;

        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.warning(e.getLocalizedMessage());
            }
        }
    }

    public void login(String user, String password) {
        String serviceEndpoint = "http://" + server + "/qcbin/authentication-point/alm-authenticate";
        HttpPost httppost = new HttpPost(serviceEndpoint);
        StringEntity ent;
        try {
            ent = new StringEntity(
                    "<alm-authentication>" +
                    "<user>" + user + "</user>" +
                    "<password>" + password + "</password>" +
                    "</alm-authentication>", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        httppost.setEntity(ent);
        execute(httppost);
        log.fine("login success");
        this.user = user;
    }

    public void logout() {
        String serviceEndpoint = "http://" + server + "/qcbin/authentication-point/logout";
        execute(new HttpGet(serviceEndpoint));
        log.fine("logout");
        this.user = null;
    }

    private String fullEntityUrl(String entityUrl) {
        return "http://" + server + "/qcbin/rest/domains/" + domain + "/projects/" + project + entityUrl;
    }

    protected String post(String serviceUrl, String xml) {
        String serviceEndpoint = fullEntityUrl(serviceUrl);
        HttpPost httppost = new HttpPost(serviceEndpoint);
        if (xml != null) {
            StringEntity ent = null;
            try {
                ent = new StringEntity(xml, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            ent.setContentType("application/xml");
            if (log.isLoggable(Level.FINEST)) {
                log.finest("post " + serviceEndpoint + "\n" + xml.replace("\n", ""));
            }
            httppost.setEntity(ent);
        } else {
            log.finest("post " + serviceEndpoint);
        }
        return execute(httppost);
    }

    protected String put(String serviceUrl, String xml) {
        String serviceEndpoint = fullEntityUrl(serviceUrl);
        HttpPut httpput = new HttpPut(serviceEndpoint);
        if (xml != null) {
            StringEntity ent;
            try {
                ent = new StringEntity(xml, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            ent.setContentType("application/xml");
            if (log.isLoggable(Level.FINEST)) {
                log.finest("put " + serviceEndpoint + "\n" + xml.replace("\n", ""));
            }
            httpput.setEntity(ent);
        } else {
            log.finest("put " + serviceEndpoint);
        }
        return execute(httpput);
    }

    protected String get(String serviceUrl) {
        String serviceEndpoint = fullEntityUrl(serviceUrl);
        return execute(new HttpGet(serviceEndpoint));
    }

    public void connect(String domain, String project) {
        this.domain = domain;
        this.project = project;
    }

    public void disconnect() {
        // todo: version control commit (if enabled)
    }

    public TestRunFactory getTestRunFactory() {
        return new TestRunFactory(this);
    }

}
