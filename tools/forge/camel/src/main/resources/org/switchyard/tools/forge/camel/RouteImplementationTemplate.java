package ${package.name};

import org.apache.camel.builder.RouteBuilder;
import org.switchyard.component.camel.Route;

@Route(${service.name}.class)
public class ${service.name}Builder extends RouteBuilder {
    
    /**
     * The Camel route is configured via this method.  The from:
     * endpoint is required to be a SwitchYard service.
     */
    public void configure() {
        from("switchyard://${service.name}")
        .log("Received message for ${service.name} : ${body}");
    }
}
