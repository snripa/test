package org.rii;


import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class Checker {

    private final String host;

    public Checker(String host) {
        this.host = host;
    }

    public ServiceHealth checkHealth() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            return httpclient.execute(new HttpGet(host), resp ->
                    resp.getStatusLine().getStatusCode() == 200 ? ServiceHealth.UP : ServiceHealth.DOWN);

        } catch (IOException ex) {
            return ServiceHealth.NOT_AVAILABLE;
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
