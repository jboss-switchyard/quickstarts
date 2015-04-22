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
                    <operationSelector operationName="print"/>
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
will create a CamelContext add add a route for the configured component, which is identified in this case using the *configUri*
attribute. This route will look like this for the above configuration:

    from("direct://input").to("switchyard://SimpleCamelService?operationName=print");
    
The *operationName* is optional if the Service only has a single method and can left out in that case.
    
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
Camel endpoint to be called. The *OrderComponent* is configured with an implementation of the *OrderService* interface and
a concreate implementation of *OrderServiceImpl*:

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
Notice that *OrderServiceImpl* requires a *WarehouseService* instance. This is declared in the configuration using the
reference element on the *OrderComponent* and SwitchYard will inject a proxy that will delegate to the Camel endpoint.

---
# Transactions with Camel Service Bindings
Certain Camel Components support transactions, for example the JMS Component, and there are a few options that can be configured which
will be described in this section. 

## Local Transaction configuration
A Service Binding for Camel using the JMS Component with a local transaction can look like this:

    <camel:binding.camel configURI="jms://GreetingServiceQueue?connectionFactory=%23ConnectionFactory&amp;transacted=true"/>
This configures a JMS Component to use a local transaction. This means that if the route, in our case this  is the 
route that sends the JMS Message content to the target SwitchYard Service, completes successfully the transaction will
be committed. If the route does not end successfully, for example an exception is thrown from the Service Implementation, 
the transaction will be rolled back and the message will still be in the JMS Brokers queue. 

**Note: The default transaction error handler will determine the policy for redelivery and this is currently not configurable. This is something that we going to attend to for the next 0.4 release.**

## JTA Transaction configuration
Using local transactions as described in the previous example is nice for cases where the service implementation does
not need to take part of the same transaction. If for example your service implementation does require participation, for
example if it persists data to a database, you will need to configure a JTA transaction manager that will coordinate the
transaction.
The following examples shows how to configure a JTA transaction manager:

    <camel:binding.camel configURI="jms://GreetingServiceQueue?connectionFactory=%23JmsXA&amp;transactionManager=%23jtaTransactionManager&amp;transacted=true"/>
    
The _#jtaTransactionManager_ is a reference to a bean in the Camel Registry. This would normally be configured using Spring XML
when outside of SwitchYard. Is is possible to specify a custom transaction manager by using a CDI bean which will be describe 
later in this document. By default though, if you configure the _transactionManager_ to be _#jtaTransactionManager_, SwitchYard will 
perform lookups in JNDI to try to determine which TransactionManager to use to drive the transactions. The order is as follows:

1. java:jboss/UserTransaction 
2. java:comp/UserTransaction 


## Custom Transaction Manager
If you are deploying SwitchYard in an environment that does not have the transaction managers available that were listed in
 the previous section you can use CDI to have a JTA Transaction Manager injected into the Camel Registry. 
Example of a custom JTA Transaction Manager using CDI:

    import javax.enterprise.context.ApplicationScoped;
    import javax.enterprise.inject.Produces;
    import javax.inject.Named;
    
    import org.springframework.transaction.PlatformTransactionManager;
    import org.springframework.transaction.jta.JtaTransactionManager;
    
    @ApplicationScoped
    public class CustomTransactionManager {
        
        @Produces @Named ("myTransactionManager")
        public PlatformTransactionManager create() {
            final JtaTransactionManager transactionManager = new JtaTransactionManager();
            transactionManager.setUserTransactionName("UserTransactionJndiName");
            transactionManager.setTransactionManagerName("TransactionManagerJndiName");
            transactionManager.setTransactionSynchronizationRegistryName("TransactionSynchronizationRegistryJndiName");
            transactionManager.afterPropertiesSet();
            return transactionManager;
        }
    
    }
    
