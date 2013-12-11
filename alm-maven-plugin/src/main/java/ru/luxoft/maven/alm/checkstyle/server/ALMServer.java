package ru.luxoft.maven.alm.checkstyle.server;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
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
            e.printStackTrace();
        }
    }

    public void login(String user, String password) {
        String serviceEndpoint = "http://" + server + "/qcbin/authentication-point/alm-authenticate";
        try {
            HttpPost httppost = new HttpPost(serviceEndpoint);
            StringEntity ent = new StringEntity(
                    "<alm-authentication>" +
                    "<user>" + user + "</user>" +
                    "<password>" + password + "</password>" +
                    "</alm-authentication>", "UTF-8");
            httppost.setEntity(ent);
            CloseableHttpResponse response = httpClient.execute(httppost);
            response.close();
            log.fine("login success");
            this.user = user;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed POST " + serviceEndpoint, e);
        }
    }

    public void logout() {
        String serviceEndpoint = "http://" + server + "/qcbin/authentication-point/logout";
        try {
            HttpGet get = new HttpGet(serviceEndpoint);
            CloseableHttpResponse response = httpClient.execute(get);
            response.close();
            log.fine("logout");
            this.user = null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed GET " + serviceEndpoint, e);
        }
    }

    private String fullEntityUrl(String entityUrl) {
        return "http://" + server + "/qcbin/rest/domains/" + domain + "/projects/" + project + entityUrl;
    }

    protected String post(String serviceUrl, String xml) {
        String serviceEndpoint = fullEntityUrl(serviceUrl);
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

    protected String put(String serviceUrl, String xml) {
        String serviceEndpoint = fullEntityUrl(serviceUrl);
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

    protected String get(String serviceUrl) {
        String serviceEndpoint = fullEntityUrl(serviceUrl);
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
