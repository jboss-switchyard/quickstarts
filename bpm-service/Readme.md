Introduction
============
This quickstart demonstrates the usage of the bpm component.
This example shows a bpm component, ProcessOrder, which exposes a business process flow.

The incoming messageId is mapped from the SwitchYard Context and into a process variable
using an MVEL expression, and printed out via a BPMN onEntry-script of the Inventory service.

This example is invoked through a SOAP gateway binding.  

If you would like to watch the process execution, uncomment this line in
src/main/resources/META-INF/switchyard.xml:
```
<!-- <eventListener class="org.drools.event.DebugProcessEventListener"/> -->
```

![BPM Service Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpm-service/bpm-service.jpg)


Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone mode:
    ${AS}/bin/standalone.sh
3. Deploy the Quickstart : 
    cp target/switchyard-quickstart-bpm-service.jar ${AS7}/standalone/deployments
4. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
      - Submit a request with your preferred SOAP client - src/test/resources/xml contains sample 
        requests and the responses that you should see
      - SOAP-UI : Use the wsdl for this projects (src/main/resources/wsdl/) to create a soap-ui 
        project.  Use the sample request (src/test/resources/xml/soap-request.xml) as an example 
        of a sample request.  The output below is the expected output :


Expected Output:
================

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">  
   <SOAP-ENV:Header/>  
   <SOAP-ENV:Body>  
      <ns2:submitOrderResponse xmlns:ns2="urn:switchyard-quickstart:bpm-service:1.0">  
         <orderId>test1</orderId>  
         <accepted>true</accepted>  
         <status>Thanks for your order, it has been shipped!</status>  
      </ns2:submitOrderResponse>  
   </SOAP-ENV:Body>  
</SOAP-ENV:Envelope>
```

## Further Reading

1. [BPM Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/BPM+Services)


