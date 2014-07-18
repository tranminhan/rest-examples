package shop.infrastructure.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.ietf.annotations.PATCH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.xml.wss.impl.misc.ReflectionUtil;

import shop.domain.model.Customer;

@Path("/customers")
public class CustomerResource {
    static Logger                               logger     = LoggerFactory.getLogger(CustomerResource.class);

    private static final Map<Integer, Customer> customerDb = new HashMap<Integer, Customer>();
    private static final AtomicInteger          idCounter  = new AtomicInteger();

    public CustomerResource()
    {
        Customer customer = new Customer();
        customer.setId(idCounter.getAndIncrement());
        customer.setFirstName("Peter");
        customer.setLastName("Pan");
        customer.setStreet("Maze");
        customer.setCity("Unknown");
        customer.setState("Unknown");
        customer.setZip("Unknown");
        customer.setCountry("Unknown");

        logger.info("customer: " + ReflectionToStringBuilder.toString(customer));
        customerDb.put(customer.getId(), customer);
    }

    @POST
    @Consumes("application/xml")
    public Response createCustomer(InputStream is) {
        Customer customer = readCustomer(is);
        customer.setId(idCounter.getAndIncrement());
        customerDb.put(customer.getId(), customer);
        logger.info("Customer created: " + ReflectionToStringBuilder.toString(customer));

        return Response.created(URI.create("/" + customer.getId())).build();
    }

    @GET
    @Produces("application/xml")
    public StreamingOutput getCustomers(
            @QueryParam("start") final int start,
            @QueryParam("size") @DefaultValue("2") final int size) {

        logger.info("getCustomes with start=" + start + ", size=" + size);

        return new StreamingOutput() {

            public void write(OutputStream output) throws IOException, WebApplicationException {
                PrintStream writer = new PrintStream(output);
                writer.println("<customers>");
                List<Customer> list = new ArrayList<Customer>(customerDb.values());
                for (int i = start; i < list.size() && i < (start + size); i++) {
                    final Customer customer = list.get(i);
                    writer.println("<customer id=\"" + customer.getId() + "\">");
                    writer.println("   <first-name>" + customer.getFirstName() + "</first-name>");
                    writer.println("   <last-name>" + customer.getLastName() + "</last-name>");
                    writer.println("   <street>" + customer.getStreet() + "</street>");
                    writer.println("   <city>" + customer.getCity() + "</city>");
                    writer.println("   <state>" + customer.getState() + "</state>");
                    writer.println("   <zip>" + customer.getZip() + "</zip>");
                    writer.println("   <country>" + customer.getCountry() + "</country>");
                    writer.println("</customer>");
                }

                writer.println("</customers>");
            }
        };
    }

