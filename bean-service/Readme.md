
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

EAP
----------
1. Start EAP in standalone mode:

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
      - SOAP-UI : Use the wsdl for this project (src/main/resources/wsdl/OrderService.wsdl) to create 
        a soap-ui project.    Use the sample request (src/test/resources/xml/soap-request.xml) as an 
        example of a sample request.   See the "Expected Output" heading for the expected results.

4. Undeploy the quickstart:
        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode :
    
        ${WILDFLY}/bin/standalone.sh

2. Build and deploy the Quickstart : 

        mvn install -Pdeploy -Pwildfly

3. To submit a webservice request to invoke the SOAP gateway :
<br/>
```
        mvn exec:java
```
<br/>

4. Undeploy the quickstart:
        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :
 
    ${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the bean-service quickstart :

karaf@root> features:install switchyard-quickstart-bean-service

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-bean-service


Expected Output
===============
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
