package shop.infrastructure.resources;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}
