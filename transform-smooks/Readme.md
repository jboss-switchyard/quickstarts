Introduction
============
This quickstart demonstrates the usage of the Smooks transformer.  The service contained in this 
quickstart - OrderService - creates an OrderAck object based on the information from the Order 
object receives.  The object is then transformed by the Smooks transformer from an OrderAck 
object to a submitOrder XML and then back to a Order Java object.     

This example is invoked through a SOAP gateway binding.

Requirements
============
Maven


Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone mode:
    ${AS}/bin/standalone.sh
3. Deploy the Quickstart : 
    cp target/switchyard-quickstart-transform-smooks.jar ${AS7}/standalone/deployments
4. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
      - Submit a request with your preferred SOAP client - src/test/resources/xml contains sample 
        requests and the responses that you should see
      - SOAP-UI : Use the wsdl for this project (src/main/resources/wsdl/OrderService.wsdl) to create 
        a soap-ui project.    Use the sample request (src/test/resources/xml/soap-request.xml) as an 
        example of a sample request.  The output below is the expected output :

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <orders:submitOrderResponse xmlns:orders="urn:switchyard-quickstart:transform-smooks:1.0">
         <orderAck>
             <orderId>PO-19838-XYZ</orderId>
             <accepted>true</accepted>
             <status>Order Accepted</status>
         </orderAck>
      </orders:submitOrderResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)

