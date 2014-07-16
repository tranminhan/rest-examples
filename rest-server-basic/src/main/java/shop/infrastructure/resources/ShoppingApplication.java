package shop.infrastructure.resources;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/shopping")
public class ShoppingApplication extends Application {

    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<Object>();
        singletons.add(new CustomerResource());
        return singletons;
    }
}
