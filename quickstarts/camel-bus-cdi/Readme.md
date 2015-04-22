Introduction
============
This quickstart demonstrates usage of custom ProcessorFactory used by Camel Exchange Bus internally to create and it's binding feature, by invoking given SQL query.

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Submit two webservice requests to invoke the SOAP gateway.  There are a number of ways to do this :
    - Submit the requests with your preferred SOAP client - src/test/resources/xml contains 
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
1. Start Wildfly in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy -Pwildfly

3. Submit two webservice requests to invoke the SOAP gateway.  There are a number of ways to do this :
- Submit the requests with your preferred SOAP client - src/test/resources/xml contains 
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

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-bus-cdi quickstart :

karaf@root> features:install switchyard-quickstart-camel-bus-cdi

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-bus-cdi



Expected Output
===============
```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <orders:orderAck xmlns:orders="urn:switchyard-quickstart:cdi-bus:1.0">
         <orderId>PO-19838-XYZ</orderId>
         <accepted>true</accepted>
         <status>Order Accepted</status>
      </orders:orderAck>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <orders:orderAck xmlns:orders="urn:switchyard-quickstart:cdi-bus:1.0">
         <orderId>PO-19838-XYZ</orderId>
         <accepted>true</accepted>
         <status>Order Accepted, after delay</status>
      </orders:orderAck>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

In console you should see following output  
```
    [SimpleAuditor] Step DOMAIN_HANDLERS took 0ms  
    [SimpleAuditor] Step ADDRESSING took 0ms  
    [SimpleAuditor] Step TRANSACTION_HANDLER took 0ms  
    [SimpleAuditor] Step GENERIC_POLICY took 0ms  
    [SimpleAuditor] Step VALIDATION took 0ms  
    [SimpleAuditor] Step TRANSFORMATION took 0ms  
    [SimpleAuditor] Step VALIDATION took 0ms  
    [SelectiveAuditor] Calling provider with message Order[orderId=null, itemId=null, quantity=0]  
    [SimpleAuditor] Step PROVIDER_CALLBACK took 1001ms  
    [SelectiveAuditor] Provider respond with message OrderAck [orderId=null, accepted=true, status=Order Accepted, after delay]  
    [SimpleAuditor] Step TRANSACTION_HANDLER took 0ms  
    [SimpleAuditor] Step DOMAIN_HANDLERS took 0ms  
    [SimpleAuditor] Step VALIDATION took 0ms  
    [SimpleAuditor] Step TRANSFORMATION took 0ms  
    [SimpleAuditor] Step VALIDATION took 0ms  
    [SimpleAuditor] Step CONSUMER_CALLBACK took 1ms  
    [CountingProcessor] Route node filter[simple{${property.SwitchYardExchange.contract.consumerOperation.exchangePattern} == 'IN_OUT'}] took 14 ms
```

