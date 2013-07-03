Introduction
============
This quickstart demonstrates the usage of the bean component.   This example shows a bean component 
service, OrderService, which is provided through the OrderServiceBean, and an InventoryService which 
is provided through the InventoryServiceBean implementation.    Orders are submitted through the 
OrderService, and the OrderService then looks items up in the InventoryService to see if they are 
in stock and the order can be processed.

This example is invoked through a SOAP gateway binding.

![Bean Service Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bean-service/bean-service.jpg)

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

3. Deploy the Quickstart : 

        mvn jboss-as:deploy

4. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
      - Submit a request with your preferred SOAP client - src/test/resources/xml contains sample 
        requests and the responses that you should see
      - Use the simple bundled SOAP client and the sample request XML e.g.
<br/>
```
            mvn exec:java
```
<br/>
      - SOAP-UI : Use the wsdl for this project (src/main/resources/wsdl/OrderService.wsdl) to create 
        a soap-ui project.    Use the sample request (src/test/resources/xml/soap-request.xml) as an 
        example of a sample request.  The output below is the expected output :

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
       <SOAP-ENV:Body>
           <orders:submitOrderResponse xmlns:orders="urn:switchyard-quickstart:bean-service:1.0">
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

1. [Bean Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Bean)
