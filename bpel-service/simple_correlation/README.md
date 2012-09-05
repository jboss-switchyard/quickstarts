Introduction
============

This example shows a simple example of how two separate interactions 
can be correlated to the same BPEL process instance.

In this case, a 'hello' message must be sent first, and then a 'goodbye'
message must follow, where both messages must have the same ID field to
enable them to be correlated to the same process instance.

![Simple Correlation Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/simple_correlation/bpel-correlation.jpg)


Running the quickstart
======================

JBoss AS 7, simple_correlation
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone mode:
    ${AS}/bin/standalone.sh
3. Deploy the Quickstart :
    cp target/switchyard-quickstart-bpel-service-simple-correlation.jar ${AS7}/standalone/deployments
4. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
      - Submit a request with your preferred SOAP client - src/test/resources/xml contains sample
        requests and the responses that you should see
      - Use the simple bundled SOAP client and the sample request XML e.g.
```
    mvn exec:java
```
      - SOAP-UI : Use the wsdl for this projects (src/main/resources/wsdl/) to create a soap-ui project.
        Use the sample request (src/test/resources/xml/soap-request.xml) as an example of a sample
        request.  The output below is the expected output :

Expected Output
===============

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <helloMessage xmlns="http://www.jboss.org/bpel/examples/simple_correlation/">
         <sessionId>
            <id>1</id>
         </sessionId>
         <parameter>BPEL, Hello World!</parameter>
      </helloMessage>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>

and

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <goodbyeMessage xmlns="http://www.jboss.org/bpel/examples/simple_correlation/">
         <sessionId>
            <id>1</id>
         </sessionId>
         <parameter>BPEL, Goodbye World!</parameter>
      </goodbyeMessage>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>

```
