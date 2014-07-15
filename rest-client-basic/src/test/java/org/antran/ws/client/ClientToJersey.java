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

public class ClientToJersey {

    Logger                      logger                                = LoggerFactory.getLogger(ClientToJersey.class);

    private static final String HTTP_LOCALHOST_8080_WS_TEST01_SERVLET = "http://localhost:8080/ws-test01/servlet";

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
}
