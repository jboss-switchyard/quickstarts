# Switchyard Camel Component
This project enables Apache Camel endpoints to be exposed as SwitchYard services and also contains
a Apache Camel component Camel routes to call Switchyard Services. 

Exposing Apache Camel endpoint(s) as SwitchYard services:
-----
<pre>
&lt;sca:composite&gt;
    &lt;sca:service name="SimpleCamelService"&gt;
        &lt;camel:binding.direct uri="direct://input"&gt;
            &lt;camel:operationSelector operationName="print"/&gt;
        &lt;/camel:binding.direct&gt;
    &lt;/sca:service&gt;
        
    &lt;sca:component name="ComponentName"&gt;
        &lt;bean:implementation.bean class="org.switchyard.component.camel.deploy.support.SimpleCamelServiceImpl"/&gt;
        &lt;sca:service name="SimpleCamelService"&gt;
            &lt;sca:interface.java interface="org.switchyard.component.camel.deploy.support.SimpleCamelService"/&gt;
        &lt;/sca:service&gt;
    &lt;/sca:component&gt;

&lt;/sca:composite&gt;

</pre>

Calling a SwitchYard service from Apache Camel:
----
<pre>
&lt;camelContext id="camelContext" xmlns="http://camel.apache.org/schema/spring"&gt;

    &lt;route id="fromCamelComponent"&gt;
        &lt;from uri="direct:input"/&gt;
        &lt;log message="From Camel Component body: ${body}"/&gt;
        &lt;to uri="switchyard://oneWayService?operationName=someMethodName"/&gt;
    &lt;/route&gt;

&lt;/camelContext&gt;
</pre>