    @GET
    @Path("{id}")
    @Produces("application/xml")
    public StreamingOutput getCustomer(@PathParam("id") int id) {
        final Customer customer = customerDb.get(id);
        if (customer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                outputCustomer(output, customer);
            }
        };
    }

    @GET
    @Path("{firstname}-{lastname}")
    public StreamingOutput getCustomerWithFirstNameAndLastName(
            @PathParam("firstname") String firstName,
            @PathParam("lastname") String lastName) {
        Customer found = null;
        for (Customer customer : customerDb.values()) {
            if (customer.getLastName().equalsIgnoreCase(lastName)
                    && customer.getFirstName().equalsIgnoreCase(firstName)) {
                found = customer;
                break;
            }
        }

        final Customer customer = found != null ? found : null;
        if (customer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                outputCustomer(output, customer);
            }
        };
    }

    @PUT
    @Path("{id}")
    @Consumes("application/xml")
    public void updateCustomer(@PathParam("id") int id, InputStream is) {
        final Customer customerToUpdate = readCustomer(is);
        final Customer currentCustomer = customerDb.get(id);

        if (currentCustomer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        logger.info("Customer before updated: " + ReflectionToStringBuilder.toString(currentCustomer));

        currentCustomer.setFirstName(customerToUpdate.getFirstName());
        currentCustomer.setLastName(customerToUpdate.getLastName());
        currentCustomer.setStreet(customerToUpdate.getStreet());
        currentCustomer.setCity(customerToUpdate.getCity());
        currentCustomer.setState(customerToUpdate.getState());
        currentCustomer.setZip(customerToUpdate.getZip());
        currentCustomer.setCountry(customerToUpdate.getCountry());

        logger.info("Customer after updated: " + ReflectionToStringBuilder.toString(currentCustomer));
    }

    @PUT
    @Path("{id}/edit")
    @Consumes("application/xml")
    public void updatePartialCustomerWithPut(@PathParam("id") int id, InputStream is) {
        updatePartialCustomer(id, is);
    }

    @PATCH
    @Path("{id}")
    @Consumes("application/xml")
    public void updatePartialCustomer(@PathParam("id") int id, InputStream is) {
        final Customer customerToUpdate = readCustomer(is);
        final Customer currentCustomer = customerDb.get(id);

        if (currentCustomer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        logger.info("Customer before updated: " + ReflectionToStringBuilder.toString(currentCustomer));

        if (customerToUpdate.getFirstName() != null) {
            currentCustomer.setFirstName(customerToUpdate.getFirstName());
        }

        if (customerToUpdate.getLastName() != null) {
            currentCustomer.setLastName(customerToUpdate.getLastName());
        }

        if (customerToUpdate.getStreet() != null) {
            currentCustomer.setStreet(customerToUpdate.getStreet());
        }

        if (customerToUpdate.getCity() != null) {
            currentCustomer.setCity(customerToUpdate.getCity());
        }

        if (customerToUpdate.getState() != null) {
            currentCustomer.setState(customerToUpdate.getState());
        }

        if (customerToUpdate.getZip() != null) {
            currentCustomer.setZip(customerToUpdate.getZip());
        }

        if (customerToUpdate.getCountry() != null) {
            currentCustomer.setCountry(customerToUpdate.getCountry());
        }

        logger.info("Customer after updated: " + ReflectionToStringBuilder.toString(currentCustomer));
    }

    private void outputCustomer(OutputStream output, Customer customer) {
        PrintStream writer = new PrintStream(output);
        writer.println("<customer id=\"" + customer.getId() + "\">");
        writer.println("   <first-name>" + customer.getFirstName() + "</first-name>");
        writer.println("   <last-name>" + customer.getLastName() + "</last-name>");
        writer.println("   <street>" + customer.getStreet() + "</street>");
        writer.println("   <city>" + customer.getCity() + "</city>");
        writer.println("   <state>" + customer.getState() + "</state>");
        writer.println("   <zip>" + customer.getZip() + "</zip>");
        writer.println("   <country>" + customer.getCountry() + "</country>");
        writer.println("</customer>");
    }

    private Customer readCustomer(InputStream is) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(is);
            Element root = document.getDocumentElement();

            Customer customer = new Customer();
            if (root.getAttribute("id") != null && !root.getAttribute("id").equals("")) {
                customer.setId(Integer.valueOf(root.getAttribute("id")));
            }

            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);

                if (element.getTagName().equals("first-name")) {
                    customer.setFirstName(element.getTextContent());
                }
                else if (element.getTagName().equals("last-name")) {
                    customer.setLastName(element.getTextContent());
                }
                else if (element.getTagName().equals("street")) {
                    customer.setStreet(element.getTextContent());
                }
                else if (element.getTagName().equals("city")) {
                    customer.setCity(element.getTextContent());
                }
                else if (element.getTagName().equals("state")) {
                    customer.setState(element.getTextContent());
                }
                else if (element.getTagName().equals("zip")) {
                    customer.setZip(element.getTextContent());
                }
                else if (element.getTagName().equals("country")) {
                    customer.setCountry(element.getTextContent());
                }
            }

            return customer;
        }
        catch (ParserConfigurationException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        catch (SAXException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        catch (IOException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }
}
