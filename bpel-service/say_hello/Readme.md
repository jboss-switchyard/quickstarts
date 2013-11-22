Introduction
============

This example demonstrates a BPEL process that reads a message passed as input and 
replies to it with a "Hello, &lt;input&gt;" message.

![Say Hello Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/say_hello/bpel-say-hello.jpg)


Running the quickstart
======================

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
    - SOAP-UI : Use the wsdl for this projects (src/main/resources/wsdl/) to create a soap-ui project.  
      Use the sample request (src/test/resources/xml/soap-request.xml) as an example of a sample 
      request. The output below is the expected output :

Expected Output
===============

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <sayHelloResponse xmlns="http://www.jboss.org/bpel/examples">
         <tns:result xmlns:tns="http://www.jboss.org/bpel/examples">Hello Fred</tns:result>
      </sayHelloResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

4. Undeploy the quickstart:
        mvn clean -Pdeploy

## Further Reading

1. [BPEL Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/BPEL)
