package shop.infrastructure.resources;

import static org.junit.Assert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerResourceAsHtmlTest {
    private static final String CUSTOMERS_BASE_RESOURCE = "http://localhost:8080/rest-server/shopping/customers.html";
    Logger                      logger                  = LoggerFactory.getLogger(CustomerResourceAsHtmlTest.class);

    @Test
    public void shouldCreateNewCustomer() {
        Client client = ClientBuilder.newClient();

        Form form = new Form()
                .param("firstname", "Adam")
                .param("lastname", "Levin");

        Response response = client.target(CUSTOMERS_BASE_RESOURCE)
                .request()
                .post(Entity.form(form));
        logger.info("responseAsString: " + response.readEntity(String.class));
        logger.info("Location: " + response.getHeaderString("Location"));
        assertEquals(201, response.getStatus());
    }

}
