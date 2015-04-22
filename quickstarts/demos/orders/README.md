# Orders Demo Quickstart

This example demonstrates how to:

1. Implement a SwitchYard Service (a simple "Orders" Service) as a set of [CDI](https://docs.jboss.org/author/display/SWITCHYARD/Bean+Services) beans.
2. Expose a SwitchYard Service through a SOAP interface.
3. Expose a [JSF](http://www.jboss.org/richfaces) User Interface to the same CDI based Service.

![Orders Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/demos/orders/orders.jpg)


## Prerequisites

1. Java 6.
2. An unzipped SwitchYard AS7 Distribution.  <b><u>Note this example will only run on AS7</u></b>.

Running the quickstart
======================

EAP
----------
1. Start EAP in standalone mode:

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


4. Undeploy the quickstart:

mvn clean -Pdeploy



Wildfly
----------
1. Start Wildfly in standalone mode:

${AS}/bin/standalone.sh

2. Build and deploy the quickstart: 

mvn install -Pdeploy,wildfly

3. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
- Submit a request with your preferred SOAP client - src/test/resources/xml contains 
sample requests and the responses that you should see
- Use the simple bundled SOAP client and the sample request XML e.g.
<br/>
```
mvn exec:java
```

4. Undeploy the quickstart:

mvn clean -Pdeploy,wildfly




Expected Output
===============
```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <orders:submitOrderResponse xmlns:orders="urn:switchyard-quickstart-demo:orders:1.0">
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

1. [SwitchYard User Documentation](https://docs.jboss.org/author/display/SWITCHYARD/)
2. [SwitchYard CDI Bean Services](https://docs.jboss.org/author/display/SWITCHYARD/Bean)
3. [SwitchYard SOAP Bindings](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
