package org.antran.ws.client;

import static org.junit.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientToServlet {

    Logger                      logger                                = LoggerFactory.getLogger(ClientToServlet.class);

    private static final String HTTP_LOCALHOST_8080_WS_TEST01_SERVLET = "http://localhost:8080/rest-server/servlet";

    @Test
    public void shouldCallServer() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(HTTP_LOCALHOST_8080_WS_TEST01_SERVLET);

        Response response = target.request()
                .accept(MediaType.APPLICATION_ATOM_XML)
                .get();
        assertNotNull(response);

        String content = response.readEntity(String.class);
        logger.info(content);
    }

    @Test
    public void shouldAskForJsonString() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(HTTP_LOCALHOST_8080_WS_TEST01_SERVLET);

        String response = target.request()
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        assertNotNull(response);
        logger.info(response);
    }

    @Test
    public void shouldPostNewResource() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(HTTP_LOCALHOST_8080_WS_TEST01_SERVLET);

        Response response = target
                .queryParam("who", "a tester")
                .queryParam("what", "it's from test baby")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(null);

        assertNotNull(response);
        String content = response.readEntity(String.class);
        logger.info(content);

        response = target
                .queryParam("id", 8)           
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        assertNotNull(response);
        content = response.readEntity(String.class);
        logger.info(content);
    }
}
