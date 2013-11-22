Introduction
============
This quickstart demonstrates the usage of the bpm component.
This example shows a bpm component, ProcessOrder, which exposes a business process flow.

The incoming messageId is mapped from the SwitchYard Context and into a process variable
using an MVEL expression, and printed out via a BPMN onEntry-script of the Inventory service.

This example is invoked through a SOAP gateway binding.

If you would like to log the process execution, uncomment these lines in
src/main/resources/META-INF/switchyard.xml:
```
<listeners>
    <listener class="org.drools.event.DebugProcessEventListener"/>
</listeners>
```

![BPM Service Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpm-service/bpm-service.jpg)


Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart : 

        mvn install -Pdeploy

3. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
    - Submit a request with your preferred SOAP client - src/test/resources/xml contains sample 
      requests and the responses that you should see
    - Use the simple bundled SOAP client and the sample request XML e.g.
<br/>
```
            mvn exec:java
```
<br/>
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

4. Undeploy the quickstart:
        mvn clean -Pdeploy

Test Properties:
================
This quickstart also shows (albeit commented out) how one could write a test where test properties within configurations are programmatically overridden.  In switchyard.xml, you see that the binding.soap element's socketAddr port is a dynamic property, like so:
```
        <socketAddr>localhost:${soapPort:18001}</socketAddr>
```
In WebServiceTest.java, you can see that by uncommenting the commented code, you could set the value of the soapPort property to 18002, and that will be used instead of the default port of 18001.  One could also run the test with a system property overriding the value, which takes highest precedence. (i.e.: -DsoapPort=18003)

Additionally, you can see how a domain property (userName) is retrieved from a system property (${user.name}), which is then mapped into a process instance variable (userName), which can then be used inside the process itself.

## Further Reading

1. [BPM Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/BPM)
