Introduction
============
This quickstart demonstrates the usage of a soap-proxy service. A SOAP service is deployed
on port 8080 with context / (ReverseService), and a proxy service is started on the same port
with another context /proxy to proxy it.

![Camel SOAP Proxy Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-soap-proxy/camel-soap-proxy.jpg)

Preqrequisites
==============
Maven
AS7

Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart: 

        mvn install -Pdeploy


3. Using http://localhost:8080/proxy/ReverseService as the endpoint url, submit a request
   with your preferred SOAP client - src/test/resources/xml contains sample requests and
   the responses that you should see. Alternatively use the simple bundled SOAP client and
   the sample request XML found in the soap-proxy subdirectory e.g.

        cd soap-proxy
        mvn exec:java

Expected Output:
================
```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <ns2:reverseResponse xmlns:ns2="urn:switchyard-quickstart:camel-soap-proxy:1.0">
         <text>raboof</text>
      </ns2:reverseResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Camel Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel)
2. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
