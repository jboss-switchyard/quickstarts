Introduction
============
This quickstart demonstrates the usage of a soap-proxy service.   A SOAP
service is deployed on port 18001 (ReverseService), and a proxy service is
started on port 18002 to proxy it. 

Preqrequisites 
==============
Maven
AS7

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 with the standalone-preview.xml :
    ./standalone.sh --server-config=standalone-preview.xml
3. Deploy the Quickstart :
    cp target/switchyard-quickstarts-camel-soap-proxy-${version}-SNAPSHOT.jar ${AS7}/standalone/deployments
4. Submit a webservice request to invoke the SOAP proxy.  There are a
   number of ways to do this :
      - Submit a request with your preferred SOAP client -
        src/test/resources/xml contains sample requests and the responses
        that you should see
 
Expected Output:
================
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <ns2:reverseResponse xmlns:ns2="urn:switchyard-quickstart:camel-soap-proxy:1.0">
         <text>raboof</text>
      </ns2:reverseResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>

## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)



