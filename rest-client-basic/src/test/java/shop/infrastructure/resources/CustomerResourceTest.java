package shop.infrastructure.resources;

import static org.junit.Assert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.antran.ws.client.ClientToServlet;
import org.junit.Test;
import org.omg.CORBA.RepositoryIdHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerResourceTest {
    Logger logger = LoggerFactory.getLogger(CustomerResourceTest.class);

    @Test
    public void shouldCreateCustomer() {
        Client client = ClientBuilder.newClient();
        String xml = "<customer>"
                + "<first-name>An</first-name>"
                + "<last-name>Tran</last-name>"
                + "<street>617B Second Street</street>"
                + "<city>Petaluma</city>"
                + "<state>CA</state>"
                + "<zip>94952</zip>"
                + "<country>USA</country>"
                + "</customer>";

        // CREATE
        Response response = client.target("http://localhost:8080/rest-server/shopping/customers")
                .request()
                .post(Entity.xml(xml));

        if (response.getStatus() != 201) {
            fail("Failed to create resource");
        }

        String location = response.getLocation().toString();
        logger.info("uri: " + location); 
        response.close(); 

        // GET
        String resourceAsString = client.target(location).request().get(String.class);
        logger.info("resourceAsString: " + resourceAsString);

        // UPDATE
        String xmlUpdate = "<customer>"
                + "<first-name>An</first-name>"
                + "<last-name>Tran</last-name>"
                + "<street>123 Cong Hoa</street>"
                + "<city>Ho Chi Minh</city>"
                + "<state>VN</state>"
                + "<zip>70000</zip>"
                + "<country>VN</country>"
                + "</customer>";
        client.target(location).request().put(Entity.xml(xmlUpdate));

        // GET AGAIN
        resourceAsString = client.target(location).request().get(String.class);
        logger.info("resourceAsString after update: " + resourceAsString);
    }
}