_ _ _
## Using SwitchYard implementation.camel with Apache Camel
A Camel route can be used as the implementation for a service. This is done by using the *implementation.camel* element which can contain a Camel route.
SwitchYard takes care of creating a service for the interface which sole purpose is to trigger the Camel route. It does this by creating a *from* route 
that triggers the Camel route. This allows SwitchYard clients to use the service interface to invoke the Camel route.

For example:

    route.xml :
    <route xmlns="http://camel.apache.org/schema/spring" id="Camel Test Route">
        <log message="ItemId [${body}]"/>
        <to uri="switchyard://WarehouseService?operationName=hasItem"/>
        <log message="Title Name [${body}]"/>
    </route>

    <sd:switchyard 
        xmlns="urn:switchyard-component-camel:config:1.0" 
        xmlns:sca="http://docs.oasis-open.org/ns/opencsa/sca/200912" 
        xmlns:sd="urn:switchyard-config:switchyard:1.0"
        xmlns:bean="urn:switchyard-component-bean:config:1.0">

        <sca:composite>
    
            <sca:component name="WarehouseComponent" >
                <bean:implementation.bean class="org.switchyard.component.camel.deploy.support.WarehouseServiceImpl"/>
                <sca:service name="WarehouseService" promote="WarehouseService">
                    <sca:interface.java interface="org.switchyard.component.camel.deploy.support.WarehouseService"/>
                </sca:service>
            </sca:component>
        
            <sca:component name="CamelComponent">
                <sca:service name="OrderService" >
                    <sca:interface.java interface="org.switchyard.component.camel.deploy.support.OrderService"/>
                </sca:service>
                
                <sca:reference name="WarehouseService">
                    <sca:interface.java interface="org.switchyard.component.camel.deploy.support.WarehouseService"/>
                </sca:reference>
                
                <implementation.camel>
                    <xml path="META-INF/route.xml"/>
                </implementation.camel>
            </sca:component>
        
        </sca:composite>

    </sd:switchyard>
As you can see in the above configuration that the route in *implementation.camel* does not contain a *from* route. Switchyard will add a *from* route for the service.

The above would be invokable by using the following code from a SwitchYard test:

    String title = newInvoker("OrderService").operation("getTitleForItem").sendInOut("10").getContent(String.class);
    
Running the above code snippet would generate the following in you console log:

    10:57:45,915 INFO  [impl.DefaultCamelContext] Apache Camel 2.6.0 (CamelContext: camel-1) started in 0.838 seconds
    10:57:46,284 INFO  [impl.DefaultCamelContext] Route: Camel Test Route started and consuming from: Endpoint[switchyard://OrderService]
    10:57:46,307 INFO  [impl.DefaultCamelContext] Apache Camel 2.6.0 (CamelContext: camel-1) is starting
    10:57:46,307 INFO  [impl.DefaultCamelContext] Total 1 routes, of which 1 is started.
    10:57:46,307 INFO  [impl.DefaultCamelContext] Apache Camel 2.6.0 (CamelContext: camel-1) started in 0.000 seconds
    10:57:46,428 INFO  [Camel Test Route] ItemId [10]
    10:57:46,434 INFO  [Camel Test Route] Title Name [Fletch]

Notice the Camel Endpoint **switchyard://OrderService**, this is the endpoint that SwitchYard generates and can be configured by setting the schema property like demonstrated above. 

---
## SwitchYard Transformers and Camel's TypeConverters
SwitchYard has build in transformers and custom transformers can be registered with the runtime. Camel has TypeConverters
that basically perform similar tasks. 

SwitchYards Camel component provides a SwitchYard transformer that delegates to Camel's TypeConverters. To accomplish this the different type of converters have to be registered with SwitchYard as transformers. This is currently a static file, *META-INF/switchyard/transforms.xml* 
which is parsed by SwitchYard. In future versions these will be dynamically registred with SwitchYard.

If users have customer Camel TypeConverters they can be specified by either adding them to the transforms.xml in the SwitchYard Camel component or they 
can add a transforms.xml to there deployment archive.

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
