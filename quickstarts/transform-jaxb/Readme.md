Introduction
============
This quickstart demonstrates the usage of the JAXB transformer.   No special configuration is 
included here for the transformer configuration - because a CDI Bean Service is used here and 
JAXB types are used in the Service interface, the transformation is automatic. 

![Transform JAXB Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/transform-jaxb/transform-jaxb.jpg)


Preqrequisites 
==============
Maven

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Submit a request with your preferred SOAP client - src/test/resources/xml contains sample 
   requests and the responses that you should see
   Alternatively use the simple bundled SOAP client and the sample request XML e.g.

        mvn exec:java

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy,wildfly

3. Submit a request with your preferred SOAP client - src/test/resources/xml contains sample 
requests and the responses that you should see
Alternatively use the simple bundled SOAP client and the sample request XML e.g.

        mvn exec:java

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the transform-jaxb quickstart :

karaf@root> features:install switchyard-quickstart-transform-jaxb

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-transform-jaxb



Expected Output:
================
```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <ns2:orderAck xmlns:ns2="urn:switchyard-quickstart:transform-jaxb:1.0">
         <orderId>PO-19838-XYZ</orderId>
         <accepted>true</accepted>
         <status>Order Accepted</status>
      </ns2:orderAck>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```


## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)
2. [JAXB Transformer Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JAXB+Transformer)
