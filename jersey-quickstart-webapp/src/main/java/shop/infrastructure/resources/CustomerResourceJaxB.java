package shop.infrastructure.resources;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shop.domain.model.Customer;

@Path("/customers.xml")
public class CustomerResourceJaxB {
    static Logger                       logger     = LoggerFactory.getLogger(CustomerResourceJaxB.class);
    static final Map<Integer, Customer> customerDb = new HashMap<Integer, Customer>();
    static final AtomicInteger          idCounter  = new AtomicInteger();

    @POST
    @Consumes("application/xml")
    public Response createCustomer(Customer customer) {
        customer.setId(idCounter.getAndIncrement());
        customerDb.put(customer.getId(), customer);

        logger.info("customer created: " + ReflectionToStringBuilder.toString(customer));
        return Response.created(URI.create("/id/" + customer.getId())).build();
    }

    @GET
    @Path("{id}")
    @Produces("application/xml")
    public Customer getCustomerAsXml(@PathParam("id") int id) {
        logger.info("requesting customer with id: " + id);

        Customer customer = customerDb.get(id);
        if (customer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return customer;
    }

}
