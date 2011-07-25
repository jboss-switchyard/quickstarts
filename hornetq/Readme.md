# SwitchYard HornetQ Component
This module contains a SwitchYard HornetQ component enabling SwitchYard to consumer and produce messages
to and from HornetQ native queues.

This module also contains a HornetQ Camel Component. This component has not dependencies on Camel's JMS Component and 
hence has no dependencies to Spring. The reason for writing the HornetQ Component for Camel was to avoid these dependencies.

## HornetQ Service Binding
A HornetQ service binding acts as a gateways/entrypoint into SwitchYard.
    <switchyard 
        xmlns="urn:switchyard-config:switchyard:1.0"
        xmlns:hornetq="urn:switchyard-component-hornetq:config:1.0" 
        xmlns:sca="http://docs.oasis-open.org/ns/opencsa/sca/200912" 
        xmlns:bean="urn:switchyard-component-bean:config:1.0">

        <sca:composite>
    
            <sca:service name="HornetQService">
                <hornetq:binding.hornetq>
                    <hornetq:config>
                       <hornetq:connector>
                          <hornetq:factoryClass>org.hornetq.core.remoting.impl.invm.InVMConnectorFactory</hornetq:factoryClass>
                       </hornetq:connector>
                       <hornetq:queue>jms.queue.consumer</hornetq:queue>
                    </hornetq:config>
                </hornetq:binding.hornetq>
            </sca:service>
        
            <sca:component name="ComponentName">
                <bean:implementation.bean class="org.switchyard.component.hornetq.deploy.support.HornetQServiceImpl"/>
                <sca:service name="HornetQService">
                    <sca:interface.java interface="org.switchyard.component.hornetq.deploy.support.HornetQService"/>
                </sca:service>
            </sca:component>
        
        </sca:composite>

    </switchyard>
The complete list of configuration options available can be found in the schema 
[hornetq-v1.xsd](hornetq/src/main/resources/org/switchyard/component/hornetq/config/model/v1/hornetq-v1.xsd)

## HornetQ Reference Binding
A HornetQ Reference Binding allows a service to send messages to HornetQ queues.
For example:
    <switchyard 
    xmlns="urn:switchyard-config:switchyard:1.0"
    xmlns:hornetq="urn:switchyard-component-hornetq:config:1.0" 
    xmlns:sca="http://docs.oasis-open.org/ns/opencsa/sca/200912" 
    xmlns:bean="urn:switchyard-component-bean:config:1.0">

    <sca:composite>
    
        <sca:component name="OrderComponent">
            <bean:implementation.bean class="org.switchyard.component.hornetq.support.OrderServiceImpl"/>
            <sca:service name="OrderService">
                <sca:interface.java interface="org.switchyard.component.hornetq.support.OrderService"/>
            </sca:service>
            <sca:reference name="WarehouseService"/>
        </sca:component>
        
        <sca:reference name="WarehouseService" promote="WarehouseService">
            <sca:interface.java interface="org.switchyard.component.hornetq.support.WarehouseService"/>
            <hornetq:binding.hornetq>
                <hornetq:operationSelector operationName="processOrder"/>
                <hornetq:config>
                    <hornetq:connector>
                       <hornetq:factoryClass>org.hornetq.core.remoting.impl.invm.InVMConnectorFactory</hornetq:factoryClass>
                    </hornetq:connector>
                    <hornetq:queue>jms.queue.producer</hornetq:queue>
                </hornetq:config>
            </hornetq:binding.hornetq>
        </sca:reference>
        
    </sca:composite>
    
</switchyard>
In the above configuration we have defined a component named _OrderComponent_ that has a service named _OrderService_. This 
service is implemented by the POJO _OrderServiceImpl_ :

    @Service(OrderService.class)
    public class OrderServiceImpl implements OrderService {
    
        @Inject @Reference 
        private WarehouseService warehouseService;

        @Override
        public void sendOrder(final String order)
        {
            warehouseService.processOrder(order);
        }
    }

If you look at the definition of the _OrderComponent_ it has a reference declared: 

    <sca:reference name="WarehouseService"/>
This reference will be injected into _OrderServiceImpl_ by the SwitchYard Bean component. What is injected will
be a proxy that will delegate to the HornetQ component, which is configured in the _hornetq:binding.hornetq_ section in the config.
When the call _processOrder_ is called, the _order_ will be send to the HornetQ queue _jms.queue.producer_.
_ _ _

## Using HornetQ Camel Component
Using the HornetQ Camel component is very simpliar to using other components in Camel.

### Camel Spring XML DSL configuration
Example of configuring the HornetQ component using URI properties: 
    <beans 
        xmlns="http://www.springframework.org/schema/beans" 
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">
    
       <bean id="transportConfigBean" class="org.hornetq.api.core.TransportConfiguration">
           <constructor-arg value="org.hornetq.core.remoting.impl.invm.InVMConnectorFactory"/>
       </bean>

       <camelContext id="camelContext" xmlns="http://camel.apache.org/schema/spring">
           <route id="fromCamelComponent">
               <from uri="hornetq:jms.queue.input?transportConfiguration=#transportConfigBean"/>
               <log message="From Camel Component body: ${body}"/>
            </route>
        </camelContext>
    </beans>
In the above configuration we have specified the _transportConfiguration_ property to be a reference to a bean in the Camel Registry.
Any of the other set methods on the [HornetQEndpoint](hornetq/src/main/java/org/switchyard/component/hornetq/HornetQEndpoint.java) class 
are available in the same manner.

    
Example of creating the HornetQ component as a Spring Bean:
    <beans 
        xmlns="http://www.springframework.org/schema/beans" 
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">
    
	    <bean id="hornetq" class="org.switchyard.component.hornetq.HornetQComponent">
	        <property name="transportConfiguration">
		       <bean class="org.hornetq.api.core.TransportConfiguration">
		           <constructor-arg value="org.hornetq.core.remoting.impl.invm.InVMConnectorFactory"/>
			   </bean>
	        </property>
	    </bean>

       <camelContext id="camelContext" xmlns="http://camel.apache.org/schema/spring">
           <route id="fromCamelComponent">
               <from uri="hornetq:jms.queue.input"/>
               <log message="From Camel Component body: ${body}"/>
            </route>
        </camelContext>
    </beans>
_ _ _
