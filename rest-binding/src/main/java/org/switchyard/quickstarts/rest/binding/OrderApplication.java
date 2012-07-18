package org.switchyard.quickstarts.rest.binding;

import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

//@ApplicationPath("/")
public class OrderApplication extends Application {

    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> classes = new HashSet<Class<?>>();

    public OrderApplication(){
         //singletons.add(new OrderResource());
         //singletons.add(new WarehouseResource());
         classes.add(OrderResource.class);
         classes.add(WarehouseResource.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
         return classes;
    }

    @Override
    public Set<Object> getSingletons() {
         return singletons;
    }
}
