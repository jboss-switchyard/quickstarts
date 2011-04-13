# SwitchYard Camel Component
This project deals with integrating Apache Camel with SwitchYard.

## Using Apache Camel components as gateways/entrypoint into SwitchYard
    <switchyard 
        xmlns="urn:switchyard-config:switchyard:1.0"
        xmlns:camel="urn:switchyard-component-camel:config:1.0" 
        xmlns:sca="http://docs.oasis-open.org/ns/opencsa/sca/200912" 
        xmlns:bean="urn:switchyard-component-bean:config:1.0">

        <sca:composite>
            <sca:service name="SimpleCamelService">
                <camel:binding.camel configURI="direct://input">
                    <camel:operationSelector operationName="print"/>
                </camel:binding.camel>
            </sca:service>
        
            <sca:component name="ComponentName">
                <bean:implementation.bean class="org.switchyard.component.camel.deploy.support.SimpleCamelServiceImpl"/>
                <sca:service name="SimpleCamelService">
                    <sca:interface.java interface="org.switchyard.component.camel.deploy.support.SimpleCamelService"/>
                </sca:service>
            </sca:component>
        
        </sca:composite>

    </switchyard>
In the configuration above you see an example of a service that is using a Camel binding. What will happen is that SwitchYard
will create a CamelContext add add a route for the configured component, which is identified in this case using the 'configUri'
attribute. This route will look like this for the above configuration:

    from("direct://input").to("switchyard://SimpleCamelService?operationName=print");
    
_ _ _

## Using SwitchYard reference bindings with Apache Camel
A configuration of a reference binding could look like this:
    <switchyard 
        xmlns="urn:switchyard-config:switchyard:1.0"
        xmlns:camel="urn:switchyard-component-camel:config:1.0" 
        xmlns:sca="http://docs.oasis-open.org/ns/opencsa/sca/200912" 
        xmlns:bean="urn:switchyard-component-bean:config:1.0">
        
        <sca:composite>
        
            <sca:reference name="WarehouseService" promote="OrderComponent/WarehouseService">
                <camel:binding.camel configURI="vm://warehouseStatusService"/>
            </sca:reference>
            
            <sca:component name="OrderComponent">
                <bean:implementation.bean class="org.switchyard.component.camel.deploy.support.OrderServiceImpl"/>
                <sca:service name="OrderService">
                    <sca:interface.java interface="org.switchyard.component.camel.deploy.support.OrderService"/>
                </sca:service>
                <sca:reference name="WarehouseService">
                    <sca:interface.java interface="org.switchyard.component.camel.deploy.support.WarehouseService"/>
                </sca:reference>
            </sca:component>
            
        </sca:composite>
    
    </switchyard>
In this configuration we have defined a reference binding which has specified a configURI attribute which identifies the
Camel endpoint to be called. The 'OrderComponent' is configured with an implementation of the 'OrderService' interface and
a concreate implementation of 'OrderServiceImpl':

    public interface OrderService {
        public String getTitleForItem(String itemId);
    }
    
    import javax.inject.Inject;

    import org.switchyard.component.bean.Reference;
    import org.switchyard.component.bean.Service;
    
    @Service(OrderService.class)
    public class OrderServiceImpl implements OrderService {
        
        @Inject @Reference
        WarehouseService _warehouseService;
    
        @Override
        public String getTitleForItem(String itemId) {
            return _warehouseService.hasItem(itemId);
        }
    }
Notice that 'OrderServiceImpl' requires a 'WarehouseService' instance. This is declared in the configuration using the
reference element on the 'OrderComponent' and SwitchYard will inject a proxy that will delegate to the Camel endpoint.

_ _ _

## Calling a SwitchYard service from Apache Camel:
    <camelContext id="camelContext" xmlns="http://camel.apache.org/schema/spring">

        <route id="fromCamelComponent">
            <from uri="direct:input"/>
            <log message="From Camel Component body: ${body}"/>
            <to uri="switchyard://serviceName?operationName=operationName"/>
        </route>

    </camelContext>

_ _ _
