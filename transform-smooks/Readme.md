Introduction
============
This quickstart demonstrates the usage of the Smooks transformer.  The service contained in this 
quickstart - OrderService - creates an OrderAck object based on the information from the Order 
object receives.  The object is then transformed by the Smooks transformer from an OrderAck 
object to a submitOrder XML and then back to a Order Java object.     

![Transform Smooks Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/transform-smooks/transform-smooks.jpg)


Requirements
============
Maven


Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart: 

        mvn install -Pdeploy

3. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
    - Submit a request with your preferred SOAP client - src/test/resources/xml contains 
      sample requests and the responses that you should see
    - Use the simple bundled SOAP client and the sample request XML e.g.
<br/>
```
            mvn exec:java
```
<br/>
    - SOAP-UI : Use the wsdl for this project (src/main/resources/wsdl/OrderService.wsdl) to 
      create a soap-ui project. Use the sample request (src/test/resources/xml/soap-request.xml) 
      as an example of a sample request. The output below is the expected output : 

Expected Output
===============
```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <orders:orderAck xmlns:orders="urn:switchyard-quickstart:transform-smooks:1.0">
         <orderId>PO-19838-XYZ</orderId>
         <accepted>true</accepted>
         <status>Order Accepted</status>
      </orders:orderAck>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)
2. [Smooks Transformer Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Smooks+Transformer)
