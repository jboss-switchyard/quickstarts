bean-service: demonstrates the usage of the bean component
============================================
Author: SwitchYard Team  
Level: Beginner  
Technologies: SwitchYard, Camel, SOAP 
Summary: Demonstrates the usage of the bean component. 
Target Product: FSW  
Source: <https://github.com/jboss-switchyard/quickstarts>


What is it?
-----------
This quickstart demonstrates the usage of the bean component.   

This example shows a bean component 
service, OrderService, which is provided through the OrderServiceBean, and an InventoryService which 
is provided through the InventoryServiceBean implementation.    Orders are submitted through the 
OrderService, and the OrderService then looks items up in the InventoryService to see if they are 
in stock and the order can be processed.

This example is invoked through a SOAP gateway binding.

![Bean Service Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bean-service/bean-service.jpg)



System requirements
-------------------

Before building and running this quick start you need:

* Maven 3.0.3 or higher
* JDK 1.6 or 1.7
* JBoss AS 7

Build and Deploy the Quickstart
-------------------------
1. Start JBoss AS 7 in standalone mode:

    `${AS}/bin/standalone.sh`

2. Build and deploy the Quickstart : 

    mvn install -Pdeploy

Use the bundle
-------------------

Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
      * Submit a request with your preferred SOAP client - src/test/resources/xml contains sample 
        requests and the responses that you should see
      * Use the simple bundled SOAP client and the sample request XML e.g.
<br/>
```
            mvn exec:java
```
<br/>
      * SOAP-UI : Use the wsdl for this project (src/main/resources/wsdl/OrderService.wsdl) to create 
        a soap-ui project.    Use the sample request (src/test/resources/xml/soap-request.xml) as an 
        example of a sample request.  The expected output is below:

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

Undeploy the bundle
-------------------

Undeploy the quickstart:
        mvn clean -Pdeploy

## Further Reading

1. [Bean Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Bean)
