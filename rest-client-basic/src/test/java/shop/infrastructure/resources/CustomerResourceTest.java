package shop.infrastructure.resources;

import static org.junit.Assert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.antran.ws.client.ClientToServlet;
import org.junit.Ignore;
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

        logger.info("uri: " + response);
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

    @Test
    public void shouldUpdateCustomerPartially() {
        Client client = ClientBuilder.newClient();
        String location = "http://localhost:8080/rest-server/shopping/customers/0";

        // GET
        String resourceAsString = client.target(location).request().get(String.class);
        logger.info("resourceAsString: " + resourceAsString);

        // PATCH
        String xmlPatch = "<customer>"
                + "<city>Ha Long</city>"
                + "</customer>";
        client.target(location + "/edit").request().method("PUT", Entity.xml(xmlPatch));
        // GET AGAIN
        resourceAsString = client.target(location).request().get(String.class);
        logger.info("resourceAsString after update: " + resourceAsString);
    }

    @Test
    public void shouldFindWithFirstNameAndLastName() {
        Client client = ClientBuilder.newClient();
        String location = "http://localhost:8080/rest-server/shopping/customers/" + "Peter-Pan";

        Response response = client.target(location).request().get();
        response.bufferEntity();
        String responseAsString = response.readEntity(String.class);
        logger.info("responseAsString: " + responseAsString);
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void shouldUpdateCustomerPartiallyWithPATCH() {
        Client client = ClientBuilder.newClient();
        String location = "http://localhost:8080/rest-server/shopping/customers/0";

        // GET
        String resourceAsString = client.target(location).request().get(String.class);
        logger.info("resourceAsString: " + resourceAsString);

        // PATCH
        String xmlPatch = "<customer>"
                + "<city>Ha Long</city>"
                + "</customer>";
        client.target(location).request().method("PATCH", Entity.xml(xmlPatch));
        // GET AGAIN
        resourceAsString = client.target(location).request().get(String.class);
        logger.info("resourceAsString after update: " + resourceAsString);
    }
}
