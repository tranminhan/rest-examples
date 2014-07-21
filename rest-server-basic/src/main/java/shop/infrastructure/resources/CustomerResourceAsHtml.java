package shop.infrastructure.resources;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import shop.domain.model.Customer;

@Path("customers.html")
public class CustomerResourceAsHtml extends CustomerResource {

    @POST
    @Produces("text/html")
    public Response createCustomer(
            @FormParam("firstname") String firstName,
            @FormParam("lastname") String lastName) {

        Customer customer = new Customer();
        customer.setId(idCounter.getAndIncrement());
        customer.setFirstName(firstName);
        customer.setLastName(lastName);

        customerDb.put(customer.getId(), customer);

        return Response
                .created(URI.create("" + customer.getId()))
                .cookie(new NewCookie("last-visit", new Date().toString()))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces("text/plain")
    public Response getCustomer(@PathParam("id") int id,
            @HeaderParam("User-Agent") String userAgent,
            @CookieParam("last-visit") String date) {

        final Customer customer = customerDb.get(id);
        if (customer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        String output = "User-Agent: " + userAgent + "\r\n";
        output += "Last Visit: " + date + "\r\n\r\n";
        output += "Customer: " + customer.getFirstName() + " " + customer.getLastName();

        return Response.ok(output)
                .cookie(new NewCookie("last-visit", new Date().toString()))
                .build();
    }
}
