Introduction
============
This quickstart demonstrates the usage of the XSLT transformer.  The service contained in this 
quickstart is a dummy service (OrderService) which simply returns the order object so that it can 
be processed by the transformer.  The object is then transformed from an Order object to an 
OrderAck object using XSLT.  

![Transform XSLT Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/transform-xslt/transform-xslt.jpg)

This example is invoked through a SOAP gateway binding.  

Running the quickstart
======================

EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart : 

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
      as an example of a sample request.   Check the "Expected Output" section for the expected results. 

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart : 

        mvn install -Pdeploy -Pwildfly

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
as an example of a sample request.   Check the "Expected Output" section for the expected results. 

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the transform-xslt quickstart :

karaf@root> features:install switchyard-quickstart-transform-xslt

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-transform-xslt



Expected Output
===============
```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <orders:orderAck xmlns:orders="urn:switchyard-quickstart:transform-xslt:1.0">
         <orderId>PO-19838-XYZ</orderId>
         <accepted>true</accepted>
         <status>Order Accepted</status>
      </orders:orderAck>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

Notes
==============
If you are using JDK 7, you may come across this issue when running this quickstart :

https://bugs.openjdk.java.net/browse/JDK-8016153


## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)
2. [XSLT Transformer Documentation](https://docs.jboss.org/author/display/SWITCHYARD/XSLT+Transformer)
